package com.example.model

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val size: String,
    val color: String,
    val stock: Int,
    val sku: String,
    val description: String = ""
)

data class SaleOrder(
    val id: String,
    val orderId: String,
    val time: String,
    val total: Double,
    val itemsCount: Int,
    val paymentMethod: String
)

data class ShopStats(
    val todaySales: Double = 4280.50,
    val totalOrders: Int = 84,
    val activeShoppers: Int = 19,
    val lowStockAlerts: Int = 3
)

object SampleShopData {
    val initialProducts = listOf(
        Product("p1", "Organic Basmati Royal Rice 5kg", "Grains & Rice", 18.50, "5 kg", "Pearl White", 42, "SKU-BAS-01", "Premium aged long-grain aromatic rice"),
        Product("p2", "Cold-Pressed Extra Virgin Olive Oil 1L", "Oils & Vinegars", 14.99, "1 Litre", "Golden Olive", 18, "SKU-OIL-02", "First cold-pressed Mediterranean olive oil"),
        Product("p3", "Artisan Sourdough Loaf", "Bakery", 4.25, "750g", "Crust Amber", 8, "SKU-BAK-03", "Naturally leavened fresh artisan sourdough"),
        Product("p4", "Cold Brew Single-Origin Arabica 330ml", "Beverages", 3.80, "330 ml", "Dark Roast", 24, "SKU-BEV-04", "16-hour steeped smooth chilled coffee"),
        Product("p5", "Alphonso Mango Organic Preserves 350g", "Spreads & Jams", 6.50, "350g", "Mango Yellow", 15, "SKU-JAM-05", "Rich sun-ripened Alphonso mango fruit spread"),
        Product("p6", "Pure Himalayan Pink Crystal Salt 1kg", "Spices & Seasoning", 5.20, "1 kg", "Rose Pink", 31, "SKU-SLT-06", "Mineral-rich unrefined rock salt"),
        Product("p7", "Handcrafted Lavender Herbal Soap", "Personal Care", 4.50, "125g", "Lavender Violet", 2, "SKU-SOP-07", "Cold-processed botanical essential oil soap"),
        Product("p8", "Single Estate Dark Chocolate 72% 100g", "Snacks & Sweets", 3.95, "100g", "Dark Cocoa", 30, "SKU-CHK-08", "Direct-trade Madagascar origin cacao"),
        Product("p9", "Nizam AI Signature Retail Polo", "Apparel", 24.50, "Size L", "Navy Blue", 14, "SKU-POLO-09", "100% breathable organic combed cotton"),
        Product("p10", "Classic Canvas Shopping Tote", "Accessories", 8.00, "Standard", "Natural Ecru", 50, "SKU-TOTE-10", "Heavy-duty eco-friendly reusable cotton tote")
    )

    val initialOrders = listOf(
        SaleOrder("ord1", "#NZ-9042", "2 mins ago", 37.45, 4, "Contactless NFC"),
        SaleOrder("ord2", "#NZ-9041", "12 mins ago", 82.10, 7, "Credit Card"),
        SaleOrder("ord3", "#NZ-9040", "28 mins ago", 14.99, 1, "Digital UPI / QR"),
        SaleOrder("ord4", "#NZ-9039", "45 mins ago", 54.30, 5, "Cash Counter 1"),
        SaleOrder("ord5", "#NZ-9038", "1 hr ago", 22.80, 3, "Contactless NFC")
    )
}
