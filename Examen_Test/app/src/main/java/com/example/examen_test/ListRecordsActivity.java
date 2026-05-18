package com.example.examen_test;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ListRecordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_records);

        ListView lvInregistrari = findViewById(R.id.lvInregistrari);

        // Date de test
        List<InterventieAuto> lista = new ArrayList<>();
        lista.add(new InterventieAuto("Dacia Logan", 500.0f, 2, true, "Ion Popescu"));
        lista.add(new InterventieAuto("Renault Clio", 350.5f, 1, false, "Vasile Ionescu"));

        ArrayAdapter<InterventieAuto> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        lvInregistrari.setAdapter(adapter);
    }
}
