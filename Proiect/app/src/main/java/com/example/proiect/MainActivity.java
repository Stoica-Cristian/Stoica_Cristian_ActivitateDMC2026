package com.example.proiect;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proiect.data.SavedEventState;
import com.example.proiect.data.ThreatDatabaseHelper;
import com.example.proiect.data.ThreatEvent;
import com.example.proiect.data.ThreatRepository;
import com.example.proiect.ui.DashboardPieChartView;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private TextView tvRiskScore;
    private TextView tvSummary;
    private TextView tvTopEvent;
    private ProgressBar progressBar;
    private LinearLayout cardTopEvent;
    private View bottomNavigation;
    private DashboardPieChartView chartSeverity;
    private DashboardPieChartView chartConfidence;
    private String topEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvRiskScore = findViewById(R.id.tvRiskScore);
        tvSummary = findViewById(R.id.tvDashboardSummary);
        tvTopEvent = findViewById(R.id.tvTopEvent);
        progressBar = findViewById(R.id.progressDashboard);
        cardTopEvent = findViewById(R.id.cardTopEvent);
        chartSeverity = findViewById(R.id.chartSeverityDashboard);
        chartConfidence = findViewById(R.id.chartConfidenceDashboard);
        TextView tvRefresh = findViewById(R.id.tvRefreshDashboard);
        TextView tvSettings = findViewById(R.id.tvSettingsDashboard);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        tvRefresh.setOnClickListener(v -> loadDashboard(true));
        tvSettings.setOnClickListener(v -> open(SettingsActivity.class));
        cardTopEvent.setOnClickListener(v -> {
            if (topEventId != null) {
                android.content.Intent intent = new android.content.Intent(this, ThreatDetailActivity.class);
                intent.putExtra(AppConstants.EXTRA_EVENT_ID, topEventId);
                startActivity(intent);
            }
        });
        AppNavigation.setup(this, bottomNavigation, R.id.nav_home);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            AppNavigation.select(bottomNavigation, R.id.nav_home);
        }
        loadDashboard(false);
    }

    private void open(Class<?> target) {
        startActivity(new android.content.Intent(this, target));
    }

    private void loadDashboard(boolean forceRefresh) {
        progressBar.setVisibility(View.VISIBLE);
        ThreatRepository.LoadCallback callback = (events, error) -> {
            progressBar.setVisibility(View.GONE);
            renderDashboard(events);
        };
        if (forceRefresh) {
            ThreatRepository.getInstance().refreshEvents(this, callback);
        } else {
            ThreatRepository.getInstance().loadEvents(this, callback);
        }
    }

    private void renderDashboard(List<ThreatEvent> events) {
        int maxRisk = 0;
        int highCount = 0;
        int mediumCount = 0;
        int lowCount = 0;
        int confidenceLow = 0;
        int confidenceMedium = 0;
        int confidenceHigh = 0;
        int totalRisk = 0;
        int totalConfidence = 0;
        int totalIocs = 0;
        Set<String> families = new HashSet<>();
        Set<String> countries = new HashSet<>();
        ThreatEvent topEvent = null;
        for (ThreatEvent event : events) {
            if ("Ridicat".equalsIgnoreCase(event.getSeverity())) {
                highCount++;
            } else if ("Mediu".equalsIgnoreCase(event.getSeverity())) {
                mediumCount++;
            } else {
                lowCount++;
            }
            if (event.getConfidence() >= 90) {
                confidenceHigh++;
            } else if (event.getConfidence() >= 75) {
                confidenceMedium++;
            } else {
                confidenceLow++;
            }
            totalRisk += event.getRiskScore();
            totalConfidence += event.getConfidence();
            totalIocs += event.getIocs().size();
            families.add(event.getLoaderFamily());
            countries.add(event.getCountry());
            if (event.getRiskScore() > maxRisk) {
                maxRisk = event.getRiskScore();
                topEvent = event;
            }
        }

        ThreatDatabaseHelper db = ThreatDatabaseHelper.getInstance(this);
        Map<String, SavedEventState> savedStates = db.getSavedStates();
        int watchedCount = 0;
        for (SavedEventState state : savedStates.values()) {
            if (state.isWatched()) {
                watchedCount++;
            }
        }
        int averageRisk = events.isEmpty() ? 0 : Math.round(totalRisk / (float) events.size());
        int averageConfidence = events.isEmpty() ? 0 : Math.round(totalConfidence / (float) events.size());

        tvRiskScore.setText(String.valueOf(maxRisk));
        topEventId = topEvent == null ? null : topEvent.getId();
        String topTitle = topEvent == null ? "Fara incidente" : topEvent.getTitle();
        tvTopEvent.setText(topEvent == null ? "Fara incident" : topEvent.getLoaderFamily() + "\n" + topTitle);
        tvSummary.setText(
                "Incidente: " + events.size()
                        + "\nCazuri: " + db.getCaseCount()
                        + "\nIncidente salvate: " + savedStates.size()
                        + "\nUrmarite: " + watchedCount
                        + "\nFamilii loader: " + families.size()
                        + "\nTari afectate: " + countries.size()
                        + "\nIndicatori IOC: " + totalIocs
                        + "\nRisc mediu: " + averageRisk
                        + "\nIncredere medie: " + averageConfidence + "%"
        );
        chartSeverity.setChartData(
                "Severitate",
                new String[]{"Ridicat", "Mediu", "Scazut"},
                new int[]{highCount, mediumCount, lowCount}
        );
        chartConfidence.setChartData(
                "Incredere",
                new String[]{"90-100", "75-89", "60-74"},
                new int[]{confidenceHigh, confidenceMedium, confidenceLow}
        );
    }
}
