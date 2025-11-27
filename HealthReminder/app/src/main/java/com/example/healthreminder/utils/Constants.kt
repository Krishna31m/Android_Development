package com.example.healthreminder.utils

object Constants {

    // Firestore Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_MEDICINES = "medicines"
    const val COLLECTION_MEDICINE_HISTORY = "medicineHistory"
    const val COLLECTION_WATER_INTAKE = "waterIntake"
    const val COLLECTION_EXERCISES = "exercises"
    const val COLLECTION_EXERCISE_HISTORY = "exerciseHistory"
    const val COLLECTION_MEALS = "meals"
    const val COLLECTION_MEAL_HISTORY = "mealHistory"
    const val COLLECTION_MOOD = "mood"
    const val COLLECTION_DOCTOR_VISITS = "doctorVisits"
    const val COLLECTION_CHALLENGES = "challenges"
    const val COLLECTION_HEALTH_STATS = "healthStats"

    // SharedPreferences Keys
    const val PREF_NAME = "HealthReminderPrefs"
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_NAME = "user_name"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_IS_LOGGED_IN = "is_logged_in"
    const val PREF_WATER_GOAL = "water_goal"
    const val PREF_DARK_MODE = "dark_mode"
    const val PREF_NOTIFICATION_ENABLED = "notification_enabled"

    // Intent Keys
    const val EXTRA_MEDICINE_ID = "MEDICINE_ID"
    const val EXTRA_EXERCISE_ID = "EXERCISE_ID"
    const val EXTRA_MEAL_ID = "MEAL_ID"
    const val EXTRA_DOCTOR_VISIT_ID = "DOCTOR_VISIT_ID"
    const val EXTRA_CHALLENGE_ID = "CHALLENGE_ID"
    const val EXTRA_OPEN_MEDICINE = "OPEN_MEDICINE"
    const val EXTRA_OPEN_WATER = "OPEN_WATER"
    const val EXTRA_OPEN_EXERCISE = "OPEN_EXERCISE"
    const val EXTRA_OPEN_DIET = "OPEN_DIET"
    const val EXTRA_OPEN_DOCTOR = "OPEN_DOCTOR"

    // Notification Channels
    const val CHANNEL_ID_MEDICINE = "medicine_channel"
    const val CHANNEL_ID_WATER = "water_channel"
    const val CHANNEL_ID_EXERCISE = "exercise_channel"
    const val CHANNEL_ID_DIET = "diet_channel"
    const val CHANNEL_ID_DOCTOR = "doctor_channel"
    const val CHANNEL_ID_GENERAL = "general_channel"

    // Notification IDs
    const val NOTIFICATION_ID_MEDICINE_BASE = 1000
    const val NOTIFICATION_ID_WATER = 2001
    const val NOTIFICATION_ID_EXERCISE_BASE = 3000
    const val NOTIFICATION_ID_DIET_BASE = 4000
    const val NOTIFICATION_ID_DOCTOR_BASE = 5000

    // Alarm Request Codes
    const val ALARM_REQUEST_CODE_BASE = 10000
    const val WATER_REMINDER_REQUEST_CODE = 1001

    // Default Values
    const val DEFAULT_WATER_GOAL = 3000 // in ml (3 liters)
    const val WATER_REMINDER_INTERVAL = 120L // in minutes (2 hours)

    // Date Formats
    const val DATE_FORMAT_DISPLAY = "EEEE, MMM dd, yyyy"
    const val DATE_FORMAT_STORAGE = "yyyy-MM-dd"
    const val TIME_FORMAT_12HR = "hh:mm a"
    const val TIME_FORMAT_24HR = "HH:mm"
    const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    // Frequencies
    const val FREQUENCY_DAILY = "Daily"
    const val FREQUENCY_WEEKLY = "Weekly"
    const val FREQUENCY_CUSTOM = "Custom"

