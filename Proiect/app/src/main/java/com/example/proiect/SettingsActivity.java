package com.example.proiect;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proiect.data.AppPrefs;
import com.example.proiect.ui.UiFormat;

public class SettingsActivity extends AppCompatActivity {
    private Spinner spDefaultSeverity;
    private CheckBox cbHighConfidence;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(AppPrefs.PREFS_NAME, MODE_PRIVATE);
        spDefaultSeverity = findViewById(R.id.spDefaultSeverity);
        cbHighConfidence = findViewById(R.id.cbHighConfidence);
        Button btnSave = findViewById(R.id.btnSaveSettings);
        Button btnReset = findViewById(R.id.btnResetSettings);
        Button btnBack = findViewById(R.id.btnBackSettings);

        ArrayAdapter<String> severityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AppConstants.SEVERITY_FILTERS);
        severityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDefaultSeverity.setAdapter(severityAdapter);

        loadSettings();

        btnSave.setOnClickListener(v -> saveSettings());
        btnReset.setOnClickListener(v -> resetSettings());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        String severity = prefs.getString(AppPrefs.KEY_DEFAULT_SEVERITY, AppPrefs.DEFAULT_SEVERITY);
        spDefaultSeverity.setSelection(UiFormat.indexOf(AppConstants.SEVERITY_FILTERS, severity));
        cbHighConfidence.setChecked(prefs.getBoolean(AppPrefs.KEY_HIGH_CONFIDENCE_ONLY, false));
    }

    private void saveSettings() {
        prefs.edit()
                .putString(AppPrefs.KEY_DEFAULT_SEVERITY, spDefaultSeverity.getSelectedItem().toString())
                .putBoolean(AppPrefs.KEY_HIGH_CONFIDENCE_ONLY, cbHighConfidence.isChecked())
                .apply();
        Toast.makeText(this, "Setari salvate.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void resetSettings() {
        prefs.edit()
                .putString(AppPrefs.KEY_DEFAULT_SEVERITY, AppPrefs.DEFAULT_SEVERITY)
                .putBoolean(AppPrefs.KEY_HIGH_CONFIDENCE_ONLY, false)
                .apply();
        loadSettings();
        Toast.makeText(this, "Setarile au fost resetate.", Toast.LENGTH_SHORT).show();
    }
}
