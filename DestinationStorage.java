package com.example.travelplane.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.travelplane.data.EnglishDestinationsData;
import com.example.travelplane.models.Destination;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages persistence of destinations (add/delete) using SharedPreferences.
 * On first load, seeds data from EnglishDestinationsData.
 */
public class DestinationStorage {
    private static final String PREF_NAME = "TravelPlaneDestinations";
    private static final String KEY_DESTINATIONS = "destinations";

    private final SharedPreferences prefs;
    private final Gson gson;

    public DestinationStorage(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * Load destinations, seeding with EnglishDestinationsData on first launch.
     */
    public List<Destination> loadDestinations() {
        String json = prefs.getString(KEY_DESTINATIONS, "");
        if (json == null || json.isEmpty()) {
            List<Destination> seed = EnglishDestinationsData.getEnglishDestinations();
            saveDestinations(seed);
            return new ArrayList<>(seed);
        }
        try {
            Type type = new TypeToken<List<Destination>>() {}.getType();
            List<Destination> list = gson.fromJson(json, type);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /** Save full list. */
    public void saveDestinations(List<Destination> destinations) {
        String json = gson.toJson(destinations);
        prefs.edit().putString(KEY_DESTINATIONS, json).apply();
    }

    /** Add new destination and persist. */
    public void addDestination(Destination destination) {
        List<Destination> list = loadDestinations();
        list.add(destination);
        saveDestinations(list);
    }

    /** Delete by id and persist. */
    public void deleteDestination(int destinationId) {
        List<Destination> list = loadDestinations();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == destinationId) {
                list.remove(i);
                break;
            }
        }
        saveDestinations(list);
    }

    /** Generate next ID based on current max id. */
    public int nextId() {
        List<Destination> list = loadDestinations();
        int max = 0;
        for (Destination d : list) {
            if (d.getId() > max) max = d.getId();
        }
        return max + 1;
    }
}

