package com.example.proiect.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreatRepository {
    private static final String REMOTE_FEED_URL = "https://pastebin.com/raw/9vM61Zjd";
    private static final int CONNECT_TIMEOUT_MS = 8000;
    private static final int READ_TIMEOUT_MS = 8000;

    private static ThreatRepository instance;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private List<ThreatEvent> cachedEvents = new ArrayList<>();

    public interface LoadCallback {
        void onLoaded(List<ThreatEvent> events, Exception error);
    }

    public static synchronized ThreatRepository getInstance() {
        if (instance == null) {
            instance = new ThreatRepository();
        }
        return instance;
    }

    private ThreatRepository() {
    }

    public void loadEvents(Context context, LoadCallback callback) {
        if (!cachedEvents.isEmpty()) {
            List<ThreatEvent> snapshot = new ArrayList<>(cachedEvents);
            mainHandler.post(() -> callback.onLoaded(snapshot, null));
            return;
        }
        refreshEvents(context, callback);
    }

    public void refreshEvents(Context context, LoadCallback callback) {
        executor.execute(() -> {
            List<ThreatEvent> events = new ArrayList<>();
            Exception error = null;

            try {
                events = ThreatFeedParser.parseEvents(readRemoteFeed());
            } catch (Exception remoteError) {
                error = remoteError;
            }

            cachedEvents = new ArrayList<>(events);
            Exception finalError = error;
            List<ThreatEvent> finalEvents = new ArrayList<>(events);
            mainHandler.post(() -> callback.onLoaded(finalEvents, finalError));
        });
    }

    private String readRemoteFeed() throws Exception {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(REMOTE_FEED_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json,text/plain,*/*");
            connection.setRequestProperty("User-Agent", "ThreatMonitor/1.0");

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                throw new IllegalStateException("Remote feed HTTP " + responseCode);
            }
            return readStream(connection.getInputStream());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readStream(InputStream stream) throws Exception {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }
}
