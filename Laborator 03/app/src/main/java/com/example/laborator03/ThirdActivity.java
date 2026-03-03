package com.example.laborator03;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            String mesaj = bundle.getString("mesaj");
            int val1 = bundle.getInt("int1", 0);
            int val2 = bundle.getInt("int2", 0);

            String text = mesaj + "\nValori: " + val1 + ", " + val2;
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();

            Button btnBack = findViewById(R.id.btnBackToSecond);
            btnBack.setOnClickListener(v -> {
                Intent it = new Intent();
                it.putExtra("raspuns", "Raspuns");
                it.putExtra("suma", val1 + val2);

                setResult(RESULT_OK, it);
                finish();
            });
        }
    }
}
