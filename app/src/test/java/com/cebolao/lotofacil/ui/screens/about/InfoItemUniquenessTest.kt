package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.TipsAndUpdates
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class InfoItemUniquenessTest {

    @Test
    fun `validateUniqueItems should pass with unique items`() {
        val uniqueItems = listOf(
            InfoItem.Tips(Icons.Default.TipsAndUpdates, content = { }),
            InfoItem.Rules(Icons.Default.Gavel, content = { }),
            InfoItem.Bolao(Icons.Default.Group, content = { }),
            InfoItem.Purpose(Icons.Default.Lightbulb, content = { })
        )

        InfoItem.validateUniqueItems(uniqueItems)
    }

    @Test
    fun `validateUniqueItems should detect duplicate titleResId`() {
        val duplicateItems = listOf(
            InfoItem.Tips(Icons.Default.TipsAndUpdates, content = { }),
            InfoItem.Rules(Icons.Default.Gavel, content = { }),
            InfoItem.Tips(Icons.Default.TipsAndUpdates, content = { })
        )

        val exception = assertFailsWith<IllegalStateException> {
            InfoItem.validateUniqueItems(duplicateItems)
        }

        assertTrue(exception.message!!.contains("Duplicate InfoItem titles detected"))
    }

    @Test
    fun `distinctBy titleResId should remove duplicates`() {
        val itemsWithDuplicates = listOf(
            InfoItem.Tips(Icons.Default.TipsAndUpdates, content = { }),
            InfoItem.Rules(Icons.Default.Gavel, content = { }),
            InfoItem.Bolao(Icons.Default.Group, content = { }),
            InfoItem.Purpose(Icons.Default.Lightbulb, content = { }),
            InfoItem.Bolao(Icons.Default.Group, content = { })
        )

        val uniqueItems = itemsWithDuplicates.distinctBy { it.titleResId }

        assertEquals(4, uniqueItems.size)
        assertEquals(uniqueItems.size, uniqueItems.map { it.titleResId }.distinct().size)
    }

    @Test
    fun `InfoItem subclasses should have unique titleResId`() {
        val infoItems = listOf(
            InfoItem.Tips(Icons.Default.TipsAndUpdates, content = { }),
            InfoItem.Rules(Icons.Default.Gavel, content = { }),
            InfoItem.Probabilities(Icons.Default.Calculate, content = { }),
            InfoItem.Bolao(Icons.Default.Group, content = { }),
            InfoItem.Purpose(Icons.Default.Lightbulb, content = { }),
            InfoItem.Legal(Icons.Default.Info, content = { }),
            InfoItem.Privacy(Icons.Default.PrivacyTip, content = { })
        )

        val titleIds = infoItems.map { it.titleResId }
        assertEquals(titleIds.size, titleIds.distinct().size)
    }

    @Test
    fun `InfoItem subclasses should have unique subtitleResId`() {
        val infoItems = listOf(
            InfoItem.Tips(Icons.Default.TipsAndUpdates, content = { }),
            InfoItem.Rules(Icons.Default.Gavel, content = { }),
            InfoItem.Probabilities(Icons.Default.Calculate, content = { }),
            InfoItem.Bolao(Icons.Default.Group, content = { }),
            InfoItem.Purpose(Icons.Default.Lightbulb, content = { }),
            InfoItem.Legal(Icons.Default.Info, content = { }),
            InfoItem.Privacy(Icons.Default.PrivacyTip, content = { })
        )

        val subtitleIds = infoItems.map { it.subtitleResId }
        assertEquals(subtitleIds.size, subtitleIds.distinct().size)
    }

    @Test
    fun `validate empty list should not throw`() {
        InfoItem.validateUniqueItems(emptyList())
    }

    @Test
    fun `validate single item should not throw`() {
        val singleItem = InfoItem.Tips(Icons.Default.TipsAndUpdates, content = { })
        InfoItem.validateUniqueItems(listOf(singleItem))
    }
}
