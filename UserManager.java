package com.example.travelplane.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.travelplane.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * User Manager to handle user registration and authentication
 * Uses SharedPreferences to store user data locally
 */
public class UserManager {
    private static final String PREF_NAME = "TravelPlannerUsers";
    private static final String KEY_USERS = "users";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public UserManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
    }

    /**
     * Register a new user
     * @param user User object to register
     * @return true if registration successful, false if user already exists
     */
    public boolean registerUser(User user) {
        List<User> users = getAllUsers();

        // Check if user already exists
        for (User existingUser : users) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                return false; // User already exists
            }
        }

        // Add new user
        users.add(user);
        saveUsers(users);
        return true;
    }

    /**
     * Authenticate user with email and password
     * @param email User email
     * @param password User password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String email, String password) {
        List<User> users = getAllUsers();

        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }

        return null; // Authentication failed
    }

    /**
     * Check if user exists by email
     * @param email User email
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String email) {
        List<User> users = getAllUsers();

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Update user password (for password reset)
     * @param email User email
     * @param newPassword New password
     * @return true if update successful, false otherwise
     */
    public boolean updatePassword(String email, String newPassword) {
        List<User> users = getAllUsers();

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                user.setPassword(newPassword);
                saveUsers(users);
                return true;
            }
        }

        return false;
    }

    /**
     * Get all registered users
     * @return List of all users
     */
    private List<User> getAllUsers() {
        String usersJson = preferences.getString(KEY_USERS, "");

        if (usersJson.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> users = gson.fromJson(usersJson, type);
            return users != null ? users : new ArrayList<>();
        } catch (Exception e) {
            // If JSON is corrupted, return empty list and clear preferences
            editor.remove(KEY_USERS);
            editor.apply();
            return new ArrayList<>();
        }
    }

    /**
     * Save users list to SharedPreferences
     * @param users List of users to save
     */
    private void saveUsers(List<User> users) {
        String usersJson = gson.toJson(users);
        editor.putString(KEY_USERS, usersJson);
        editor.apply();
    }
}

