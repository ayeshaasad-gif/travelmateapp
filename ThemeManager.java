package com.example.travelplane.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Theme Manager to handle app theme (Light/Dark mode)
 * Uses SharedPreferences to persist theme preference
 */
public class ThemeManager {
    private static final String PREF_NAME = "TravelPlannerTheme";
    private static final String KEY_THEME_MODE = "themeMode";

    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public ThemeManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * Save theme preference
     * @param themeMode Theme mode (THEME_LIGHT or THEME_DARK)
     */
    public void setTheme(int themeMode) {
        editor.putInt(KEY_THEME_MODE, themeMode);
        editor.apply();
        applyTheme(themeMode);
    }

    /**
     * Get saved theme preference
     * @return Theme mode (THEME_LIGHT or THEME_DARK)
     */
    public int getTheme() {
        return preferences.getInt(KEY_THEME_MODE, THEME_LIGHT);
    }

    /**
     * Apply theme to the app
     * @param themeMode Theme mode to apply
     */
    public void applyTheme(int themeMode) {
        if (themeMode == THEME_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Apply saved theme on app start
     */
    public void applySavedTheme() {
        applyTheme(getTheme());
    }
}

