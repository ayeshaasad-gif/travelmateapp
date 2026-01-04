package com.example.travelplane.backend;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.example.travelplane.models.Destination;
import com.example.travelplane.models.TripNote;
import com.example.travelplane.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Backend Service - Simulates a backend server with async operations
 * Provides CRUD operations for Users, Destinations, and Trip Notes
 * All operations are async to simulate network delay
 */
public class BackendService {

    private static final String PREF_NAME = "TravelPlannerBackend";
    private static final String KEY_USERS = "backend_users";
    private static final String KEY_DESTINATIONS = "backend_destinations";
    private static final String KEY_NOTES = "backend_notes";
    private static final int NETWORK_DELAY_MS = 500; // Simulate 500ms network delay

    private static BackendService instance;
    private final SharedPreferences preferences;
    private final Gson gson;
    private final ExecutorService executor;
    private final Handler mainHandler;

    private BackendService(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        executor = Executors.newFixedThreadPool(4);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Get singleton instance
     */
    public static synchronized BackendService getInstance(Context context) {
        if (instance == null) {
            instance = new BackendService(context);
        }
        return instance;
    }

    // ==================== USER OPERATIONS ====================

    /**
     * Register a new user (async)
     */
    public void registerUser(User user, BackendCallback<User> callback) {
        executor.execute(() -> {
            simulateNetworkDelay();

            List<User> users = getAllUsersSync();

            // Check if user exists
            for (User existingUser : users) {
                if (existingUser.getEmail().equals(user.getEmail())) {
                    postError(callback, "User already exists");
                    return;
                }
            }

            users.add(user);
            saveUsersSync(users);

            postSuccess(callback, user);
        });
    }

    /**
     * Authenticate user (async)
     */
    public void authenticateUser(String email, String password, BackendCallback<User> callback) {
        executor.execute(() -> {
            simulateNetworkDelay();

            List<User> users = getAllUsersSync();

            for (User user : users) {
                if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                    postSuccess(callback, user);
                    return;
                }
            }

            postError(callback, "Invalid credentials");
        });
    }

    /**
     * Get user by email (async)
     */
    public void getUserByEmail(String email, BackendCallback<User> callback) {
        executor.execute(() -> {
            simulateNetworkDelay();

            List<User> users = getAllUsersSync();

            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    postSuccess(callback, user);
                    return;
                }
            }

            postError(callback, "User not found");
        });
    }

    /**
     * Update user password (async)
     */
    public void updateUserPassword(String email, String newPassword, BackendCallback<Boolean> callback) {
        executor.execute(() -> {
            simulateNetworkDelay();

            List<User> users = getAllUsersSync();

            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    user.setPassword(newPassword);
                    saveUsersSync(users);
                    postSuccess(callback, true);
                    return;
                }
            }

            postError(callback, "User not found");
        });
    }

    // ==================== DESTINATION OPERATIONS ====================

    /**
     * Save destinations to local cache (async)
     */
    public void cacheDestinations(List<Destination> destinations, BackendCallback<Boolean> callback) {
        executor.execute(() -> {
            simulateNetworkDelay();

            String json = gson.toJson(destinations);
            preferences.edit().putString(KEY_DESTINATIONS, json).apply();

            postSuccess(callback, true);
        });
    }

    /**
     * Get cached destinations (async)
     */
    public void getCachedDestinations(BackendCallback<List<Destination>> callback) {
        executor.execute(() -> {
            simulateNetworkDelay();

            String json = preferences.getString(KEY_DESTINATIONS, "");

            if (json.isEmpty()) {
                postSuccess(callback, new ArrayList<>());
                return;
            }

            try {
                Type type = new TypeToken<List<Destination>>() {}.getType();
                List<Destination> destinations = gson.fromJson(json, type);
                postSuccess(callback, destinations != null ? destinations : new ArrayList<>());
            } catch (Exception e) {
                postError(callback, "Failed to load cached destinations");
            }
        });
    }

    // ==================== TRIP NOTES OPERATIONS ====================

    /**
     * Save trip note (async)
     */
    public void saveTripNote(TripNote note, BackendCallback<TripNote> callback) {
        executor.execute(() -> {
            simulateNetworkDelay();

            List<TripNote> notes = getAllNotesSync();

            // Generate ID if new note
            if (note.getId() == 0) {
                int newId = notes.isEmpty() ? 1 : notes.get(notes.size() - 1).getId() + 1;
                note.setId(newId);
                note.setTimestamp(System.currentTimeMillis());
            }

            notes.add(note);
            saveNotesSync(notes);

            postSuccess(callback, note);
        });
    }

    /**
     * Get all trip notes (async)
     */
    public void getAllTripNotes(BackendCallback<List<TripNote>> callback) {
        executor.execute(() -> {
            simulateNetworkDelay();

            List<TripNote> notes = getAllNotesSync();
            postSuccess(callback, notes);
        });
    }

    /**
     * Delete trip note (async)
     */
    public void deleteTripNote(int noteId, BackendCallback<Boolean> callback) {
        executor.execute(() -> {
            simulateNetworkDelay();

            List<TripNote> notes = getAllNotesSync();

            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).getId() == noteId) {
                    notes.remove(i);
                    saveNotesSync(notes);
                    postSuccess(callback, true);
                    return;
                }
            }

            postError(callback, "Note not found");
        });
    }

    // ==================== SYNC OPERATIONS (INTERNAL) ====================

    private List<User> getAllUsersSync() {
        String json = preferences.getString(KEY_USERS, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> users = gson.fromJson(json, type);
            return users != null ? users : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveUsersSync(List<User> users) {
        String json = gson.toJson(users);
        preferences.edit().putString(KEY_USERS, json).apply();
    }

    private List<TripNote> getAllNotesSync() {
        String json = preferences.getString(KEY_NOTES, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type type = new TypeToken<List<TripNote>>() {}.getType();
            List<TripNote> notes = gson.fromJson(json, type);
            return notes != null ? notes : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveNotesSync(List<TripNote> notes) {
        String json = gson.toJson(notes);
        preferences.edit().putString(KEY_NOTES, json).apply();
    }

    // ==================== UTILITY METHODS ====================

    private void simulateNetworkDelay() {
        try {
            Thread.sleep(NETWORK_DELAY_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private <T> void postSuccess(BackendCallback<T> callback, T data) {
        mainHandler.post(() -> callback.onSuccess(data));
    }

    private <T> void postError(BackendCallback<T> callback, String error) {
        mainHandler.post(() -> callback.onError(error));
    }

    /**
     * Callback interface for async backend operations
     */
    public interface BackendCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
}

