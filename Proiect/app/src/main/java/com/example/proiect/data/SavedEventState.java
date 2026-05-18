package com.example.proiect.data;

public class SavedEventState {
    private final String eventId;
    private final boolean watched;
    private final float rating;
    private final String savedAt;

    public SavedEventState(String eventId, boolean watched, float rating, String savedAt) {
        this.eventId = eventId;
        this.watched = watched;
        this.rating = rating;
        this.savedAt = savedAt;
    }

    public String getEventId() {
        return eventId;
    }

    public boolean isWatched() {
        return watched;
    }

    public float getRating() {
        return rating;
    }

    public String getSavedAt() {
        return savedAt;
    }
}
