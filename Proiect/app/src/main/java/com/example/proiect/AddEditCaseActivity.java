package com.example.proiect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proiect.data.CaseNote;
import com.example.proiect.data.SavedEventState;
import com.example.proiect.data.ThreatDatabaseHelper;
import com.example.proiect.data.ThreatEvent;
import com.example.proiect.data.ThreatFeedParser;
import com.example.proiect.data.ThreatRepository;
import com.example.proiect.ui.UiFormat;

import java.util.Calendar;

public class AddEditCaseActivity extends AppCompatActivity {
    private EditText etTitle;
    private EditText etNotes;
    private EditText etResolution;
    private Spinner spStatus;
    private CheckBox cbNotify;
    private RatingBar ratingBar;
    private CalendarView calendarView;
    private TextView tvEvent;
    private TextView tvDueDate;
    private TextView tvResolutionLabel;
    private Button btnDelete;
    private Button btnOpenRelatedEvent;
    private ThreatDatabaseHelper database;
    private long caseId;
    private String eventId;
    private String currentClosedAt = "";
    private long selectedDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_case);

        database = ThreatDatabaseHelper.getInstance(this);
        caseId = getIntent().getLongExtra(AppConstants.EXTRA_CASE_ID, 0);
        eventId = getIntent().getStringExtra(AppConstants.EXTRA_EVENT_ID);
        selectedDueDate = System.currentTimeMillis();

        etTitle = findViewById(R.id.etCaseTitle);
        etNotes = findViewById(R.id.etCaseNotes);
        etResolution = findViewById(R.id.etCaseResolution);
        spStatus = findViewById(R.id.spCaseStatus);
        cbNotify = findViewById(R.id.cbNotify);
        ratingBar = findViewById(R.id.ratingCase);
        calendarView = findViewById(R.id.calendarDueDate);
        tvEvent = findViewById(R.id.tvRelatedEvent);
        tvDueDate = findViewById(R.id.tvDueDate);
        tvResolutionLabel = findViewById(R.id.tvResolutionLabel);
        btnDelete = findViewById(R.id.btnDeleteCase);
        btnOpenRelatedEvent = findViewById(R.id.btnOpenRelatedEvent);
        Button btnSave = findViewById(R.id.btnSaveCase);
        Button btnBack = findViewById(R.id.btnBackCaseForm);

        setupSpinner(spStatus, AppConstants.CASE_STATUSES);
        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateResolutionVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateResolutionVisibility();
            }
        });

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth, 12, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            selectedDueDate = calendar.getTimeInMillis();
            updateDueDateLabel();
        });

        btnSave.setOnClickListener(v -> saveCase());
        btnBack.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> deleteCase());
        btnOpenRelatedEvent.setOnClickListener(v -> openRelatedEvent());

        if (caseId > 0) {
            if (!loadExistingCase()) {
                return;
            }
        } else {
            initNewCase();
        }
        resolveEventLabel();
    }

    private void setupSpinner(Spinner spinner, String[] values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void initNewCase() {
        ratingBar.setRating(3f);
        applySavedIncidentState();
        calendarView.setDate(selectedDueDate, false, true);
        updateDueDateLabel();
        updateResolutionVisibility();
        btnDelete.setVisibility(View.GONE);
    }

    private void applySavedIncidentState() {
        if (eventId == null || eventId.trim().isEmpty()) {
            return;
        }
        SavedEventState state = database.getSavedState(eventId);
        if (state == null) {
            return;
        }
        cbNotify.setChecked(state.isWatched());
        ratingBar.setRating(state.getRating());
    }

    private boolean loadExistingCase() {
        CaseNote note = database.getCase(caseId);
        if (note == null) {
            Toast.makeText(this, "Cazul nu a fost gasit.", Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        eventId = note.getEventId();
        currentClosedAt = note.getClosedAt() == null ? "" : note.getClosedAt();
        etTitle.setText(note.getTitle());
        etNotes.setText(note.getNotes());
        etResolution.setText(note.getResolution());
        spStatus.setSelection(UiFormat.indexOf(AppConstants.CASE_STATUSES, note.getStatus()));
        cbNotify.setChecked(note.isNotify());
        ratingBar.setRating(note.getRating());
        selectedDueDate = note.getDueDate();
        calendarView.setDate(selectedDueDate, false, true);
        updateDueDateLabel();
        updateResolutionVisibility();
        btnDelete.setVisibility(View.VISIBLE);
        return true;
    }

    private void resolveEventLabel() {
        if (eventId == null || eventId.trim().isEmpty()) {
            tvEvent.setText("Caz manual fara incident asociat.");
            btnOpenRelatedEvent.setVisibility(View.GONE);
            return;
        }
        tvEvent.setText("Incident asociat: " + eventId);
        btnOpenRelatedEvent.setVisibility(View.VISIBLE);
        ThreatRepository.getInstance().loadEvents(this, (events, error) -> {
            ThreatEvent event = ThreatFeedParser.findById(events, eventId);
            if (event == null) {
                event = database.getCachedEvent(eventId);
            }
            if (event != null) {
                tvEvent.setText("Incident asociat: " + event.getTitle());
                if (caseId == 0 && etTitle.getText().toString().trim().isEmpty()) {
                    etTitle.setText("Investigatie: " + event.getLoaderFamily());
                }
            }
        });
    }

    private void saveCase() {
        String title = etTitle.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String status = spStatus.getSelectedItem().toString();
        boolean closed = isClosedStatus(status);
        String resolution = closed ? etResolution.getText().toString().trim() : "";

        if (title.isEmpty()) {
            etTitle.setError("Titlul este obligatoriu");
            return;
        }
        if (closed && resolution.isEmpty()) {
            etResolution.setError("Concluzia este obligatorie la inchidere");
            updateResolutionVisibility();
            return;
        }
        if (notes.isEmpty()) {
            notes = "Caz creat pentru triere.";
        }

        CaseNote note = new CaseNote();
        note.setId(caseId);
        note.setTitle(title);
        note.setEventId(eventId);
        note.setStatus(status);
        note.setDueDate(selectedDueDate);
        note.setNotify(cbNotify.isChecked());
        note.setRating(ratingBar.getRating());
        note.setNotes(notes);
        note.setResolution(resolution);
        note.setClosedAt(currentClosedAt);

        long savedId = database.upsertCase(note);
        caseId = savedId;
        btnDelete.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Caz salvat.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void deleteCase() {
        if (caseId > 0) {
            database.deleteCase(caseId);
            Toast.makeText(this, "Caz sters.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void openRelatedEvent() {
        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(this, "Cazul nu are incident asociat.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, ThreatDetailActivity.class);
        intent.putExtra(AppConstants.EXTRA_EVENT_ID, eventId);
        startActivity(intent);
    }

    private void updateDueDateLabel() {
        tvDueDate.setText("Termen analiza: " + UiFormat.formatDate(selectedDueDate));
    }

    private void updateResolutionVisibility() {
        String status = spStatus.getSelectedItem() == null ? "" : spStatus.getSelectedItem().toString();
        int visibility = isClosedStatus(status) ? View.VISIBLE : View.GONE;
        tvResolutionLabel.setVisibility(visibility);
        etResolution.setVisibility(visibility);
        if (visibility == View.GONE) {
            etResolution.setError(null);
        }
    }

    private boolean isClosedStatus(String status) {
        return "Inchis".equalsIgnoreCase(status);
    }
}
