package com.example.data.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class MandiLocation(
    val id: String,
    val name: String,
    val city: String,
    val state: String,
    val latitude: Double,
    val longitude: Double,
    val specialtyProduce: String,
    val priceMultiplier: Float,
    val address: String
)

object MandiCatalog {
    val allMandis = listOf(
        MandiLocation(
            id = "mandi_udaipur_savina",
            name = "Krishi Upaj Mandi Samiti, Savina, Udaipur",
            city = "Udaipur",
            state = "Rajasthan",
            latitude = 24.5518,
            longitude = 73.7259,
            specialtyProduce = "Tomato, Onion, Garlic, Green Chilli, Seasonal F&V",
            priceMultiplier = 0.96f,
            address = "Krishi Upaj Mandi Samiti, Savina Sector 9, Udaipur, Rajasthan 313002"
        ),
        MandiLocation(
            id = "mandi_udaipur_pratapnagar",
            name = "Pratapnagar Sub-Mandi, Udaipur",
            city = "Udaipur",
            state = "Rajasthan",
            latitude = 24.5942,
            longitude = 73.7441,
            specialtyProduce = "Vegetables, Grain, Fodder, Local Produce",
            priceMultiplier = 0.95f,
            address = "Pratap Nagar Bypass, National Highway 27, Udaipur, Rajasthan"
        ),
        MandiLocation(
            id = "mandi_fatehnagar",
            name = "Fatehnagar Krishi Upaj Mandi",
            city = "Fatehnagar",
            state = "Rajasthan",
            latitude = 24.8631,
            longitude = 74.0722,
            specialtyProduce = "Garlic, Maize, Wheat, Mustard, Onion",
            priceMultiplier = 0.94f,
            address = "APMC Mandi Yard, Fatehnagar, Mavli Tehsil, Udaipur, Rajasthan"
        ),
        MandiLocation(
            id = "mandi_mavli",
            name = "Mavli Krishi Upaj Mandi",
            city = "Mavli",
            state = "Rajasthan",
            latitude = 24.7891,
            longitude = 73.9875,
            specialtyProduce = "Maize, Mustard, Garlic, Gram, Tomato",
            priceMultiplier = 0.94f,
            address = "Railway Station Road, Mavli, Udaipur District, Rajasthan"
        ),
        MandiLocation(
            id = "mandi_dabok_vallabhnagar",
            name = "Dabok / Vallabhnagar Market Hub",
            city = "Dabok",
            state = "Rajasthan",
            latitude = 24.6186,
            longitude = 73.8821,
            specialtyProduce = "Tomato, Chilli, Seasonal Vegetables, Agri Hub",
            priceMultiplier = 0.95f,
            address = "Near Maharana Pratap Airport / Vallabhnagar Road, Dabok, Udaipur"
        ),
        MandiLocation(
            id = "mandi_nathdwara_rajsamand",
            name = "Nathdwara & Rajsamand Krishi Mandi",
            city = "Nathdwara",
            state = "Rajasthan",
            latitude = 24.9312,
            longitude = 73.8219,
            specialtyProduce = "Vegetables, Flowers, Marigold, Rose, Mustard",
            priceMultiplier = 0.97f,
            address = "NH-58 Market Yard, Nathdwara, Rajsamand District, Rajasthan"
        ),
        MandiLocation(
            id = "mandi_chittorgarh_barisadri",
            name = "Chittorgarh & Bari Sadri Krishi Upaj Mandi",
            city = "Chittorgarh",
            state = "Rajasthan",
            latitude = 24.8887,
            longitude = 74.6269,
            specialtyProduce = "Garlic, Opium Seeds, Mustard, Wheat, Coriander, Chilli",
            priceMultiplier = 0.95f,
            address = "Chanderiya APMC Mandi Complex, Chittorgarh, Mewar Region, Rajasthan"
        ),
        MandiLocation(
            id = "mandi_jaipur",
            name = "Muhana Terminal Mandi",
            city = "Jaipur",
            state = "Rajasthan",
            latitude = 26.7865,
            longitude = 75.7622,
            specialtyProduce = "Onion, Mustard, Pomegranate, Tomato",
            priceMultiplier = 0.96f,
            address = "Muhana Mandi Rd, Sanganer, Jaipur, Rajasthan"
        ),
        MandiLocation(
            id = "mandi_delhi_azadpur",
            name = "Azadpur APMC Mandi",
            city = "New Delhi",
            state = "Delhi NCR",
            latitude = 28.7156,
            longitude = 77.1788,
            specialtyProduce = "All India Vegetables, Apple, Mango",
            priceMultiplier = 1.08f,
            address = "Azadpur Wholesale Fruit & Veg Market, GT Karnal Rd, Delhi"
        ),
        MandiLocation(
            id = "mandi_nashik_lasalgaon",
            name = "Lasalgaon & Nashik APMC",
            city = "Nashik",
            state = "Maharashtra",
            latitude = 20.1472,
            longitude = 74.2255,
            specialtyProduce = "Onion, Grapes, Pomegranate, Tomato",
            priceMultiplier = 0.95f,
            address = "APMC Market Yard, Lasalgaon, Nashik, Maharashtra"
        ),
        MandiLocation(
            id = "mandi_mumbai_vashi",
            name = "Vashi APMC Wholesale Market",
            city = "Navi Mumbai",
            state = "Maharashtra",
            latitude = 19.0760,
            longitude = 73.0033,
            specialtyProduce = "Alphonso Mango, Exotic Fruits, Vegetables",
            priceMultiplier = 1.10f,
            address = "APMC Complex, Sector 19, Vashi, Navi Mumbai"
        ),
        MandiLocation(
            id = "mandi_agra",
            name = "Agra APMC Mandi",
            city = "Agra",
            state = "Uttar Pradesh",
            latitude = 27.1767,
            longitude = 78.0081,
            specialtyProduce = "Potato, Mustard, Cauliflower",
            priceMultiplier = 0.92f,
            address = "Kuberpur Wholesale Mandi Yard, Agra, Uttar Pradesh"
        ),
        MandiLocation(
            id = "mandi_srinagar",
            name = "Parimpora Fruit Mandi",
            city = "Srinagar",
            state = "Jammu & Kashmir",
            latitude = 34.0837,
            longitude = 74.7973,
            specialtyProduce = "Kashmir Apple, Walnut, Cherry, Pear",
            priceMultiplier = 1.12f,
            address = "Parimpora Fruit Market, Bypass Road, Srinagar"
        ),
        MandiLocation(
            id = "mandi_ludhiana",
            name = "Ludhiana New Grain & Veg Mandi",
            city = "Ludhiana",
            state = "Punjab",
            latitude = 30.9010,
            longitude = 75.8573,
            specialtyProduce = "Potato, Wheat, Kinnow, Green Veg",
            priceMultiplier = 0.97f,
            address = "Gill Road APMC Yard, Ludhiana, Punjab"
        ),
        MandiLocation(
            id = "mandi_nagpur",
            name = "Nagpur Cotton & Orange Market (Kalmeshwar)",
            city = "Nagpur",
            state = "Maharashtra",
            latitude = 21.1458,
            longitude = 79.0882,
            specialtyProduce = "Nagpur Orange, Sweet Lime, Chilli",
            priceMultiplier = 0.90f,
            address = "APMC Yard, Kalmeshwar Road, Nagpur"
        ),
        MandiLocation(
            id = "mandi_bangalore",
            name = "Yeshwanthpur APMC Yard",
            city = "Bengaluru",
            state = "Karnataka",
            latitude = 13.0280,
            longitude = 77.5408,
            specialtyProduce = "Tomato, Onion, Flower, Coconut",
            priceMultiplier = 1.05f,
            address = "Subramanyanagar, Yeshwanthpur, Bengaluru"
        ),
        MandiLocation(
            id = "mandi_chennai",
            name = "Koyambedu Wholesale Market",
            city = "Chennai",
            state = "Tamil Nadu",
            latitude = 13.0694,
            longitude = 80.1948,
            specialtyProduce = "Banana, Mango, Cut Flowers, Green Veg",
            priceMultiplier = 1.03f,
            address = "Poonamallee High Rd, Koyambedu, Chennai"
        ),
        MandiLocation(
            id = "mandi_indore",
            name = "Choithram APMC Mandi",
            city = "Indore",
            state = "Madhya Pradesh",
            latitude = 22.6841,
            longitude = 75.8456,
            specialtyProduce = "Potato, Garlic, Soybean, Wheat",
            priceMultiplier = 0.93f,
            address = "Choithram Square, Manik Bagh, Indore"
        ),
        MandiLocation(
            id = "mandi_jaipur",
            name = "Muhana Terminal Mandi",
            city = "Jaipur",
            state = "Rajasthan",
            latitude = 26.7865,
            longitude = 75.7622,
            specialtyProduce = "Onion, Mustard, Pomegranate, Tomato",
            priceMultiplier = 0.96f,
            address = "Muhana Mandi Rd, Sanganer, Jaipur, Rajasthan"
        )
    )

    fun findNearestMandi(lat: Double, lng: Double): MandiLocation {
        return allMandis.minByOrNull { calculateDistanceKm(lat, lng, it.latitude, it.longitude) }
            ?: allMandis.first()
    }

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Radius of the Earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c * 10).toInt() / 10.0
    }
}
