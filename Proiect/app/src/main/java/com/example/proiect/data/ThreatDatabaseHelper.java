package com.example.proiect.data;

import android.content.Context;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class ThreatDatabaseHelper {
    private static ThreatDatabaseHelper instance;

    private final AppDatabase database;
    private final SavedEventDao savedEventDao;
    private final CaseNoteDao caseNoteDao;

    public static synchronized ThreatDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ThreatDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private ThreatDatabaseHelper(Context context) {
        database = AppDatabase.getInstance(context);
        savedEventDao = database.savedEventDao();
        caseNoteDao = database.caseNoteDao();
        caseNoteDao.normalizeLegacyStatuses();
    }

    public void saveEventState(ThreatEvent event, boolean watched, float rating) {
        SavedEventEntity entity = new SavedEventEntity(
                event.getId(),
                watched,
                rating,
                event.toJsonString(),
                nowText()
        );
        savedEventDao.upsert(entity);
    }

    public SavedEventState getSavedState(String eventId) {
        if (eventId == null) {
            return null;
        }
        SavedEventEntity entity = savedEventDao.getById(eventId);
        return entity == null ? null : entity.toState();
    }

    public Map<String, SavedEventState> getSavedStates() {
        Map<String, SavedEventState> states = new HashMap<>();
        for (SavedEventEntity entity : savedEventDao.getAll()) {
            SavedEventState state = entity.toState();
            states.put(state.getEventId(), state);
        }
        return states;
    }

    public ThreatEvent getCachedEvent(String eventId) {
        if (eventId == null) {
            return null;
        }
        SavedEventEntity entity = savedEventDao.getById(eventId);
        if (entity == null) {
            return null;
        }
        try {
            return ThreatFeedParser.parseEvent(new JSONObject(entity.getCachedJson()));
        } catch (Exception ignored) {
            return null;
        }
    }

    public long upsertCase(CaseNote note) {
        prepareCaseForSave(note);
        if (note.getId() > 0) {
            CaseNote existing = caseNoteDao.getById(note.getId());
            if (existing != null) {
                note.setCreatedAt(existing.getCreatedAt());
                int updatedRows = caseNoteDao.update(note);
                if (updatedRows > 0) {
                    return note.getId();
                }
            }
        }
        note.setId(0);
        note.setCreatedAt(nowText());
        return caseNoteDao.insert(note);
    }

    public List<CaseNote> getAllCases() {
        return caseNoteDao.getAll();
    }

    public int getOpenCaseCountForEvent(String eventId) {
        if (eventId == null || eventId.trim().isEmpty()) {
            return 0;
        }
        return caseNoteDao.getOpenCountForEvent(eventId);
    }

    public int getClosedCaseCountForEvent(String eventId) {
        if (eventId == null || eventId.trim().isEmpty()) {
            return 0;
        }
        return caseNoteDao.getClosedCountForEvent(eventId);
    }

    public Map<String, Integer> getCaseCountsByEvent() {
        Map<String, Integer> counts = new HashMap<>();
        for (EventCaseCount item : caseNoteDao.getCaseCountsByEvent()) {
            counts.put(item.eventId, item.count);
        }
        return counts;
    }

    public CaseNote getCase(long id) {
        return caseNoteDao.getById(id);
    }

    public void deleteCase(long id) {
        caseNoteDao.deleteById(id);
    }

    public int getCaseCount() {
        return caseNoteDao.getCount();
    }

    private void prepareCaseForSave(CaseNote note) {
        note.setResolution(note.getResolution());
        boolean closed = "Inchis".equalsIgnoreCase(note.getStatus());
        String closedAt = note.getClosedAt();
        if (closed) {
            note.setClosedAt(closedAt == null || closedAt.trim().isEmpty() ? nowText() : closedAt);
        } else {
            note.setClosedAt("");
        }
    }

    private String nowText() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date());
    }
}
