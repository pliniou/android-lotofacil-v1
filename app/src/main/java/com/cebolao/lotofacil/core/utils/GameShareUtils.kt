package com.cebolao.lotofacil.core.utils

import com.cebolao.lotofacil.domain.model.LotofacilGame
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utilitário para formatar jogos da Lotofácil para compartilhamento
 * via WhatsApp e outras redes sociais.
 */
object GameShareUtils {

    /**
     * Formata um jogo da Lotofácil para compartilhamento via WhatsApp.
     * Inclui emojis, números formatados em grupos de 5, e estatísticas completas.
     */
    fun formatGameForWhatsApp(game: LotofacilGame): String {
        val sorted = game.numbers.sorted()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))
        val createdAt = dateFormat.format(Date(game.creationTimestamp))

        // Números formatados em 3 linhas de 5
        val numbersFormatted = sorted.chunked(5) { chunk ->
            chunk.joinToString("  ") { n ->
                String.format(Locale.US, "%02d", n)
            }
        }.joinToString("\n")

        return buildString {
            appendLine("🍀 *LOTOFÁCIL — JOGO GERADO* 🍀")
            appendLine("━━━━━━━━━━━━━━━━━━━━")
            appendLine()
            appendLine("🎱 *Números:*")
            appendLine("```")
            appendLine(numbersFormatted)
            appendLine("```")
            appendLine()
            appendLine("📊 *Estatísticas do Jogo:*")
            appendLine("• Soma: *${game.sum}*")
            appendLine("• Pares: *${game.evens}* | Ímpares: *${game.odds}*")
            appendLine("• Primos: *${game.primes}*")
            appendLine("• Moldura: *${game.frame}* | Miolo: *${game.portrait}*")
            appendLine("• Fibonacci: *${game.fibonacci}*")
            appendLine("• Múltiplos de 3: *${game.multiplesOf3}*")
            appendLine()
            appendLine("📅 Gerado em: $createdAt")
            if (game.isPinned) {
                appendLine("📌 Jogo fixado")
            }
            appendLine()
            appendLine("━━━━━━━━━━━━━━━━━━━━")
            append("_Gerado pelo CebolãoGenerator_ 🧅")
        }
    }
}
