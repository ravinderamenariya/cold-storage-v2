package com.example.data.ai

import android.util.Log
import com.example.data.model.Crate
import com.example.util.IndianLanguage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "GeminiAiService"

data class GeminiMarketPrediction(
    val aiAnalysis: String,
    val recommendedSellingWindow: String,
    val expectedPriceTrend: String,
    val storageAdjustmentAdvice: String,
    val spoilageRiskLevel: String,
    val isAiGenerated: Boolean = true
)

/**
 * Service integrating Gemini 3.8 Flash via Firebase AI Logic
 * for real-time agricultural cold storage intelligence, mandi pricing advisory,
 * and multi-language voice assistant answers.
 */
class GeminiAiService {

    // General-use conversational model for voice assistant
    private val assistantModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    // Schema for structured cold storage agronomic prediction
    private val predictionSchema = Schema.obj(
        properties = mapOf(
            "aiAnalysis" to Schema.string("Agronomic evaluation of crop condition, degradation rate, and shelf life in current storage"),
            "recommendedSellingWindow" to Schema.string("Ideal timeframe to take produce to APMC mandi for peak profit, e.g. Next 3 to 5 days"),
            "expectedPriceTrend" to Schema.string("Forecast of price movement in the selected local mandi (bullish/bearish/stable)"),
            "storageAdjustmentAdvice" to Schema.string("Actionable recommendation for temperature, humidity, or ventilation to prevent loss"),
            "spoilageRiskLevel" to Schema.string("Risk classification: Low, Moderate, or High")
        )
    )

    private val predictionConfig = generationConfig {
        responseMimeType = "application/json"
        responseSchema = predictionSchema
    }

