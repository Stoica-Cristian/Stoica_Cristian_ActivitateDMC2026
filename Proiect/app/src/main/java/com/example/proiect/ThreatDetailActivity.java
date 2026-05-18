package com.example.proiect;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proiect.data.SavedEventState;
import com.example.proiect.data.ThreatDatabaseHelper;
import com.example.proiect.data.ThreatEvent;
import com.example.proiect.data.ThreatFeedParser;
import com.example.proiect.data.ThreatRepository;
import com.example.proiect.ui.UiFormat;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Locale;

public class ThreatDetailActivity extends AppCompatActivity {
    private TextView tvTitle;
    private TextView tvSeverityBadge;
    private TextView tvFamily;
    private TextView tvLocation;
    private TextView tvDates;
    private TextView tvConfidence;
    private TextView tvLifecycle;
    private TextView tvSummary;
    private TextView tvScore;
    private LinearLayout layoutTechniques;
    private LinearLayout layoutIocs;
    private LinearLayout layoutActions;
    private SwitchMaterial switchWatched;
    private RatingBar ratingBar;
    private ProgressBar progressLoading;
    private ProgressBar progressConfidence;
    private Button btnSource;
    private Button btnCase;
    private ThreatDatabaseHelper database;
    private ThreatEvent event;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threat_detail);

        eventId = getIntent().getStringExtra(AppConstants.EXTRA_EVENT_ID);
        database = ThreatDatabaseHelper.getInstance(this);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvSeverityBadge = findViewById(R.id.tvDetailSeverityBadge);
        tvFamily = findViewById(R.id.tvDetailFamily);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvDates = findViewById(R.id.tvDetailDates);
        tvConfidence = findViewById(R.id.tvDetailConfidence);
        tvLifecycle = findViewById(R.id.tvDetailLifecycle);
        tvSummary = findViewById(R.id.tvDetailSummary);
        tvScore = findViewById(R.id.tvDetailScore);
        layoutTechniques = findViewById(R.id.layoutDetailTechniques);
        layoutIocs = findViewById(R.id.layoutDetailIocs);
        layoutActions = findViewById(R.id.layoutDetailActions);
        switchWatched = findViewById(R.id.switchWatched);
        ratingBar = findViewById(R.id.ratingEvent);
        progressLoading = findViewById(R.id.progressDetail);
        progressConfidence = findViewById(R.id.progressConfidence);
        btnSource = findViewById(R.id.btnOpenSource);

        Button btnSave = findViewById(R.id.btnSaveEvent);
        btnCase = findViewById(R.id.btnCreateCaseFromEvent);
        Button btnBack = findViewById(R.id.btnBackDetail);

        btnSave.setOnClickListener(v -> saveState());
        btnCase.setOnClickListener(v -> openCase());
        btnBack.setOnClickListener(v -> finish());
        btnSource.setOnClickListener(v -> openSource());

        loadEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (event != null) {
            renderLifecycle();
        }
    }

    private void loadEvent() {
        progressLoading.setVisibility(View.VISIBLE);
        ThreatRepository.getInstance().loadEvents(this, (events, error) -> {
            progressLoading.setVisibility(View.GONE);
            event = ThreatFeedParser.findById(events, eventId);
            if (event == null) {
                event = database.getCachedEvent(eventId);
            }
            if (event == null) {
                Toast.makeText(this, "Incidentul nu a fost gasit.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            renderEvent();
        });
    }

    private void renderEvent() {
        int severityColor = UiFormat.severityColor(event.getSeverity());
        tvTitle.setText(event.getTitle());
        tvSeverityBadge.setText(event.getSeverity().toUpperCase(Locale.ROOT));
        tvSeverityBadge.setBackground(roundedBackground(severityColor, 8));
        tvFamily.setText("Familie loader: " + event.getLoaderFamily());
        tvLocation.setText(event.getLocationLabel());
        tvDates.setText(event.getFirstSeen() + " - " + event.getLastSeen());
        tvConfidence.setText("Incredere: " + event.getConfidence() + "%");
        progressConfidence.setProgress(event.getConfidence());
        tvSummary.setText(event.getSummary());
        tvScore.setText("Risc " + event.getRiskScore());

        renderIndexedRows(layoutTechniques, event.getTechniques(), "T", getColor(R.color.accent_blue));
        renderIocRows(layoutIocs, event.getIocs());
        renderIndexedRows(layoutActions, event.getDefensiveActions(), "PAS", getColor(R.color.accent_primary));

        boolean hasSource = event.getSourceUrl() != null && !event.getSourceUrl().isEmpty();
        btnSource.setEnabled(hasSource);
        btnSource.setAlpha(hasSource ? 1f : 0.45f);

        SavedEventState state = database.getSavedState(event.getId());
        if (state != null) {
            switchWatched.setChecked(state.isWatched());
            ratingBar.setRating(state.getRating());
        } else {
            switchWatched.setChecked(false);
            ratingBar.setRating(3f);
        }
        renderLifecycle();
    }

    private void renderLifecycle() {
        if (event == null || tvLifecycle == null) {
            return;
        }
        int openCount = database.getOpenCaseCountForEvent(event.getId());
        int closedCount = database.getClosedCaseCountForEvent(event.getId());
        int caseCount = openCount + closedCount;
        if (caseCount == 0) {
            tvLifecycle.setText("Stare investigatie: fara caz asociat");
            btnCase.setText("Creeaza caz de investigatie");
        } else if (openCount > 0) {
            tvLifecycle.setText("Stare investigatie: deschisa"
                    + " | cazuri deschise " + openCount
                    + " | inchise " + closedCount);
            btnCase.setText("Adauga caz nou");
        } else if (caseCount == 1) {
            tvLifecycle.setText("Stare investigatie: inchisa | 1 caz inchis");
            btnCase.setText("Adauga caz nou");
        } else {
            tvLifecycle.setText("Stare investigatie: inchisa | " + caseCount + " cazuri inchise");
            btnCase.setText("Adauga caz nou");
        }
    }

    private void renderIndexedRows(LinearLayout container, List<String> values, String prefix, int badgeColor) {
        container.removeAllViews();
        if (values == null || values.isEmpty()) {
            addEmptyRow(container);
            return;
        }

        for (int i = 0; i < values.size(); i++) {
            LinearLayout row = createRow(i);
            row.addView(createBadge(prefix + (i + 1), badgeColor, Color.WHITE, dp(52)));
            row.addView(createValue(values.get(i), false));
            container.addView(row);
        }
    }

    private void renderIocRows(LinearLayout container, List<String> values) {
        container.removeAllViews();
        if (values == null || values.isEmpty()) {
            addEmptyRow(container);
            return;
        }

        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            LinearLayout row = createRow(i);
            row.addView(createBadge(inferIocType(value), getColor(R.color.accent_warm), Color.WHITE, dp(76)));
            row.addView(createValue(value, true));
            container.addView(row);
        }
    }

    private LinearLayout createRow(int index) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = index == 0 ? 0 : dp(8);
        row.setLayoutParams(params);
        return row;
    }

    private TextView createBadge(String text, int backgroundColor, int textColor, int minWidth) {
        TextView badge = new TextView(this);
        badge.setGravity(Gravity.CENTER);
        badge.setMinWidth(minWidth);
        badge.setPadding(dp(8), dp(4), dp(8), dp(4));
        badge.setText(text);
        badge.setTextColor(textColor);
        badge.setTextSize(11);
        badge.setTypeface(Typeface.DEFAULT_BOLD);
        badge.setBackground(roundedBackground(backgroundColor, 6));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.rightMargin = dp(10);
        badge.setLayoutParams(params);
        return badge;
    }

    private TextView createValue(String text, boolean monospace) {
        TextView value = new TextView(this);
        value.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        value.setLineSpacing(dp(2), 1f);
        value.setText(text);
        value.setTextColor(getColor(R.color.text_primary));
        value.setTextSize(14);
        if (monospace) {
            value.setTypeface(Typeface.MONOSPACE);
            value.setTextIsSelectable(true);
        }
        return value;
    }

    private void addEmptyRow(LinearLayout container) {
        TextView empty = new TextView(this);
        empty.setText("Nu exista date pentru aceasta sectiune.");
        empty.setTextColor(getColor(R.color.text_muted));
        empty.setTextSize(14);
        container.addView(empty);
    }

    private String inferIocType(String value) {
        String safeValue = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        if (safeValue.startsWith("http://") || safeValue.startsWith("https://")) {
            return "URL";
        }
        if (safeValue.matches("\\d{1,3}(\\.\\d{1,3}){3}")) {
            return "IP";
        }
        if (safeValue.matches("[a-f0-9]{32,64}")) {
            return "HASH";
        }
        if (safeValue.contains(".")) {
            return "DOMENIU";
        }
        return "IOC";
    }

    private GradientDrawable roundedBackground(int color, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        return drawable;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void saveState() {
        if (event == null) {
            return;
        }
        if (trySaveState()) {
            Toast.makeText(this, "Incident salvat.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incidentul nu a putut fi salvat.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCase() {
        if (event == null) {
            return;
        }
        if (!trySaveState()) {
            Toast.makeText(this, "Se deschide formularul fara salvarea incidentului.", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, AddEditCaseActivity.class);
        intent.putExtra(AppConstants.EXTRA_EVENT_ID, event.getId());
        startActivity(intent);
    }

    private boolean trySaveState() {
        try {
            database.saveEventState(event, switchWatched.isChecked(), ratingBar.getRating());
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void openSource() {
        if (event == null || event.getSourceUrl() == null || event.getSourceUrl().isEmpty()) {
            return;
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(event.getSourceUrl())));
        } catch (Exception e) {
            Toast.makeText(this, "Nu se poate deschide referinta.", Toast.LENGTH_SHORT).show();
        }
    }
}
