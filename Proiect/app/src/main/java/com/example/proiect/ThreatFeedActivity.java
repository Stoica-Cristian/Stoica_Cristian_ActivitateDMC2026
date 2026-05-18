package com.example.proiect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proiect.data.AppPrefs;
import com.example.proiect.data.SavedEventState;
import com.example.proiect.data.ThreatDatabaseHelper;
import com.example.proiect.data.ThreatEvent;
import com.example.proiect.data.ThreatRepository;
import com.example.proiect.ui.ThreatEventAdapter;
import com.example.proiect.ui.UiFormat;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ThreatFeedActivity extends AppCompatActivity {
    private final List<ThreatEvent> allEvents = new ArrayList<>();
    private final List<ThreatEvent> visibleEvents = new ArrayList<>();
    private ThreatEventAdapter adapter;
    private Spinner spSeverity;
    private Spinner spRiskSort;
    private SwitchMaterial swWatched;
    private TextView tvStatus;
    private ProgressBar progressBar;
    private ThreatDatabaseHelper database;
    private Map<String, SavedEventState> savedStates;
    private Map<String, Integer> caseCounts;
    private View bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threat_feed);

        database = ThreatDatabaseHelper.getInstance(this);
        adapter = new ThreatEventAdapter(this);

        spSeverity = findViewById(R.id.spSeverityFilter);
        spRiskSort = findViewById(R.id.spRiskSort);
        swWatched = findViewById(R.id.swOnlyWatched);
        tvStatus = findViewById(R.id.tvFeedStatus);
        progressBar = findViewById(R.id.progressFeed);
        ListView lvThreats = findViewById(R.id.lvThreatEvents);
        Button btnRefresh = findViewById(R.id.btnRefreshFeed);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        ArrayAdapter<String> severityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AppConstants.SEVERITY_FILTERS);
        severityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSeverity.setAdapter(severityAdapter);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AppConstants.RISK_SORT_OPTIONS);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRiskSort.setAdapter(sortAdapter);

        SharedPreferences prefs = getSharedPreferences(AppPrefs.PREFS_NAME, MODE_PRIVATE);
        spSeverity.setSelection(UiFormat.indexOf(
                AppConstants.SEVERITY_FILTERS,
                prefs.getString(AppPrefs.KEY_DEFAULT_SEVERITY, AppPrefs.DEFAULT_SEVERITY)
        ));

        lvThreats.setAdapter(adapter);
        lvThreats.setOnItemClickListener((parent, view, position, id) -> openDetail(visibleEvents.get(position).getId()));
        btnRefresh.setOnClickListener(v -> loadEvents(true));
        AppNavigation.setup(this, bottomNavigation, R.id.nav_feed);

        spSeverity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                applyFilters();
            }
        });
        spRiskSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                applyFilters();
            }
        });
        swWatched.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());

        loadEvents(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            AppNavigation.select(bottomNavigation, R.id.nav_feed);
        }
        savedStates = database.getSavedStates();
        caseCounts = database.getCaseCountsByEvent();
        applyFilters();
    }

    private void loadEvents(boolean forceRefresh) {
        progressBar.setVisibility(View.VISIBLE);
        ThreatRepository.LoadCallback callback = (events, error) -> {
            progressBar.setVisibility(View.GONE);
            allEvents.clear();
            allEvents.addAll(events);
            savedStates = database.getSavedStates();
            caseCounts = database.getCaseCountsByEvent();
            applyFilters();
        };
        if (forceRefresh) {
            ThreatRepository.getInstance().refreshEvents(this, callback);
        } else {
            ThreatRepository.getInstance().loadEvents(this, callback);
        }
    }

    private void applyFilters() {
        if (adapter == null) {
            return;
        }
        if (savedStates == null) {
            savedStates = database.getSavedStates();
        }
        if (caseCounts == null) {
            caseCounts = database.getCaseCountsByEvent();
        }
        SharedPreferences prefs = getSharedPreferences(AppPrefs.PREFS_NAME, MODE_PRIVATE);
        boolean highConfidenceOnly = prefs.getBoolean(AppPrefs.KEY_HIGH_CONFIDENCE_ONLY, false);
        String severity = spSeverity.getSelectedItem() == null ? "Toate" : spSeverity.getSelectedItem().toString();
        String sortOrder = spRiskSort.getSelectedItem() == null ? "Ordine initiala" : spRiskSort.getSelectedItem().toString();
        boolean onlyWatched = swWatched.isChecked();

        visibleEvents.clear();
        for (ThreatEvent event : allEvents) {
            SavedEventState state = savedStates.get(event.getId());
            boolean matchesSeverity = "Toate".equals(severity) || event.getSeverity().equals(severity);
            boolean matchesWatched = !onlyWatched || (state != null && state.isWatched());
            boolean matchesConfidence = !highConfidenceOnly || event.getConfidence() >= 80;
            if (matchesSeverity && matchesWatched && matchesConfidence) {
                visibleEvents.add(event);
            }
        }
        sortVisibleEvents(sortOrder);
        adapter.setData(visibleEvents, savedStates, caseCounts);
        updateStatus();
    }

    private void sortVisibleEvents(String sortOrder) {
        if ("Risc descrescator".equals(sortOrder)) {
            Collections.sort(visibleEvents, riskComparator(false));
        } else if ("Risc crescator".equals(sortOrder)) {
            Collections.sort(visibleEvents, riskComparator(true));
        }
    }

    private Comparator<ThreatEvent> riskComparator(boolean ascending) {
        return (left, right) -> {
            int result = Integer.compare(left.getRiskScore(), right.getRiskScore());
            if (!ascending) {
                result = -result;
            }
            if (result != 0) {
                return result;
            }
            return right.getLastSeen().compareTo(left.getLastSeen());
        };
    }

    private void updateStatus() {
        if (tvStatus == null) {
            return;
        }
        if (allEvents.isEmpty()) {
            tvStatus.setText("Se incarca...");
            return;
        }
        tvStatus.setText("Afisate: " + visibleEvents.size() + " / " + allEvents.size());
    }

    private void openDetail(String eventId) {
        Intent intent = new Intent(this, ThreatDetailActivity.class);
        intent.putExtra(AppConstants.EXTRA_EVENT_ID, eventId);
        startActivity(intent);
    }
}
