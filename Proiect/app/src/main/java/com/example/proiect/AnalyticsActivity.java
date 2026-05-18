package com.example.proiect;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proiect.data.ThreatDatabaseHelper;
import com.example.proiect.data.ThreatEvent;
import com.example.proiect.data.ThreatRepository;
import com.example.proiect.ui.ThreatChartView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity {
    private final List<ThreatEvent> events = new ArrayList<>();
    private ThreatChartView chartSeverity;
    private ThreatChartView chartFamilies;
    private ThreatChartView chartConfidence;
    private TextView tvSummary;
    private Spinner spChartType;
    private ProgressBar progressBar;
    private View bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        chartSeverity = findViewById(R.id.chartSeverity);
        chartFamilies = findViewById(R.id.chartFamilies);
        chartConfidence = findViewById(R.id.chartConfidence);
        tvSummary = findViewById(R.id.tvAnalyticsSummary);
        spChartType = findViewById(R.id.spChartType);
        progressBar = findViewById(R.id.progressAnalytics);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        String[] chartTypes = {"Bar", "Column", "Piechart"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, chartTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spChartType.setAdapter(adapter);
        spChartType.setSelection(1);
        spChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                renderChart();
            }
        });

        AppNavigation.setup(this, bottomNavigation, R.id.nav_analytics);
        loadEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            AppNavigation.select(bottomNavigation, R.id.nav_analytics);
        }
    }

    private void loadEvents() {
        progressBar.setVisibility(View.VISIBLE);
        ThreatRepository.getInstance().loadEvents(this, (loadedEvents, error) -> {
            progressBar.setVisibility(View.GONE);
            events.clear();
            events.addAll(loadedEvents);
            renderChart();
        });
    }

    private void renderChart() {
        if (chartSeverity == null || chartFamilies == null || chartConfidence == null || events.isEmpty() || spChartType.getSelectedItem() == null) {
            return;
        }
        String chartType = spChartType.getSelectedItem().toString();
        Map<String, Integer> severityCounts = countSeverity();
        Map<String, Integer> familyCounts = countFamilies();
        Map<String, Integer> confidenceCounts = countConfidence();

        chartSeverity.setChartData("Incidente pe severitate", chartType, toLabels(severityCounts), toValues(severityCounts));
        chartFamilies.setChartData("Distributie familii loader", chartType, toLabels(familyCounts), toValues(familyCounts));
        chartConfidence.setChartData("Nivel de incredere", chartType, toLabels(confidenceCounts), toValues(confidenceCounts));

        ThreatDatabaseHelper db = ThreatDatabaseHelper.getInstance(this);
        tvSummary.setText("Incidente analizate: " + events.size()
                + "\nCazuri: " + db.getCaseCount());
    }

    private Map<String, Integer> countSeverity() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("Ridicat", 0);
        counts.put("Mediu", 0);
        counts.put("Scazut", 0);
        for (ThreatEvent event : events) {
            counts.put(event.getSeverity(), counts.containsKey(event.getSeverity()) ? counts.get(event.getSeverity()) + 1 : 1);
        }
        return counts;
    }

    private Map<String, Integer> countFamilies() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (ThreatEvent event : events) {
            String family = event.getLoaderFamily();
            counts.put(family, counts.containsKey(family) ? counts.get(family) + 1 : 1);
        }
        return counts;
    }

    private Map<String, Integer> countConfidence() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("60-74", 0);
        counts.put("75-89", 0);
        counts.put("90-100", 0);
        for (ThreatEvent event : events) {
            if (event.getConfidence() >= 90) {
                counts.put("90-100", counts.get("90-100") + 1);
            } else if (event.getConfidence() >= 75) {
                counts.put("75-89", counts.get("75-89") + 1);
            } else {
                counts.put("60-74", counts.get("60-74") + 1);
            }
        }
        return counts;
    }

    private String[] toLabels(Map<String, Integer> counts) {
        return counts.keySet().toArray(new String[0]);
    }

    private int[] toValues(Map<String, Integer> counts) {
        int[] values = new int[counts.size()];
        int i = 0;
        for (Integer value : counts.values()) {
            values[i++] = value;
        }
        return values;
    }
}
