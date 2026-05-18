package com.example.proiect.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SavedEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(SavedEventEntity event);

    @Query("SELECT * FROM saved_events WHERE event_id = :eventId LIMIT 1")
    SavedEventEntity getById(String eventId);

    @Query("SELECT * FROM saved_events")
    List<SavedEventEntity> getAll();
}
