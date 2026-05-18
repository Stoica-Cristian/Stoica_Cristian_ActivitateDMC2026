package com.example.proiect.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "case_notes")
public class CaseNote {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String title = "";

    @ColumnInfo(name = "event_id")
    private String eventId;

    @NonNull
    private String status = "";

    @ColumnInfo(name = "due_date")
    private long dueDate;

    private boolean notify;

    private float rating;

    @NonNull
    private String notes = "";

    @NonNull
    @ColumnInfo(defaultValue = "''")
    private String resolution = "";

    @NonNull
    @ColumnInfo(name = "closed_at", defaultValue = "''")
    private String closedAt = "";

    @NonNull
    @ColumnInfo(name = "created_at")
    private String createdAt = "";

    public CaseNote() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? "" : status;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution == null ? "" : resolution;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt == null ? "" : closedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt == null ? "" : createdAt;
    }
}
