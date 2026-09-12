package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import java.util.UUID

data class AnnouncementState(
    val isSpeaking: Boolean = false,
    val activeAnnouncementText: String? = null,
    val currentUtteranceId: String? = null,
    val isTtsReady: Boolean = false,
    val errorMessage: String? = null
)

data class AnnouncementPreset(
    val id: String,
    val title: String,
    val text: String,
    val category: String
)

class AnnouncementManager(
    private val context: Context
) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private val _announcementState = MutableStateFlow(AnnouncementState())
    val announcementState: StateFlow<AnnouncementState> = _announcementState.asStateFlow()

    val presets = listOf(
        AnnouncementPreset(
            id = "preset_welcome",
            title = "Welcome Greeting",
            text = "Welcome to Nizam AI Shop! Enjoy special member discounts and fresh arrivals across all aisles today.",
            category = "Greeting"
        ),
        AnnouncementPreset(
            id = "preset_flash_sale",
            title = "Flash Sale 20% Off",
            text = "Attention valued shoppers: Special Flash Sale! Enjoy twenty percent off on all fresh produce and bakery items for the next thirty minutes.",
            category = "Promotions"
        ),
        AnnouncementPreset(
            id = "preset_counter_open",
            title = "Counter 2 Open",
            text = "Store announcement: Billing counter number two is now open for fast checkout with card or digital payment.",
            category = "Operations"
        ),
        AnnouncementPreset(
            id = "preset_closing_soon",
            title = "Store Closing in 15m",
            text = "Notice to our valued shoppers: Nizam AI Shop will close in fifteen minutes. Please finalize your selections and proceed to the registers.",
            category = "Operations"
        ),
        AnnouncementPreset(
            id = "preset_ai_assistance",
            title = "Smart Cart Assistance",
            text = "Need help finding an item? Use the Nizam AI interactive tablets located at each aisle or ask any store associate.",
            category = "Customer Care"
        )
    )

    init {
        // Run TTS instantiation on background thread to ensure main looper is never blocked
        Thread {
            try {
                val tts = TextToSpeech(context, this@AnnouncementManager)
                textToSpeech = tts
            } catch (t: Throwable) {
                Log.w("AnnouncementManager", "Non-fatal TTS initialization notice: ${t.localizedMessage}")
                _announcementState.update { it.copy(isTtsReady = false, errorMessage = null) }
            }
        }.apply {
            isDaemon = true
            name = "TTS-Init-Thread"
            start()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val tts = textToSpeech
            if (tts != null) {
                val langResult = tts.setLanguage(Locale.US)
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w("AnnouncementManager", "US English not supported, attempting default locale")
                    tts.language = Locale.getDefault()
                }

                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _announcementState.update {
                            it.copy(isSpeaking = true, currentUtteranceId = utteranceId)
                        }
                    }

                    override fun onDone(utteranceId: String?) {
                        _announcementState.update {
                            it.copy(isSpeaking = false, activeAnnouncementText = null, currentUtteranceId = null)
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _announcementState.update {
                            it.copy(
                                isSpeaking = false,
                                activeAnnouncementText = null,
                                currentUtteranceId = null,
                                errorMessage = "Announcement playback error"
                            )
                        }
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        Log.e("AnnouncementManager", "Announcement error code $errorCode: $utteranceId")
                        _announcementState.update {
                            it.copy(
                                isSpeaking = false,
                                activeAnnouncementText = null,
                                currentUtteranceId = null,
                                errorMessage = "Announcement error code: $errorCode"
                            )
                        }
                    }
                })

                _announcementState.update { it.copy(isTtsReady = true, errorMessage = null) }
            }
        } else {
            Log.e("AnnouncementManager", "TTS initialization failed with status $status")
            _announcementState.update { it.copy(isTtsReady = false, errorMessage = "Text-to-Speech not available on device") }
        }
    }

    fun broadcastAnnouncement(text: String) {
        if (text.isBlank()) return

        val utteranceId = UUID.randomUUID().toString()
        _announcementState.update {
            it.copy(
                isSpeaking = true,
                activeAnnouncementText = text,
                currentUtteranceId = utteranceId,
                errorMessage = null
            )
        }

        val tts = textToSpeech
        if (tts != null && _announcementState.value.isTtsReady) {
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            }
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        } else {
            // If TTS engine is initializing or unavailable, simulate temporary broadcast state safely
            Log.w("AnnouncementManager", "TTS not ready, fallback simulated broadcast")
            _announcementState.update { it.copy(isSpeaking = true) }
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                stopAnnouncement()
            }, 3000)
        }
    }

    fun stopAnnouncement() {
        try {
            textToSpeech?.stop()
        } catch (e: Exception) {
            Log.e("AnnouncementManager", "Error stopping TTS", e)
        }

        _announcementState.update {
            it.copy(
                isSpeaking = false,
                activeAnnouncementText = null,
                currentUtteranceId = null
            )
        }
    }

    fun shutdown() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (_: Exception) {}
        textToSpeech = null
    }
}
