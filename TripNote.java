package com.example.travelplane.models;

/**
 * Model class representing a Trip Note
 */
public class TripNote {
    private int id;
    private String title;
    private String description;
    private long timestamp;
    private String imageUri; // Path to the photo

    public TripNote() {
    }

    public TripNote(int id, String title, String description, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.imageUri = null;
    }

    public TripNote(int id, String title, String description, long timestamp, String imageUri) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.imageUri = imageUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}

