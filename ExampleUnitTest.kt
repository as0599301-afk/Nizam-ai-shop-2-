package com.example

import com.example.model.SampleShopData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testProductCatalog() {
        val products = SampleShopData.initialProducts
        assertTrue("Products list should not be empty", products.isNotEmpty())
        assertEquals(10, products.size)

        val basmati = products.first { it.id == "p1" }
        assertEquals("Organic Basmati Royal Rice 5kg", basmati.name)
        assertEquals(18.50, basmati.price, 0.001)
    }

    @Test
    fun testOrderHistory() {
        val orders = SampleShopData.initialOrders
        assertTrue("Orders list should not be empty", orders.isNotEmpty())
        assertEquals(5, orders.size)
    }
}
