package com.example.laborator04;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddSportsClubActivity extends AppCompatActivity {

    public static final String EXTRA_CLUB = "com.example.laborator04.EXTRA_CLUB";

    private EditText etClubName, etMemberCount, etEstablishmentDate;
    private CheckBox cbHasEquipment;
    private RadioGroup rgCategory;
    private Spinner spSportType;
    private RatingBar rbRating;
    private MaterialSwitch swIsPrivate;
    private ToggleButton tbIsOpen;
    private Button btnSave;
    
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sports_club);

        initializeViews();
        setupSpinner();
        setupDatePicker();

        btnSave.setOnClickListener(v -> {
            String name = etClubName.getText().toString();
            int members = Integer.parseInt(etMemberCount.getText().toString().isEmpty() ? "0" : etMemberCount.getText().toString());
            boolean hasEquipment = cbHasEquipment.isChecked();
            
            int selectedId = rgCategory.getCheckedRadioButtonId();
            String category = "";
            if (selectedId != -1) {
                RadioButton rb = findViewById(selectedId);
                category = rb.getText().toString();
            }

            SportType sportType = (SportType) spSportType.getSelectedItem();
            float rating = rbRating.getRating();
            boolean isPrivate = swIsPrivate.isChecked();
            boolean isOpen = tbIsOpen.isChecked();
            Date date = calendar.getTime();

            SportsClub club = new SportsClub(name, members, isPrivate, sportType, rating, hasEquipment, category, isOpen, date);

            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_CLUB, club);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void initializeViews() {
        etClubName = findViewById(R.id.etClubName);
        etMemberCount = findViewById(R.id.etMemberCount);
        etEstablishmentDate = findViewById(R.id.etEstablishmentDate);
        cbHasEquipment = findViewById(R.id.cbHasEquipment);
        rgCategory = findViewById(R.id.rgCategory);
        spSportType = findViewById(R.id.spSportType);
        rbRating = findViewById(R.id.rbRating);
        swIsPrivate = findViewById(R.id.swIsPrivate);
        tbIsOpen = findViewById(R.id.tbIsOpen);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupSpinner() {
        ArrayAdapter<SportType> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, SportType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSportType.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etEstablishmentDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etEstablishmentDate.setText(dateFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }
}
