package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.audio.AnnouncementManager
import com.example.model.Product
import com.example.model.SaleOrder
import com.example.model.SampleShopData
import com.example.model.ShopStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AppMode {
    OWNER,
    CUSTOMER
}

enum class OwnerScreen(val title: String) {
    DASHBOARD("Dashboard"),
    ANNOUNCEMENTS("Announcements"),
    INVENTORY("Inventory"),
    ORDERS("Orders & POS"),
    SETTINGS("Settings")
}

class NizamShopViewModel(application: Application) : AndroidViewModel(application) {

    val announcementManager = AnnouncementManager(application.applicationContext)
    val announcementState = announcementManager.announcementState

    private val _appMode = MutableStateFlow(AppMode.OWNER)
    val appMode: StateFlow<AppMode> = _appMode.asStateFlow()

    private val _currentScreen = MutableStateFlow(OwnerScreen.DASHBOARD)
    val currentScreen: StateFlow<OwnerScreen> = _currentScreen.asStateFlow()

    private val _products = MutableStateFlow(SampleShopData.initialProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _orders = MutableStateFlow(SampleShopData.initialOrders)
    val orders: StateFlow<List<SaleOrder>> = _orders.asStateFlow()

    private val _stats = MutableStateFlow(ShopStats())
    val stats: StateFlow<ShopStats> = _stats.asStateFlow()

    fun setAppMode(mode: AppMode) {
        _appMode.value = mode
    }

    fun navigateTo(screen: OwnerScreen) {
        _currentScreen.value = screen
    }

    fun broadcastAnnouncement(text: String) {
        announcementManager.broadcastAnnouncement(text)
    }

    fun stopAnnouncement() {
        announcementManager.stopAnnouncement()
    }

    fun addStock(productId: String, amount: Int) {
        _products.update { list ->
            list.map {
                if (it.id == productId) it.copy(stock = (it.stock + amount).coerceAtLeast(0)) else it
            }
        }
    }

    fun placeCustomerOrder(itemsCount: Int, total: Double) {
        val newOrder = SaleOrder(
            id = "ord_${System.currentTimeMillis()}",
            orderId = "#NZ-${(1000..9999).random()}",
            time = "Just now",
            total = total,
            itemsCount = itemsCount,
            paymentMethod = "App Checkout"
        )
        _orders.update { listOf(newOrder) + it }
        _stats.update {
            it.copy(
                todaySales = it.todaySales + total,
                totalOrders = it.totalOrders + 1
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        announcementManager.shutdown()
    }
}
