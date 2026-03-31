package com.example.laborator04;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText etTextSize;
    private Spinner spTextColor;
    private static final String PREFS_NAME = "AppSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etTextSize = findViewById(R.id.etTextSize);
        spTextColor = findViewById(R.id.spTextColor);
        Button btnSaveSettings = findViewById(R.id.btnSaveSettings);

        String[] colors = {"Black", "Red", "Blue", "Green"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTextColor.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        etTextSize.setText(String.valueOf(prefs.getFloat("textSize", 18f)));
        String savedColor = prefs.getString("textColor", "Black");
        spTextColor.setSelection(adapter.getPosition(savedColor));

        btnSaveSettings.setOnClickListener(v -> {
            float size = Float.parseFloat(etTextSize.getText().toString());
            String color = spTextColor.getSelectedItem().toString();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("textSize", size);
            editor.putString("textColor", color);
            editor.apply();

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
