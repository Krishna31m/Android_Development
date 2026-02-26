package com.example.healthreminder.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREF_NAME,
        Context.MODE_PRIVATE
    )

    private val editor: SharedPreferences.Editor = prefs.edit()

    /**
     * Save user ID
     */
    fun saveUserId(userId: String) {
        editor.putString(Constants.PREF_USER_ID, userId).apply()
    }

    /**
     * Get user ID
     */
    fun getUserId(): String? {
        return prefs.getString(Constants.PREF_USER_ID, null)
    }

    /**
     * Save user name
     */
    fun saveUserName(name: String) {
        editor.putString(Constants.PREF_USER_NAME, name).apply()
    }

    /**
     * Get user name
     */
    fun getUserName(): String? {
        return prefs.getString(Constants.PREF_USER_NAME, null)
    }

    /**
     * Save user email
     */
    fun saveUserEmail(email: String) {
        editor.putString(Constants.PREF_USER_EMAIL, email).apply()
    }

    /**
     * Get user email
     */
    fun getUserEmail(): String? {
        return prefs.getString(Constants.PREF_USER_EMAIL, null)
    }

    /**
     * Set login status
     */
    fun setLoggedIn(isLoggedIn: Boolean) {
        editor.putBoolean(Constants.PREF_IS_LOGGED_IN, isLoggedIn).apply()
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(Constants.PREF_IS_LOGGED_IN, false)
    }

    /**
     * Save water goal
     */
    fun saveWaterGoal(goal: Int) {
        editor.putInt(Constants.PREF_WATER_GOAL, goal).apply()
    }

    /**
     * Get water goal
     */
    fun getWaterGoal(): Int {
        return prefs.getInt(Constants.PREF_WATER_GOAL, Constants.DEFAULT_WATER_GOAL)
    }

    /**
     * Set dark mode
     */
    fun setDarkMode(enabled: Boolean) {
        editor.putBoolean(Constants.PREF_DARK_MODE, enabled).apply()
    }

    /**
     * Check if dark mode is enabled
     */
    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean(Constants.PREF_DARK_MODE, false)
    }

    /**
     * Set notification enabled
     */
    fun setNotificationEnabled(enabled: Boolean) {
        editor.putBoolean(Constants.PREF_NOTIFICATION_ENABLED, enabled).apply()
    }

    /**
     * Check if notifications are enabled
     */
    fun isNotificationEnabled(): Boolean {
        return prefs.getBoolean(Constants.PREF_NOTIFICATION_ENABLED, true)
    }

    /**
     * Save string value
     */
    fun saveString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    /**
     * Get string value
     */
    fun getString(key: String, defaultValue: String? = null): String? {
        return prefs.getString(key, defaultValue)
    }

    /**
     * Save int value
     */
    fun saveInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    /**
     * Get int value
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return prefs.getInt(key, defaultValue)
    }

    /**
     * Save boolean value
     */
    fun saveBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    /**
     * Get boolean value
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    /**
     * Save long value
     */
    fun saveLong(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    /**
     * Get long value
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return prefs.getLong(key, defaultValue)
    }

    /**
     * Remove key
     */
    fun remove(key: String) {
        editor.remove(key).apply()
    }

    /**
     * Clear all preferences (logout)
     */
    fun clearAll() {
        editor.clear().apply()
    }

    /**
     * Check if key exists
     */
    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    companion object {
        @Volatile
        private var instance: PreferencesHelper? = null

        fun getInstance(context: Context): PreferencesHelper {
            return instance ?: synchronized(this) {
                instance ?: PreferencesHelper(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}