    // Meal Types
    const val MEAL_TYPE_BREAKFAST = "Breakfast"
    const val MEAL_TYPE_LUNCH = "Lunch"
    const val MEAL_TYPE_DINNER = "Dinner"
    const val MEAL_TYPE_SNACK = "Snack"

    // Exercise Types
    val EXERCISE_TYPES = arrayOf(
        "Gym",
        "Yoga",
        "Running",
        "Walking",
        "Cycling",
        "Swimming",
        "Dance",
        "Sports",
        "Cardio",
        "Strength Training",
        "Other"
    )

    // Mood Types
    val MOOD_TYPES = arrayOf("Happy", "Sad", "Anxious", "Calm", "Energetic")
    val MOOD_EMOJIS = arrayOf("😊", "😢", "😰", "😌", "⚡")

    // Blood Groups
    val BLOOD_GROUPS = arrayOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

    // Challenge Types
    const val CHALLENGE_TYPE_WATER = "Water Challenge"
    const val CHALLENGE_TYPE_WALK = "Walk Challenge"
    const val CHALLENGE_TYPE_SUGAR_FREE = "Sugar Free Challenge"
    const val CHALLENGE_TYPE_MEDITATION = "Meditation Challenge"
    const val CHALLENGE_TYPE_EXERCISE = "Exercise Challenge"

    // Challenge Durations
    val CHALLENGE_DURATIONS = arrayOf(7, 15, 21, 30, 60, 90)

    // Status
    const val STATUS_TAKEN = "Taken"
    const val STATUS_SKIPPED = "Skipped"
    const val STATUS_MISSED = "Missed"
    const val STATUS_COMPLETED = "Completed"
    const val STATUS_PENDING = "Pending"

    // Actions
    const val ACTION_TAKEN = "ACTION_TAKEN"
    const val ACTION_SKIP = "ACTION_SKIP"
    const val ACTION_SNOOZE = "ACTION_SNOOZE"

    // Days of Week
    val DAYS_OF_WEEK = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val DAYS_OF_WEEK_FULL = arrayOf(
        "Monday", "Tuesday", "Wednesday", "Thursday",
        "Friday", "Saturday", "Sunday"
    )

    // Permissions
    const val PERMISSION_REQUEST_CODE = 100
    const val PERMISSION_NOTIFICATION = 101
    const val PERMISSION_CAMERA = 102
    const val PERMISSION_STORAGE = 103

    // File Upload
    const val MAX_FILE_SIZE = 5 * 1024 * 1024 // 5 MB
    const val STORAGE_PATH_PRESCRIPTIONS = "prescriptions"
    const val STORAGE_PATH_PROFILE_IMAGES = "profile_images"

    // Pagination
    const val PAGE_SIZE = 20
    const val INITIAL_LOAD_SIZE = 40

    // Animation Duration
    const val ANIMATION_DURATION_SHORT = 200L
    const val ANIMATION_DURATION_NORMAL = 300L
    const val ANIMATION_DURATION_LONG = 500L

    // Splash Screen Duration
    const val SPLASH_DURATION = 2000L // 2 seconds

    // Work Manager Tags
    const val WORK_TAG_WATER_REMINDER = "water_reminder_work"
    const val WORK_TAG_DAILY_SYNC = "daily_sync_work"
    const val WORK_TAG_CLEANUP = "cleanup_work"

    // Error Messages
    const val ERROR_NETWORK = "Please check your internet connection"
    const val ERROR_UNKNOWN = "Something went wrong. Please try again"
    const val ERROR_AUTH = "Authentication failed"
    const val ERROR_PERMISSION = "Permission required"

    // Success Messages
    const val SUCCESS_SAVED = "Saved successfully"
    const val SUCCESS_DELETED = "Deleted successfully"
    const val SUCCESS_UPDATED = "Updated successfully"

    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_NAME_LENGTH = 2
    const val MAX_NAME_LENGTH = 50
}