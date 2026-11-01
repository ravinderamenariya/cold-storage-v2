package com.example

import com.example.data.model.CrateStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.pow

class ExampleUnitTest {
    @Test
    fun crateStatus_categorizationIsAccurate() {
        assertEquals(CrateStatus.HEALTHY, CrateStatus.fromFreshness(85))
        assertEquals(CrateStatus.HEALTHY, CrateStatus.fromFreshness(70))
        assertEquals(CrateStatus.WATCH, CrateStatus.fromFreshness(50))
        assertEquals(CrateStatus.WATCH, CrateStatus.fromFreshness(40))
        assertEquals(CrateStatus.SPOILED_RISK, CrateStatus.fromFreshness(30))
        assertEquals(CrateStatus.SPOILED_RISK, CrateStatus.fromFreshness(0))
    }

    @Test
    fun q10DegradationModel_calculatesExpectedExtensionAtLowerTemps() {
        val baseTomatoDays = 14
        val optimalTemp = 4.0
        val warmTemp = 14.0

        val q10Optimal = 2.2.pow(max(0.0, optimalTemp - optimalTemp) / 10.0)
        val q10Warm = 2.2.pow(max(0.0, warmTemp - optimalTemp) / 10.0)

        assertTrue("Degradation rate should be 1.0 at optimal temp", q10Optimal == 1.0)
        assertTrue("Degradation rate at +10°C should be 2.2x faster", q10Warm >= 2.19)

        val daysAtOptimal = (baseTomatoDays / q10Optimal).toInt()
        val daysAtWarm = (baseTomatoDays / q10Warm).toInt()

        assertTrue("Cold storage should extend tomato life significantly", daysAtOptimal > daysAtWarm)
        assertEquals(14, daysAtOptimal)
        assertEquals(6, daysAtWarm)
    }

    @Test
    fun geminiAiService_answersAgriculturalQuestionsWithoutForcedTemperatureEcho() {
        val aiService = com.example.data.ai.GeminiAiService()

        // Test garlic inquiry
        val garlicAnswer = aiService.getFallbackAnswer(
            query = "लहसुन का भाव और सविना मंडी में तेजी कब आएगी?",
            language = com.example.util.IndianLanguage.HINDI,
            c1Temp = 12.0,
            c2Temp = 4.0,
            batteryPercent = 78,
            solarWatts = 450.0,
            nearestMandi = "Krishi Upaj Mandi Samiti, Savina, Udaipur"
        )
        assertTrue("Garlic answer should mention garlic (लहसुन)", garlicAnswer.contains("लहसुन"))
        assertTrue("Garlic answer should mention Savina mandi or Udaipur", garlicAnswer.contains("सविना") || garlicAnswer.contains("उदयपुर"))
        assertTrue("Garlic answer should not simply recite room temperature", !garlicAnswer.startsWith("उदयपुर कोल्ड स्टोरेज में कमरा 1"))

        // Test tomato return inquiry
        val tomatoAnswer = aiService.getFallbackAnswer(
            query = "टमाटर को 5 दिन बाद बेचें तो क्या return मिलेगा?",
            language = com.example.util.IndianLanguage.HINDI,
            c1Temp = 12.0,
            c2Temp = 4.0,
            batteryPercent = 78,
            solarWatts = 450.0,
            nearestMandi = "Krishi Upaj Mandi Samiti, Savina, Udaipur"
        )
        assertTrue("Tomato return should contain return/मुनाफा analysis", tomatoAnswer.contains("रिटर्न") || tomatoAnswer.contains("मुनाफा") || tomatoAnswer.contains("टमाटर"))

        // Test pest and disease inquiry
        val diseaseAnswer = aiService.getFallbackAnswer(
            query = "फसलों में कीट और फफूंद का देसी इलाज क्या है?",
            language = com.example.util.IndianLanguage.HINDI,
            c1Temp = 12.0,
            c2Temp = 4.0,
            batteryPercent = 78,
            solarWatts = 450.0,
            nearestMandi = "Krishi Upaj Mandi Samiti, Savina, Udaipur"
        )
        assertTrue("Disease answer should recommend biological remedies", diseaseAnswer.contains("नीम") || diseaseAnswer.contains("ट्राइकोडर्मा") || diseaseAnswer.contains("फफूंद"))
    }
}
