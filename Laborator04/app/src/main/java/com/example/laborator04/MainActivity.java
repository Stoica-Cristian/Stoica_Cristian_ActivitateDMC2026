package com.example.laborator04;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<SportsClub> sportsClubs = new ArrayList<>();
    private SportsClubAdapter adapter;

    private final ActivityResultLauncher<Intent> addClubLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    SportsClub club = result.getData().getParcelableExtra(AddSportsClubActivity.EXTRA_CLUB, SportsClub.class);
                    int position = result.getData().getIntExtra(AddSportsClubActivity.EXTRA_POSITION, -1);
                    
                    if (club != null) {
                        if (position != -1) {
                            // Update existing object
                            sportsClubs.set(position, club);
                            adapter.notifyDataSetChanged();
                        } else {
                            // Add new object
                            adapter.add(club);
                        }
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

        adapter = new SportsClubAdapter(this, sportsClubs);
        lvSportsClubs.setAdapter(adapter);

        lvSportsClubs.setOnItemClickListener((parent, view, position, id) -> {
            SportsClub club = sportsClubs.get(position);
            Intent intent = new Intent(this, AddSportsClubActivity.class);
            intent.putExtra(AddSportsClubActivity.EXTRA_CLUB, club);
            intent.putExtra(AddSportsClubActivity.EXTRA_POSITION, position);
            addClubLauncher.launch(intent);
        });

        lvSportsClubs.setOnItemLongClickListener((parent, view, position, id) -> {
            adapter.remove(adapter.getItem(position));
            return true;
        });

        btnAddClub.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddSportsClubActivity.class);
            addClubLauncher.launch(intent);
        });
    }
}
