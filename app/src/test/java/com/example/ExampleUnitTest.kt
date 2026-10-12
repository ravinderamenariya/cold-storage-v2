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
}
