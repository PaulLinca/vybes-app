package com.linca.vybes.common.util

import android.text.format.DateUtils
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    fun formatPostedDate(postedDate: ZonedDateTime): String {
        val now = ZonedDateTime.now()
        val timeInMillis = postedDate.toInstant().toEpochMilli()
        val nowInMillis = now.toInstant().toEpochMilli()

        // If it's less than 24 hours ago, show relative time
        return if (now.minusDays(1).isBefore(postedDate)) {
            DateUtils.getRelativeTimeSpanString(
                timeInMillis,
                nowInMillis,
                DateUtils.MINUTE_IN_MILLIS
            ).toString()
        } else {
            // Show formatted date for older posts
            val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault())
            postedDate.format(formatter)
        }
    }
}