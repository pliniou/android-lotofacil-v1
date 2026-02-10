package com.cebolao.lotofacil.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterStateTest {

    @Test
    fun `filter state should correctly report enabled status`() {
        val filterState = FilterState(
            type = FilterType.SOMA_DEZENAS,
            isEnabled = true,
            selectedRange = 170f..220f
        )

        assertTrue(filterState.isEnabled)
        assertEquals(FilterType.SOMA_DEZENAS, filterState.type)
        assertEquals(170f..220f, filterState.selectedRange)
    }

    @Test
    fun `filter state should correctly calculate range percentage`() {
        val filterState = FilterState(
            type = FilterType.SOMA_DEZENAS,
            isEnabled = true,
            selectedRange = 170f..220f
        )

        val rangePercentage = filterState.rangePercentage
        assertTrue(rangePercentage > 0f)
        assertTrue(rangePercentage <= 1f)
    }

    @Test
    fun `disabled filter should have zero range percentage`() {
        val filterState = FilterState(
            type = FilterType.PARES,
            isEnabled = false
        )

        assertFalse(filterState.isEnabled)
        assertEquals(0f, filterState.rangePercentage, 0.001f)
    }

    @Test
    fun `containsValue should return true for values in range`() {
        val filterState = FilterState(
            type = FilterType.SOMA_DEZENAS,
            isEnabled = true,
            selectedRange = 170f..220f
        )

        assertTrue(filterState.containsValue(180))
        assertTrue(filterState.containsValue(200))
        assertTrue(filterState.containsValue(220))
    }

    @Test
    fun `containsValue should return false for values outside range`() {
        val filterState = FilterState(
            type = FilterType.SOMA_DEZENAS,
            isEnabled = true,
            selectedRange = 170f..220f
        )

        assertFalse(filterState.containsValue(169))
        assertFalse(filterState.containsValue(221))
    }

    @Test
    fun `containsValue should return true for disabled filter`() {
        val filterState = FilterState(
            type = FilterType.PARES,
            isEnabled = false
        )

        assertTrue(filterState.containsValue(7))
    }
}
