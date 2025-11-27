package com.example.healthreminder.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    // Date Formats
    private const val DATE_FORMAT_DISPLAY = "EEEE, MMM dd, yyyy"
    private const val DATE_FORMAT_STORAGE = "yyyy-MM-dd"
    private const val TIME_FORMAT_12HR = "hh:mm a"
    private const val TIME_FORMAT_24HR = "HH:mm"
    private const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_FORMAT_SHORT = "MMM dd"

    /**
     * Get current date in storage format (yyyy-MM-dd)
     */
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT_STORAGE, Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * Get current date in display format (Monday, Nov 21, 2025)
     */
    fun getCurrentDateDisplay(): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * Convert Date to storage format string
     */
    fun dateToStorageString(date: Date): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT_STORAGE, Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * Convert Date to display format string
     */
    fun dateToDisplayString(date: Date): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * Convert Date to short format (Nov 21)
     */
    fun dateToShortString(date: Date): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT_SHORT, Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * Convert string to Date
     */
    fun stringToDate(dateString: String, format: String = DATE_FORMAT_STORAGE): Date? {
        return try {
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Convert time string to 12-hour format
     */
    fun timeTo12HourFormat(time24: String): String {
        return try {
            val inputFormat = SimpleDateFormat(TIME_FORMAT_24HR, Locale.getDefault())
            val outputFormat = SimpleDateFormat(TIME_FORMAT_12HR, Locale.getDefault())
            val date = inputFormat.parse(time24)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            time24
        }
    }

    /**
     * Convert Firebase Timestamp to Date
     */
    fun timestampToDate(timestamp: Timestamp): Date {
        return timestamp.toDate()
    }

    /**
     * Convert Firebase Timestamp to display string
     */
    fun timestampToDisplayString(timestamp: Timestamp): String {
        return dateToDisplayString(timestamp.toDate())
    }

    /**
     * Get day of week from date
     */
    fun getDayOfWeek(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            Calendar.SUNDAY -> "Sun"
            else -> ""
        }
    }

    /**
     * Check if date is today
     */
    fun isToday(date: Date): Boolean {
        val today = dateToStorageString(Date())
        val checkDate = dateToStorageString(date)
        return today == checkDate
    }

    /**
     * Check if date is in the past
     */
    fun isPast(date: Date): Boolean {
        return date.before(Date())
    }

    /**
     * Get days between two dates
     */
    fun daysBetween(startDate: Date, endDate: Date): Int {
        val diff = endDate.time - startDate.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    /**
     * Add days to date
     */
    fun addDays(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return calendar.time
    }

    /**
     * Get time in milliseconds for alarm
     */
    fun getTimeInMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // If time has passed, schedule for next day
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.timeInMillis
    }

    /**
     * Parse time string (HH:mm) to hour and minute
     */
    fun parseTime(timeString: String): Pair<Int, Int> {
        return try {
            val parts = timeString.split(":")
            Pair(parts[0].toInt(), parts[1].toInt())
        } catch (e: Exception) {
            Pair(0, 0)
        }
    }

    /**
     * Get relative time string (e.g., "2 hours ago")
     */
    fun getRelativeTimeString(date: Date): String {
        val now = Date()
        val diff = now.time - date.time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
            hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
            else -> "Just now"
        }
    }

    /**
     * Format timestamp for display
     */
    fun formatTimestamp(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        return if (isToday(date)) {
            "Today at ${timeTo12HourFormat(SimpleDateFormat(TIME_FORMAT_24HR, Locale.getDefault()).format(date))}"
        } else {
            dateToShortString(date)
        }
    }
}