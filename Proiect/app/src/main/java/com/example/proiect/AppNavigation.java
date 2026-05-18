package com.example.proiect;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

public final class AppNavigation {
    private static final int[] NAV_ITEM_IDS = {
            R.id.nav_home,
            R.id.nav_feed,
            R.id.nav_map,
            R.id.nav_cases,
            R.id.nav_analytics
    };

    private AppNavigation() {
    }

    public static void setup(Activity activity, View navigationView, int selectedItemId) {
        for (int itemId : NAV_ITEM_IDS) {
            View itemView = navigationView.findViewById(itemId);
            if (itemView == null) {
                continue;
            }
            itemView.setOnClickListener(v -> open(activity, itemId, selectedItemId));
        }
        select(navigationView, selectedItemId);
    }

    public static void select(View navigationView, int selectedItemId) {
        for (int itemId : NAV_ITEM_IDS) {
            View itemView = navigationView.findViewById(itemId);
            if (itemView != null) {
                setSelectedRecursive(itemView, itemId == selectedItemId);
            }
        }
    }

    private static void open(Activity activity, int itemId, int selectedItemId) {
        if (itemId == selectedItemId) {
            return;
        }
        Class<?> target = targetFor(itemId);
        if (target == null) {
            return;
        }
        Intent intent = new Intent(activity, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        if (!(activity instanceof MainActivity) && target != MainActivity.class) {
            activity.finish();
        }
    }

    private static void setSelectedRecursive(View view, boolean selected) {
        view.setSelected(selected);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                setSelectedRecursive(group.getChildAt(i), selected);
            }
        }
    }

    private static Class<?> targetFor(int itemId) {
        if (itemId == R.id.nav_home) {
            return MainActivity.class;
        }
        if (itemId == R.id.nav_feed) {
            return ThreatFeedActivity.class;
        }
        if (itemId == R.id.nav_map) {
            return ThreatMapActivity.class;
        }
        if (itemId == R.id.nav_cases) {
            return CasesActivity.class;
        }
        if (itemId == R.id.nav_analytics) {
            return AnalyticsActivity.class;
        }
        return null;
    }
}
