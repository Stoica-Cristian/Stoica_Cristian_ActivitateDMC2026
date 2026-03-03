package com.example.laborator03;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "LifecycleCheck";

    private final ActivityResultLauncher<Intent> thirdActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String mesaj = data.getStringExtra("raspuns");
                    int suma = data.getIntExtra("suma", 0);

                    String text = mesaj + "\nSuma: " + suma;
                    Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.d(TAG, "onCreate() apelat - DEBUG");

        Button btnOpenThird = findViewById(R.id.btnOpenThird);
        btnOpenThird.setOnClickListener(v -> {
            Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
            
            Bundle bundle = new Bundle();
            bundle.putString("mesaj", "Mesaj din Activitatea 2");
            bundle.putInt("int1", 15);
            bundle.putInt("int2", 25);
            intent.putExtras(bundle);
            
            thirdActivityLauncher.launch(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart() apelat - INFO");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume() apelat - VERBOSE");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(TAG, "onPause() apelat - WARNING");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop() apelat - ERROR");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() apelat - DEBUG");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart() apelat - INFO");
    }
}
