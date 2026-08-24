package com.victorfaria.juntos

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeUtils {
    private const val DAY_MILLIS = 24L * 60 * 60 * 1000

    data class Elapsed(
        val days: Long,
        val hours: Long,
        val minutes: Long,
        val seconds: Long,
        val remainderMillis: Long
    )

    /** Calcula o tempo decorrido entre [targetMillis] e agora ([nowMillis]). */
    fun elapsedSince(targetMillis: Long, nowMillis: Long = System.currentTimeMillis()): Elapsed {
        val total = (nowMillis - targetMillis).coerceAtLeast(0)
        val days = total / DAY_MILLIS
        val remainder = total % DAY_MILLIS
        val hours = remainder / (60 * 60 * 1000)
        val minutes = (remainder / (60 * 1000)) % 60
        val seconds = (remainder / 1000) % 60
        return Elapsed(days, hours, minutes, seconds, remainder)
    }

    /** Próximo instante (em millis de relógio) em que o dia contado vai virar, ou seja, o próximo aniversário. */
    fun nextAnniversaryMillis(targetMillis: Long, nowMillis: Long = System.currentTimeMillis()): Long {
        val elapsed = elapsedSince(targetMillis, nowMillis)
        return nowMillis + (DAY_MILLIS - elapsed.remainderMillis)
    }

    fun formatDaysLabel(days: Long): String {
        return if (days == 1L) "1 dia" else "$days dias"
    }

    fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
        return formatter.format(Date(millis))
    }

    data class Breakdown(val years: Int, val months: Int, val days: Int)

    /**
     * Decompõe o tempo decorrido em anos/meses/dias completos de calendário (como uma idade),
     * "andando" a partir da data alvo até agora.
     */
    fun calendarBreakdown(targetMillis: Long, nowMillis: Long = System.currentTimeMillis()): Breakdown {
        if (nowMillis <= targetMillis) return Breakdown(0, 0, 0)

        val cursor = Calendar.getInstance().apply { timeInMillis = targetMillis }
        var years = 0
        while (true) {
            val next = cursor.clone() as Calendar
            next.add(Calendar.YEAR, 1)
            if (next.timeInMillis > nowMillis) break
            cursor.timeInMillis = next.timeInMillis
            years++
        }
        var months = 0
        while (true) {
            val next = cursor.clone() as Calendar
            next.add(Calendar.MONTH, 1)
            if (next.timeInMillis > nowMillis) break
            cursor.timeInMillis = next.timeInMillis
            months++
        }
        var days = 0
        while (true) {
            val next = cursor.clone() as Calendar
            next.add(Calendar.DAY_OF_MONTH, 1)
            if (next.timeInMillis > nowMillis) break
            cursor.timeInMillis = next.timeInMillis
            days++
        }
        return Breakdown(years, months, days)
    }

    private fun pluralize(value: Int, singular: String, plural: String) =
        if (value == 1) "1 $singular" else "$value $plural"

    /** Ex.: "2 anos, 3 meses, 12 dias" — omite as unidades maiores quando forem zero. */
    fun formatBreakdownLabel(breakdown: Breakdown): String {
        val parts = mutableListOf<String>()
        if (breakdown.years > 0) parts.add(pluralize(breakdown.years, "ano", "anos"))
        if (breakdown.years > 0 || breakdown.months > 0) parts.add(pluralize(breakdown.months, "mês", "meses"))
        parts.add(pluralize(breakdown.days, "dia", "dias"))
        return parts.joinToString(", ")
    }
}