    // Structured JSON model for market & shelf-life predictions
    private val predictionModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = predictionConfig
        )
    }

    /**
     * Answers farmer questions using Gemini 3.8 Flash with live cold storage telemetry and local context.
     */
    suspend fun askAssistant(
        userQuery: String,
        language: IndianLanguage,
        c1Temp: Double,
        c2Temp: Double,
        humidityC1: Double,
        humidityC2: Double,
        outsideTemp: Double,
        batteryPercent: Int,
        solarWatts: Double,
        gasStatus: String,
        activeCrates: List<Crate>,
        nearestMandi: String
    ): String {
        return try {
            val crateSummary = if (activeCrates.isEmpty()) {
                "No crates currently stored"
            } else {
                activeCrates.take(4).joinToString(", ") { "${it.product} in ${it.chamber} (${it.quantity.toInt()} ${it.unit})" }
            }

            val prompt = """
                You are VISION X Kisan Mitra, an intelligent AI Assistant embedded inside a Smart Solar Cold Storage unit in India.
                
                REAL-TIME STORAGE SENSOR TELEMETRY:
                - Chamber 1 (Vegetables/Temperate): $c1Temp°C, $humidityC1% RH (Target: 12°C)
                - Chamber 2 (Fruits/Cold): $c2Temp°C, $humidityC2% RH (Target: 4°C)
                - Ambient Outside Weather: $outsideTemp°C
                - Power & Solar: Battery $batteryPercent%, Solar Panels Generating ${solarWatts.toInt()} W
                - Air Quality & Gas Safety: $gasStatus
                - Produce in Cold Storage: $crateSummary
                - Local Market / APMC Mandi: $nearestMandi
                
                USER QUESTION:
                "$userQuery"
                
                INSTRUCTIONS:
                1. Answer directly and concisely (2 to 4 short sentences max).
                2. Respond in the requested language: ${language.displayName} (${language.nativeName}).
                3. If the language is Hindi or an Indian regional language, use simple, friendly farmer vocabulary (oral conversational style).
                4. Incorporate the relevant real telemetry data (temperatures, battery, produce, mandi) if applicable to the question.
                5. Do NOT include markdown asterisks or bullet points so it reads aloud smoothly via Text-To-Speech.
            """.trimIndent()

            withTimeout(30_000L) {
                val response = assistantModel.generateContent(prompt)
                val text = response.text?.trim()
                if (!text.isNullOrBlank()) {
                    text
                } else {
                    getFallbackAnswer(userQuery, language, c1Temp, c2Temp, batteryPercent, solarWatts)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: TimeoutCancellationException) {
            Log.w(TAG, "Gemini assistant call timed out, falling back to local engine", e)
            getFallbackAnswer(userQuery, language, c1Temp, c2Temp, batteryPercent, solarWatts)
        } catch (e: Exception) {
            Log.w(TAG, "Gemini assistant query failed: ${e.message}, using local fallback", e)
            getFallbackAnswer(userQuery, language, c1Temp, c2Temp, batteryPercent, solarWatts)
        }
    }

    /**
     * Generates structured AI shelf-life and mandi market prediction via Gemini 3.8 Flash.
     */
    suspend fun predictMarketAndShelfLife(
        product: String,
        harvestDate: String,
        chamber: String,
        currentTemp: Double,
        currentHumidity: Double,
        optimalTemp: Double,
        optimalHumidity: Double,
        baseDays: Int,
        nearestMandi: String,
        currentPrice: Double,
        priceUnit: String
    ): GeminiMarketPrediction {
        return try {
            val prompt = """
                Analyze the following agricultural produce stored in an IoT Cold Storage unit:
                - Crop: $product
                - Harvest Date: $harvestDate
                - Storage Chamber: $chamber (Current: $currentTemp°C, $currentHumidity% RH)
                - Recommended Biology: Optimal $optimalTemp°C, $optimalHumidity% RH, Base Shelf Life: $baseDays days
                - Local APMC Mandi: $nearestMandi
                - Current Wholesale Price: ₹$currentPrice per $priceUnit
                
                Provide an expert agronomic assessment including shelf life viability, optimal mandi selling window, price trajectory, and storage adjustments.
            """.trimIndent()

            withTimeout(30_000L) {
                val response = predictionModel.generateContent(prompt)
                val jsonStr = response.text ?: throw IllegalStateException("Empty model response")
                val json = JSONObject(jsonStr)

                GeminiMarketPrediction(
                    aiAnalysis = json.optString("aiAnalysis", "Crop freshness is well-maintained under current refrigeration conditions."),
                    recommendedSellingWindow = json.optString("recommendedSellingWindow", "Sell within next 4–7 days"),
                    expectedPriceTrend = json.optString("expectedPriceTrend", "Price is anticipated to stay firm in $nearestMandi"),
                    storageAdjustmentAdvice = json.optString("storageAdjustmentAdvice", "Maintain stable cooling and avoid frequent door openings."),
                    spoilageRiskLevel = json.optString("spoilageRiskLevel", "Low"),
                    isAiGenerated = true
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Gemini structured prediction failed: ${e.message}. Using agronomic model fallback.", e)
            // Fallback rule-based result
            val tempDelta = currentTemp - optimalTemp
            val risk = when {
                tempDelta > 4.0 -> "High"
                tempDelta > 1.5 -> "Moderate"
                else -> "Low"
            }
            GeminiMarketPrediction(
                aiAnalysis = "Agronomic calculation shows $product respiration rate is stabilized. Storage at $currentTemp°C provides dependable freshness.",
                recommendedSellingWindow = "Optimal selling window: Next 5 to 8 days in $nearestMandi.",
                expectedPriceTrend = "Moderate demand with stable price trends observed across regional APMC mandis.",
                storageAdjustmentAdvice = if (tempDelta > 1.5) "Gradually adjust chamber setpoint closer to $optimalTemp°C to maximize storage duration." else "Conditions are optimal.",
                spoilageRiskLevel = risk,
                isAiGenerated = false
            )
        }
    }

    private fun getFallbackAnswer(
        query: String,
        language: IndianLanguage,
        c1Temp: Double,
        c2Temp: Double,
        batteryPercent: Int,
        solarWatts: Double
    ): String {
        return when (language) {
            IndianLanguage.HINDI -> "कमरा 1 का तापमान $c1Temp डिग्री और कमरा 2 का तापमान $c2Temp डिग्री है। बैटरी $batteryPercent प्रतिशत चार्ज है और सोलर से ${solarWatts.toInt()} वाट बिजली मिल रही है। सभी सिस्टम सामान्य रूप से चल रहे हैं।"
            IndianLanguage.GUJARATI -> "રૂમ 1 નું તાપમાન $c1Temp ડિગ્રી અને રૂમ 2 નું તાપમાન $c2Temp ડિગ્રી છે. બેટરી $batteryPercent ટકા છે અને સોલર કામ કરી રહ્યું છે."
            IndianLanguage.MARATHI -> "खोली 1 चे तापमान $c1Temp अंश आणि खोली 2 चे तापमान $c2Temp अंश आहे. बॅटरी $batteryPercent टक्के आहे आणि सर्व यंत्रणा सुरक्षित आहे."
            IndianLanguage.PUNJABI -> "ਕਮਰੇ 1 ਦਾ ਤਾਪਮਾਨ $c1Temp ਡਿਗਰੀ ਅਤੇ ਕਮਰੇ 2 ਦਾ $c2Temp ਡਿਗਰੀ ਹੈ। ਬੈਟਰੀ $batteryPercent ਪ੍ਰਤੀਸ਼ਤ ਹੈ।"
            else -> "Chamber 1 is at $c1Temp°C and Chamber 2 is at $c2Temp°C. Battery is at $batteryPercent% with ${solarWatts.toInt()}W solar generation. All systems are operating normally."
        }
    }
}
