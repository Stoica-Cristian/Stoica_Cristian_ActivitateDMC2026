package com.example.proiect.ui;

import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class UiFormat {
    private UiFormat() {
    }

    public static int severityColor(String severity) {
        if ("Ridicat".equalsIgnoreCase(severity)) {
            return Color.parseColor("#C62828");
        }
        if ("Mediu".equalsIgnoreCase(severity)) {
            return Color.parseColor("#D97706");
        }
        return Color.parseColor("#2E7D32");
    }

    public static String formatDate(long timeMillis) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date(timeMillis));
    }

    public static int indexOf(String[] values, String target) {
        if (target == null) {
            return 0;
        }
        for (int i = 0; i < values.length; i++) {
            if (target.equals(values[i])) {
                return i;
            }
        }
        return 0;
    }
}
