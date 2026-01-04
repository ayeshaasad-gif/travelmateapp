package com.example.travelplane.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Session Manager to handle user login sessions
 * Uses SharedPreferences to store and retrieve session data
 */
public class SessionManager {
    private static final String PREF_NAME = "TravelPlannerSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * Create login session
     * @param email User email
     * @param name User name
     */
    public void createLoginSession(String email, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    /**
     * Check if user is logged in
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get logged in user's email
     * @return User email
     */
    public String getUserEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    /**
     * Get logged in user's name
     * @return User name
     */
    public String getUserName() {
        return preferences.getString(KEY_NAME, "");
    }

    /**
     * Logout user by clearing session data
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}

