package com.rohit.savingsgoals.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DeadlineFormat {

    /** Short chip label for a goal card, e.g. "2 weeks", "12 days", "Nov 2026". */
    fun chipLabel(targetDateMillis: Long?): String? {
        if (targetDateMillis == null) return null
        val days = daysUntil(targetDateMillis)
        return when {
            days < 0 -> "Overdue"
            days == 0L -> "Today"
            days <= 13 -> "$days day${if (days == 1L) "" else "s"}"
            days <= 60 -> "${days / 7} week${if (days / 7 == 1L) "" else "s"}"
            else -> SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(targetDateMillis))
        }
    }

    /** Compact value for the "TIME LEFT" stat card on the detail screen, e.g. "12 Days". */
    fun timeLeftStat(targetDateMillis: Long?): String {
        if (targetDateMillis == null) return "—"
        val days = daysUntil(targetDateMillis)
        return when {
            days < 0 -> "Overdue"
            days == 0L -> "Today"
            days <= 60 -> "$days Day${if (days == 1L) "" else "s"}"
            else -> "${days / 30} Month${if (days / 30 == 1L) "" else "s"}"
        }
    }

    fun daysUntil(targetDateMillis: Long): Long {
        val now = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val target = Calendar.getInstance().apply {
            timeInMillis = targetDateMillis
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(target - now)
    }

    fun formatFull(millis: Long): String =
        SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(millis))
}
