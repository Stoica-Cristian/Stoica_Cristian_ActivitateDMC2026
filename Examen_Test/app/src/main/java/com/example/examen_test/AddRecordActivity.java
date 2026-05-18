package com.example.examen_test;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddRecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        EditText etModel = findViewById(R.id.etModel);
        EditText etPret = findViewById(R.id.etPret);
        EditText etDurata = findViewById(R.id.etDurata);
        CheckBox cbPiese = findViewById(R.id.cbPiese);
        EditText etMecanic = findViewById(R.id.etMecanic);
        Button btnSalveaza = findViewById(R.id.btnSalveaza);

        btnSalveaza.setOnClickListener(v -> {
            String model = etModel.getText().toString();
            String pretStr = etPret.getText().toString();
            String durataStr = etDurata.getText().toString();
            boolean piese = cbPiese.isChecked();
            String mecanic = etMecanic.getText().toString();

            if (model.isEmpty() || pretStr.isEmpty() || durataStr.isEmpty() || mecanic.isEmpty()) {
                Toast.makeText(this, "Completati toate campurile!", Toast.LENGTH_SHORT).show();
                return;
            }

            float pret = Float.parseFloat(pretStr);
            int durata = Integer.parseInt(durataStr);

            InterventieAuto interventie = new InterventieAuto(model, pret, durata, piese, mecanic);
            
            // Momentan doar afisam un mesaj. Salvarea efectiva se va face ulterior (ex: intent result sau baza de date)
            Toast.makeText(this, "Inregistrare salvata: " + interventie.toString(), Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
