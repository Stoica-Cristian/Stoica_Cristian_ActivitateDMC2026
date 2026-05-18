package com.example.proiect.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
@SuppressWarnings("SpellCheckingInspection")
public interface CaseNoteDao {
    @Insert
    long insert(CaseNote note);

    @Update
    int update(CaseNote note);

    @Query("SELECT * FROM case_notes ORDER BY id DESC")
    List<CaseNote> getAll();

    @Query("SELECT * FROM case_notes WHERE id = :id LIMIT 1")
    CaseNote getById(long id);

    @Query("DELETE FROM case_notes WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT COUNT(*) FROM case_notes")
    int getCount();

    @Query("SELECT COUNT(*) FROM case_notes WHERE event_id = :eventId AND status <> 'Inchis'")
    int getOpenCountForEvent(String eventId);

    @Query("SELECT COUNT(*) FROM case_notes WHERE event_id = :eventId AND status = 'Inchis'")
    int getClosedCountForEvent(String eventId);

    @Query("SELECT event_id AS eventId, COUNT(*) AS count "
            + "FROM case_notes "
            + "WHERE event_id IS NOT NULL AND TRIM(event_id) <> '' "
            + "GROUP BY event_id")
    List<EventCaseCount> getCaseCountsByEvent();

    @Query("UPDATE case_notes SET status = 'Necesita interventie' WHERE status = 'Escaladare'")
    void normalizeLegacyStatuses();
}
