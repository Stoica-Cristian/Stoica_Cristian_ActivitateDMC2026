package com.example.laborator11;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText etValues;
    private Button btnShowChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etValues = findViewById(R.id.etValues);
        btnShowChart = findViewById(R.id.btnShowChart);

        btnShowChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etValues.getText().toString().trim();
                if (input.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Introduceti niste valori!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String[] parts = input.split(",");
                    ArrayList<Float> values = new ArrayList<>();
                    for (String part : parts) {
                        values.add(Float.parseFloat(part.trim()));
                    }

                    if (values.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Format invalid!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float[] valuesArray = new float[values.size()];
                    for (int i = 0; i < values.size(); i++) {
                        valuesArray[i] = values.get(i);
                    }

                    Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                    intent.putExtra("values", valuesArray);
                    startActivity(intent);

                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Asigurati-va ca folositi doar numere si virgule!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
