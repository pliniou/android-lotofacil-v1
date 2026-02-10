package com.cebolao.lotofacil.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = "com.cebolao.lotofacil"
    ) {
        pressHome()
        startActivityAndWait()
        device.waitForIdle()

        navigateToBottomNav("Gerador")
        navigateToBottomNav("Jogos")
        navigateToBottomNav("Conferidor")
        navigateToBottomNav("Início")

        waitAndClickText("Ver detalhes")
        device.wait(Until.hasObject(By.text("Estatísticas avançadas")), DEFAULT_TIMEOUT_MS)
        device.waitForIdle()
    }

    private fun MacrobenchmarkScope.navigateToBottomNav(label: String) {
        waitAndClickText(label)
    }

    private fun MacrobenchmarkScope.waitAndClickText(text: String) {
        device.wait(Until.hasObject(By.text(text)), DEFAULT_TIMEOUT_MS)
        device.findObject(By.text(text))?.click()
        device.waitForIdle()
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MS = 5_000L
    }
}
