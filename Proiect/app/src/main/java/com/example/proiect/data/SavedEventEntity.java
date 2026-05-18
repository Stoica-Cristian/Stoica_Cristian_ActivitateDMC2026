package com.example.proiect.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_events")
public class SavedEventEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "event_id")
    private String eventId = "";

    @ColumnInfo(name = "is_watched")
    private boolean watched;

    private float rating;

    @NonNull
    @ColumnInfo(name = "cached_json")
    private String cachedJson = "";

    @NonNull
    @ColumnInfo(name = "saved_at")
    private String savedAt = "";

    public SavedEventEntity() {
    }

    @Ignore
    public SavedEventEntity(String eventId, boolean watched, float rating, String cachedJson, String savedAt) {
        setEventId(eventId);
        this.watched = watched;
        this.rating = rating;
        setCachedJson(cachedJson);
        setSavedAt(savedAt);
    }

    @NonNull
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId == null ? "" : eventId;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @NonNull
    public String getCachedJson() {
        return cachedJson;
    }

    public void setCachedJson(String cachedJson) {
        this.cachedJson = cachedJson == null ? "" : cachedJson;
    }

    @NonNull
    public String getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(String savedAt) {
        this.savedAt = savedAt == null ? "" : savedAt;
    }

    public SavedEventState toState() {
        return new SavedEventState(eventId, watched, rating, savedAt);
    }
}
