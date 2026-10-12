package com.example.data.model

/**
 * Category of agricultural crop.
 */
enum class CropCategory(val labelHindi: String, val labelEnglish: String) {
    VEGETABLE("सब्जियां", "Vegetables"),
    FRUIT("फल", "Fruits"),
    FLOWER("फूल", "Flowers")
}

/**
 * Comprehensive Indian Agricultural Produce item with market price tracking & shelf life specs.
 */
data class IndianAgriProduce(
    val id: String,
    val nameEnglish: String,
    val nameHindi: String,
    val emoji: String,
    val category: CropCategory,
    val baseShelfLifeDays: Int,
    val optimalTemp: Double,
    val optimalHumidity: Double,
    val tempTolerance: Double,
    val currentPricePerKgOrUnit: Int, // e.g. ₹40 / kg or ₹15 / bunch
    val unit: String = "kg",
    val majorState: String, // e.g. "Maharashtra", "Punjab", "Himachal Pradesh"
    val priceTrendPercent: Int = 0, // e.g. +15% increase
    val previousPrice: Int = currentPricePerKgOrUnit
)

/**
 * Registry of all fruits, vegetables, and flowers across India.
 */
object IndianAgriMarketCatalog {

    val allProduces: List<IndianAgriProduce> = listOf(
        // ================= VEGETABLES =================
        IndianAgriProduce("veg_tomato", "Tomato", "टमाटर", "🍅", CropCategory.VEGETABLE, 14, 4.0, 75.0, 1.5, 38, "kg", "Maharashtra / Nashik", +18, 32),
        IndianAgriProduce("veg_potato", "Potato", "आलू", "🥔", CropCategory.VEGETABLE, 120, 8.0, 85.0, 2.0, 26, "kg", "Uttar Pradesh / Agra", +8, 24),
        IndianAgriProduce("veg_onion", "Onion", "प्याज", "🧅", CropCategory.VEGETABLE, 90, 2.0, 68.0, 1.5, 42, "kg", "Maharashtra / Lasalgaon", +20, 35),
        IndianAgriProduce("veg_spinach", "Spinach", "पालक", "🥬", CropCategory.VEGETABLE, 6, 2.0, 95.0, 1.0, 25, "kg", "Punjab / Haryana", +12, 22),
        IndianAgriProduce("veg_cauliflower", "Cauliflower", "फूलगोभी", "🥦", CropCategory.VEGETABLE, 20, 1.0, 92.0, 1.0, 35, "kg", "Bihar / UP", +15, 30),
        IndianAgriProduce("veg_cabbage", "Cabbage", "पत्तागोभी", "🥬", CropCategory.VEGETABLE, 30, 1.0, 95.0, 1.0, 22, "kg", "West Bengal", -4, 23),
        IndianAgriProduce("veg_green_chilli", "Green Chilli", "हरी मिर्च", "🌶️", CropCategory.VEGETABLE, 18, 7.0, 85.0, 1.5, 65, "kg", "Andhra Pradesh / Guntur", +22, 53),
        IndianAgriProduce("veg_capsicum", "Capsicum (Shimla Mirch)", "शिमला मिर्च", "🫑", CropCategory.VEGETABLE, 16, 7.0, 90.0, 1.5, 55, "kg", "Himachal Pradesh", +10, 50),
        IndianAgriProduce("veg_ginger", "Ginger", "अदरक", "🫚", CropCategory.VEGETABLE, 60, 12.0, 75.0, 2.0, 110, "kg", "Kerala / Assam", +25, 88),
        IndianAgriProduce("veg_garlic", "Garlic", "लहसुन", "🧄", CropCategory.VEGETABLE, 180, 0.0, 65.0, 1.0, 160, "kg", "Madhya Pradesh / Mandsaur", +30, 123),
        IndianAgriProduce("veg_brinjal", "Brinjal (Eggplant)", "बैंगन", "🍆", CropCategory.VEGETABLE, 12, 10.0, 88.0, 1.5, 28, "kg", "Odisha / Karnataka", +5, 26),
        IndianAgriProduce("veg_okra", "Okra (Bhindi)", "भिंडी", "🥒", CropCategory.VEGETABLE, 8, 9.0, 90.0, 1.5, 45, "kg", "Gujarat", +14, 39),
        IndianAgriProduce("veg_carrot", "Carrot", "गाजर", "🥕", CropCategory.VEGETABLE, 45, 1.0, 95.0, 1.0, 32, "kg", "Haryana / Punjab", +10, 29),
        IndianAgriProduce("veg_peas", "Green Peas", "मटर", "🫛", CropCategory.VEGETABLE, 15, 1.0, 90.0, 1.0, 70, "kg", "Madhya Pradesh", +16, 60),

        // ================= FRUITS =================
        IndianAgriProduce("fruit_mango", "Mango", "आम", "🥭", CropCategory.FRUIT, 21, 12.0, 85.0, 2.0, 95, "kg", "Maharashtra / Ratnagiri", +25, 76),
        IndianAgriProduce("fruit_banana", "Banana", "केला", "🍌", CropCategory.FRUIT, 12, 13.0, 80.0, 1.5, 30, "dozen", "Tamil Nadu / Jalgaon", +7, 28),
        IndianAgriProduce("fruit_apple", "Apple", "सेब", "🍎", CropCategory.FRUIT, 90, 1.0, 90.0, 1.5, 140, "kg", "Kashmir / Himachal", +18, 118),
        IndianAgriProduce("fruit_grapes", "Grapes", "अंगूर", "🍇", CropCategory.FRUIT, 25, 1.0, 90.0, 1.0, 85, "kg", "Maharashtra / Nashik", +15, 73),
        IndianAgriProduce("fruit_pomegranate", "Pomegranate", "अनार", "🫐", CropCategory.FRUIT, 60, 5.0, 85.0, 1.5, 130, "kg", "Maharashtra / Solapur", +12, 116),
        IndianAgriProduce("fruit_orange", "Orange / Kinnow", "संतरा", "🍊", CropCategory.FRUIT, 35, 6.0, 88.0, 1.5, 60, "kg", "Nagpur / Punjab", +10, 54),
        IndianAgriProduce("fruit_guava", "Guava", "अमरूद", "🍈", CropCategory.FRUIT, 15, 8.0, 85.0, 1.5, 45, "kg", "UP / Allahabad", +5, 42),
        IndianAgriProduce("fruit_papaya", "Papaya", "पपीता", "🫐", CropCategory.FRUIT, 14, 11.0, 85.0, 1.5, 35, "kg", "Andhra Pradesh", +8, 32),
        IndianAgriProduce("fruit_strawberry", "Strawberry", "स्ट्रॉबेरी", "🍓", CropCategory.FRUIT, 8, 1.0, 90.0, 1.0, 180, "kg", "Maharashtra / Mahabaleshwar", +20, 150),
        IndianAgriProduce("fruit_litchi", "Litchi", "लीची", "🍒", CropCategory.FRUIT, 15, 3.0, 90.0, 1.0, 120, "kg", "Bihar / Muzaffarpur", +24, 96),
        IndianAgriProduce("fruit_pineapple", "Pineapple", "अनानास", "🍍", CropCategory.FRUIT, 21, 10.0, 85.0, 1.5, 55, "piece", "Assam / Kerala", +6, 51),
        IndianAgriProduce("fruit_watermelon", "Watermelon", "तरबूज", "🍉", CropCategory.FRUIT, 18, 10.0, 80.0, 1.5, 20, "kg", "Karnataka / Rajasthan", +4, 19),

        // ================= FLOWERS =================
        IndianAgriProduce("flw_marigold", "Marigold (Genda)", "गेंदा फूल", "🌼", CropCategory.FLOWER, 6, 4.0, 75.0, 1.0, 75, "kg", "Karnataka / West Bengal", +28, 58),
        IndianAgriProduce("flw_rose", "Rose (Gulab)", "गुलाब", "🌹", CropCategory.FLOWER, 8, 3.0, 85.0, 1.0, 120, "bundle", "Pune / Bangalore", +32, 90),
        IndianAgriProduce("flw_jasmine", "Jasmine (Mogra)", "मोगरा / चमेली", "🌸", CropCategory.FLOWER, 4, 6.0, 80.0, 1.0, 220, "kg", "Tamil Nadu / Madurai", +35, 162),
        IndianAgriProduce("flw_carnation", "Carnation", "कार्नेशन", "💐", CropCategory.FLOWER, 12, 3.0, 85.0, 1.0, 140, "bundle", "Himachal / Ooty", +16, 120),
        IndianAgriProduce("flw_gerbera", "Gerbera", "जरबेरा", "🌺", CropCategory.FLOWER, 10, 4.0, 80.0, 1.0, 90, "bundle", "Maharashtra / Polyhouse", +14, 78),
        IndianAgriProduce("flw_tuberose", "Tuberose (Rajnigandha)", "रजनीगंधा", "🪻", CropCategory.FLOWER, 7, 5.0, 80.0, 1.0, 110, "kg", "West Bengal / Nadia", +19, 92)
    )

    fun findById(id: String): IndianAgriProduce? = allProduces.find { it.id == id }

    fun findByName(name: String): IndianAgriProduce? =
        allProduces.find { it.nameEnglish.equals(name, ignoreCase = true) || it.nameHindi.equals(name, ignoreCase = true) }
            ?: allProduces.firstOrNull()

    fun getPriceHikeItems(): List<IndianAgriProduce> = allProduces.filter { it.priceTrendPercent >= 10 }
}
