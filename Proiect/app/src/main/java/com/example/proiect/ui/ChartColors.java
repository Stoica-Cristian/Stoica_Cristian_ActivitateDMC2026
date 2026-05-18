package com.example.proiect.ui;

import android.graphics.Color;

final class ChartColors {
    private static final int[] BASE_PALETTE = {
            Color.parseColor("#2196F3"),
            Color.parseColor("#F44336"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#FFC107"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#00BCD4"),
            Color.parseColor("#FF9800"),
            Color.parseColor("#E91E63"),
            Color.parseColor("#3F51B5"),
            Color.parseColor("#8BC34A")
    };

    private ChartColors() {
    }

    static int forLabel(String chartTitle, String label, int index) {
        int safeIndex = Math.max(0, index);
        int baseColor = BASE_PALETTE[safeIndex % BASE_PALETTE.length];
        int cycle = safeIndex / BASE_PALETTE.length;
        if (cycle == 0) {
            return baseColor;
        }

        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        hsv[0] = (hsv[0] + cycle * 8f) % 360f;
        hsv[1] = clamp(hsv[1] - cycle * 0.06f, 0.45f, 0.9f);
        hsv[2] = clamp(hsv[2] - cycle * 0.08f, 0.55f, 0.95f);
        return Color.HSVToColor(hsv);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
