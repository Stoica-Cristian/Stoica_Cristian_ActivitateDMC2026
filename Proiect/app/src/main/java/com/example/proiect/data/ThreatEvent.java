package com.example.proiect.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ThreatEvent {
    private final String id;
    private final String title;
    private final String loaderFamily;
    private final String severity;
    private final int confidence;
    private final String country;
    private final String city;
    private final double latitude;
    private final double longitude;
    private final String firstSeen;
    private final String lastSeen;
    private final List<String> techniques;
    private final List<String> iocs;
    private final String summary;
    private final List<String> defensiveActions;
    private final String sourceUrl;

    public ThreatEvent(
            String id,
            String title,
            String loaderFamily,
            String severity,
            int confidence,
            String country,
            String city,
            double latitude,
            double longitude,
            String firstSeen,
            String lastSeen,
            List<String> techniques,
            List<String> iocs,
            String summary,
            List<String> defensiveActions,
            String sourceUrl
    ) {
        this.id = id;
        this.title = title;
        this.loaderFamily = loaderFamily;
        this.severity = severity;
        this.confidence = confidence;
        this.country = country;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
        this.techniques = techniques == null ? new ArrayList<>() : techniques;
        this.iocs = iocs == null ? new ArrayList<>() : iocs;
        this.summary = summary;
        this.defensiveActions = defensiveActions == null ? new ArrayList<>() : defensiveActions;
        this.sourceUrl = sourceUrl;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLoaderFamily() {
        return loaderFamily;
    }

    public String getSeverity() {
        return severity;
    }

    public int getConfidence() {
        return confidence;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getFirstSeen() {
        return firstSeen;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public List<String> getTechniques() {
        return techniques;
    }

    public List<String> getIocs() {
        return iocs;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getDefensiveActions() {
        return defensiveActions;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public int getRiskScore() {
        int severityWeight;
        if ("Ridicat".equalsIgnoreCase(severity)) {
            severityWeight = 100;
        } else if ("Mediu".equalsIgnoreCase(severity)) {
            severityWeight = 65;
        } else {
            severityWeight = 35;
        }
        return Math.round((severityWeight * confidence) / 100f);
    }

    public String getLocationLabel() {
        return city + ", " + country;
    }

    public String toJsonString() {
        try {
            JSONObject object = new JSONObject();
            object.put("id", id);
            object.put("title", title);
            object.put("loaderFamily", loaderFamily);
            object.put("severity", severity);
            object.put("confidence", confidence);
            object.put("country", country);
            object.put("city", city);
            object.put("latitude", latitude);
            object.put("longitude", longitude);
            object.put("firstSeen", firstSeen);
            object.put("lastSeen", lastSeen);
            object.put("techniques", toArray(techniques));
            object.put("iocs", toArray(iocs));
            object.put("summary", summary);
            object.put("defensiveActions", toArray(defensiveActions));
            object.put("sourceUrl", sourceUrl);
            return object.toString();
        } catch (JSONException e) {
            return "{}";
        }
    }

    private JSONArray toArray(List<String> values) {
        JSONArray array = new JSONArray();
        for (String value : values) {
            array.put(value);
        }
        return array;
    }
}
