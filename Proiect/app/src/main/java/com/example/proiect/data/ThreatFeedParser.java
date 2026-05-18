package com.example.proiect.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ThreatFeedParser {
    public static List<ThreatEvent> parseEvents(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        JSONArray events = root.getJSONArray("events");
        List<ThreatEvent> result = new ArrayList<>();
        for (int i = 0; i < events.length(); i++) {
            result.add(parseEvent(events.getJSONObject(i)));
        }
        return result;
    }

    public static ThreatEvent parseEvent(JSONObject object) throws JSONException {
        return new ThreatEvent(
                object.getString("id"),
                object.getString("title"),
                object.getString("loaderFamily"),
                object.getString("severity"),
                object.getInt("confidence"),
                object.getString("country"),
                object.getString("city"),
                object.getDouble("latitude"),
                object.getDouble("longitude"),
                object.getString("firstSeen"),
                object.getString("lastSeen"),
                readStringList(object.getJSONArray("techniques")),
                readStringList(object.getJSONArray("iocs")),
                object.getString("summary"),
                readStringList(object.getJSONArray("defensiveActions")),
                object.optString("sourceUrl", "")
        );
    }

    public static ThreatEvent findById(List<ThreatEvent> events, String eventId) {
        if (eventId == null) {
            return null;
        }
        for (ThreatEvent event : events) {
            if (eventId.equals(event.getId())) {
                return event;
            }
        }
        return null;
    }

    private static List<String> readStringList(JSONArray array) throws JSONException {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            values.add(array.getString(i));
        }
        return values;
    }
}
