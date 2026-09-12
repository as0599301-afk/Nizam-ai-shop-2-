package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.SampleShopData
import com.example.ui.AppMode
import com.example.ui.NizamShopViewModel
import com.example.ui.OwnerScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NizamShopMusicAndStoreRobolectricTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var app: Application
    private lateinit var viewModel: NizamShopViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        app = ApplicationProvider.getApplicationContext()
        viewModel = NizamShopViewModel(app)
    }

    @After
    fun tearDown() {
        viewModel.stopAnnouncement()
        Dispatchers.resetMain()
    }

    @Test
    fun testAnnouncementBroadcastAndStop() {
        viewModel.broadcastAnnouncement("Attention valued customers, store closes in 15 minutes.")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue("Announcement should be speaking", viewModel.announcementState.value.isSpeaking)
        assertEquals("Attention valued customers, store closes in 15 minutes.", viewModel.announcementState.value.activeAnnouncementText)

        viewModel.stopAnnouncement()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse("Announcement should be stopped", viewModel.announcementState.value.isSpeaking)
    }

    @Test
    fun testProductModelHasPriceSizeColorStock() {
        val products = viewModel.products.value
        assertTrue("Products list must not be empty", products.isNotEmpty())

        for (product in products) {
            assertTrue("Product must have non-empty name", product.name.isNotBlank())
            assertTrue("Product price must be positive", product.price > 0)
            assertTrue("Product size must be non-empty", product.size.isNotBlank())
            assertTrue("Product color must be non-empty", product.color.isNotBlank())
            assertTrue("Product stock must be >= 0", product.stock >= 0)
        }

        // Test Restock
        val firstProduct = products.first()
        val initialStock = firstProduct.stock
        viewModel.addStock(firstProduct.id, 10)
        val updatedProduct = viewModel.products.value.first { it.id == firstProduct.id }
        assertEquals(initialStock + 10, updatedProduct.stock)
    }

    @Test
    fun testAllButtonsAndNavigationFunctional() {
        // Test navigation between screens: Announce, Inventory, Orders, Settings, and Back to Dashboard
        viewModel.navigateTo(OwnerScreen.ANNOUNCEMENTS)
        assertEquals(OwnerScreen.ANNOUNCEMENTS, viewModel.currentScreen.value)

        viewModel.navigateTo(OwnerScreen.INVENTORY)
        assertEquals(OwnerScreen.INVENTORY, viewModel.currentScreen.value)

        viewModel.navigateTo(OwnerScreen.ORDERS)
        assertEquals(OwnerScreen.ORDERS, viewModel.currentScreen.value)

        viewModel.navigateTo(OwnerScreen.SETTINGS)
        assertEquals(OwnerScreen.SETTINGS, viewModel.currentScreen.value)

        // Back to Dashboard button action
        viewModel.navigateTo(OwnerScreen.DASHBOARD)
        assertEquals(OwnerScreen.DASHBOARD, viewModel.currentScreen.value)

        // Test Mode Toggle
        assertEquals(AppMode.OWNER, viewModel.appMode.value)
        viewModel.setAppMode(AppMode.CUSTOMER)
        assertEquals(AppMode.CUSTOMER, viewModel.appMode.value)

        viewModel.setAppMode(AppMode.OWNER)
        assertEquals(AppMode.OWNER, viewModel.appMode.value)
    }

    @Test
    fun testCustomerOrderPlacement() {
        val initialOrderCount = viewModel.orders.value.size
        viewModel.placeCustomerOrder(itemsCount = 3, total = 42.50)

        assertEquals(initialOrderCount + 1, viewModel.orders.value.size)
        assertEquals(42.50, viewModel.orders.value.first().total, 0.001)
    }
}
