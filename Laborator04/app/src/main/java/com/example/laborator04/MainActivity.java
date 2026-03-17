package com.example.laborator04;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<SportsClub> sportsClubs = new ArrayList<>();
    private ArrayAdapter<SportsClub> adapter;

    private final ActivityResultLauncher<Intent> addClubLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    SportsClub club = result.getData().getParcelableExtra(AddSportsClubActivity.EXTRA_CLUB, SportsClub.class);
                    if (club != null) {
                        adapter.add(club);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvSportsClubs = findViewById(R.id.lvSportsClubs);
        Button btnAddClub = findViewById(R.id.btnAddClub);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sportsClubs);
        lvSportsClubs.setAdapter(adapter);

        lvSportsClubs.setOnItemClickListener((parent, view, position, id) -> {
            SportsClub club = sportsClubs.get(position);
            Toast.makeText(this, club.toString(), Toast.LENGTH_LONG).show();
        });

        btnAddClub.setOnClickListener(v -> {
            Toast.makeText(this, "Lungime: " + sportsClubs.size(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AddSportsClubActivity.class);
            addClubLauncher.launch(intent);
        });

        lvSportsClubs.setOnItemLongClickListener((parent, view, position, id) -> {
            adapter.remove(adapter.getItem(position));
            return true;
        });
    }
}
