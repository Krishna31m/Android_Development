package com.example.healthreminder.utils

import android.util.Patterns
import java.util.regex.Pattern

/**
 * Utility class for input validation
 */
object ValidationUtils {

    /**
     * Validate email address
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validate password
     * Rules: At least 6 characters
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= Constants.MIN_PASSWORD_LENGTH
    }

    /**
     * Validate strong password
     * Rules: At least 8 characters, 1 uppercase, 1 lowercase, 1 number
     */
    fun isStrongPassword(password: String): Boolean {
        if (password.length < 8) return false

        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }

        return hasUpperCase && hasLowerCase && hasDigit
    }

    /**
     * Validate name
     */
    fun isValidName(name: String): Boolean {
        return name.length >= Constants.MIN_NAME_LENGTH &&
                name.length <= Constants.MAX_NAME_LENGTH
    }

    /**
     * Validate phone number
     */
    fun isValidPhone(phone: String): Boolean {
        val phonePattern = Pattern.compile("^[+]?[0-9]{10,13}\$")
        return phone.isNotEmpty() && phonePattern.matcher(phone).matches()
    }

    /**
     * Validate time format (HH:mm)
     */
    fun isValidTimeFormat(time: String): Boolean {
        val timePattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]\$")
        return timePattern.matcher(time).matches()
    }

    /**
     * Validate medicine dosage
     */
    fun isValidDosage(dosage: String): Boolean {
        return dosage.isNotEmpty() && dosage.length <= 50
    }

    /**
     * Validate water amount (in ml)
     */
    fun isValidWaterAmount(amount: Int): Boolean {
        return amount > 0 && amount <= 2000 // Max 2 liters per entry
    }

    /**
     * Validate exercise duration (in minutes)
     */
    fun isValidExerciseDuration(duration: Int): Boolean {
        return duration > 0 && duration <= 480 // Max 8 hours
    }

    /**
     * Validate calorie count
     */
    fun isValidCalories(calories: Int): Boolean {
        return calories >= 0 && calories <= 5000
    }

    /**
     * Validate blood group
     */
    fun isValidBloodGroup(bloodGroup: String): Boolean {
        val validGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
        return bloodGroup in validGroups
    }

    /**
     * Validate age
     */
    fun isValidAge(age: Int): Boolean {
        return age in 1..150
    }

    /**
     * Validate weight (in kg)
     */
    fun isValidWeight(weight: Float): Boolean {
        return weight > 0 && weight <= 500
    }

    /**
     * Validate height (in cm)
     */
    fun isValidHeight(height: Float): Boolean {
        return height > 0 && height <= 300
    }

    /**
     * Get email validation error message
     */
    fun getEmailError(email: String): String? {
        return when {
            email.isEmpty() -> "Email is required"
            !isValidEmail(email) -> "Invalid email format"
            else -> null
        }
    }

    /**
     * Get password validation error message
     */
    fun getPasswordError(password: String): String? {
        return when {
            password.isEmpty() -> "Password is required"
            !isValidPassword(password) -> "Password must be at least ${Constants.MIN_PASSWORD_LENGTH} characters"
            else -> null
        }
    }

    /**
     * Get name validation error message
     */
    fun getNameError(name: String): String? {
        return when {
            name.isEmpty() -> "Name is required"
            name.length < Constants.MIN_NAME_LENGTH -> "Name is too short"
            name.length > Constants.MAX_NAME_LENGTH -> "Name is too long"
            else -> null
        }
    }

    /**
     * Get phone validation error message
     */
    fun getPhoneError(phone: String): String? {
        return when {
            phone.isEmpty() -> "Phone number is required"
            !isValidPhone(phone) -> "Invalid phone number format"
            else -> null
        }
    }

    /**
     * Check if passwords match
     */
    fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.isNotEmpty()
    }

    /**
     * Sanitize input string (remove special characters)
     */
    fun sanitizeInput(input: String): String {
        return input.trim().replace(Regex("[^a-zA-Z0-9\\s]"), "")
    }

    /**
     * Validate URL format
     */
    fun isValidUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    /**
     * Calculate password strength
     * Returns: 0 (Weak), 1 (Medium), 2 (Strong)
     */
    fun getPasswordStrength(password: String): Int {
        var strength = 0

        if (password.length >= 8) strength++
        if (password.any { it.isUpperCase() }) strength++
        if (password.any { it.isLowerCase() }) strength++
        if (password.any { it.isDigit() }) strength++
        if (password.any { !it.isLetterOrDigit() }) strength++

        return when {
            strength <= 2 -> 0 // Weak
            strength <= 3 -> 1 // Medium
            else -> 2 // Strong
        }
    }

    /**
     * Get password strength description
     */
    fun getPasswordStrengthText(password: String): String {
        return when (getPasswordStrength(password)) {
            0 -> "Weak"
            1 -> "Medium"
            2 -> "Strong"
            else -> "Unknown"
        }
    }
}