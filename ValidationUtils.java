package com.example.travelplane.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Validation utility class for input validation
 */
public class ValidationUtils {

    /**
     * Validate email format
     * @param email Email to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validate password length
     * @param password Password to validate
     * @return true if password is valid (at least 6 characters), false otherwise
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    /**
     * Check if string is empty
     * @param text String to check
     * @return true if empty, false otherwise
     */
    public static boolean isEmpty(String text) {
        return TextUtils.isEmpty(text) || text.trim().isEmpty();
    }
}

