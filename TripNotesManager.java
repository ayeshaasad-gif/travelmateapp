package com.example.travelplane.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.travelplane.models.TripNote;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Trip Notes Manager to handle trip notes storage
 * Uses SharedPreferences to store notes locally
 */
public class TripNotesManager {
    private static final String PREF_NAME = "TravelPlannerNotes";
    private static final String KEY_NOTES = "notes";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public TripNotesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
    }

    /**
     * Add a new trip note
     * @param note TripNote object to add
     */
    public void addNote(TripNote note) {
        List<TripNote> notes = getAllNotes();

        // Generate unique ID
        int newId = notes.isEmpty() ? 1 : notes.get(notes.size() - 1).getId() + 1;
        note.setId(newId);
        note.setTimestamp(System.currentTimeMillis());

        notes.add(note);
        saveNotes(notes);
    }

    /**
     * Delete a trip note
     * @param noteId ID of the note to delete
     */
    public void deleteNote(int noteId) {
        List<TripNote> notes = getAllNotes();
        // Compatible with API 21+ - use iterator instead of removeIf
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == noteId) {
                notes.remove(i);
                break;
            }
        }
        saveNotes(notes);
    }

    /**
     * Update an existing trip note
     * @param updatedNote The note with updated values
     */
    public void updateNote(TripNote updatedNote) {
        List<TripNote> notes = getAllNotes();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == updatedNote.getId()) {
                notes.set(i, updatedNote);
                break;
            }
        }
        saveNotes(notes);
    }

    /**
     * Get all trip notes
     * @return List of all trip notes
     */
    public List<TripNote> getAllNotes() {
        String notesJson = preferences.getString(KEY_NOTES, "");

        if (notesJson.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type type = new TypeToken<List<TripNote>>() {}.getType();
            List<TripNote> notes = gson.fromJson(notesJson, type);
            return notes != null ? notes : new ArrayList<>();
        } catch (Exception e) {
            // If JSON is corrupted, return empty list and clear preferences
            editor.remove(KEY_NOTES);
            editor.apply();
            return new ArrayList<>();
        }
    }

    /**
     * Save notes list to SharedPreferences
     * @param notes List of notes to save
     */
    private void saveNotes(List<TripNote> notes) {
        String notesJson = gson.toJson(notes);
        editor.putString(KEY_NOTES, notesJson);
        editor.apply();
    }
}

