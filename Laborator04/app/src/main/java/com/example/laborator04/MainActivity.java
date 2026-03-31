package com.example.laborator04;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final List<SportsClub> sportsClubs = new ArrayList<>();
    private SportsClubAdapter adapter;
    private static final String FILE_NAME = "sports_clubs.txt";
    private static final String FAVORITES_FILE = "favorites.txt";

    private final ActivityResultLauncher<Intent> addClubLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    SportsClub club = result.getData().getParcelableExtra(AddSportsClubActivity.EXTRA_CLUB, SportsClub.class);
                    int position = result.getData().getIntExtra(AddSportsClubActivity.EXTRA_POSITION, -1);
                    
                    if (club != null) {
                        if (position != -1) {
                            sportsClubs.set(position, club);
                            adapter.notifyDataSetChanged();
                        } else {
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
        Button btnSettings = findViewById(R.id.btnSettings);

        adapter = new SportsClubAdapter(this, sportsClubs);
        lvSportsClubs.setAdapter(adapter);

        loadClubsFromFile();

        lvSportsClubs.setOnItemClickListener((parent, view, position, id) -> {
            SportsClub club = sportsClubs.get(position);
            Intent intent = new Intent(this, AddSportsClubActivity.class);
            intent.putExtra(AddSportsClubActivity.EXTRA_CLUB, club);
            intent.putExtra(AddSportsClubActivity.EXTRA_POSITION, position);
            addClubLauncher.launch(intent);
        });

        lvSportsClubs.setOnItemLongClickListener((parent, view, position, id) -> {
            SportsClub club = sportsClubs.get(position);
            saveToFavorites(club);
            return true;
        });

        btnAddClub.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddSportsClubActivity.class);
            addClubLauncher.launch(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void loadClubsFromFile() {
        try (FileInputStream fis = openFileInput(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            
            String line;
            StringBuilder clubData = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.equals("---")) {
                    SportsClub club = parseClub(clubData.toString());
                    if (club != null) {
                        sportsClubs.add(club);
                    }
                    clubData = new StringBuilder();
                } else {
                    clubData.append(line).append("\n");
                }
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SportsClub parseClub(String data) {
        try {
            String[] lines = data.trim().split("\n");
            String name = lines[0].replace("Name: ", "").trim();
            int members = Integer.parseInt(lines[1].replace("Members: ", "").trim());
            boolean isPrivate = lines[2].replace("Private: ", "").trim().equals("Yes");
            SportType type = SportType.valueOf(lines[3].replace("Sport: ", "").trim());
            float rating = Float.parseFloat(lines[4].replace("Rating: ", "").trim());
            boolean hasEquipment = lines[5].replace("Equipment: ", "").trim().equals("Yes");
            String category = lines[6].replace("Category: ", "").trim();
            boolean isOpen = lines[7].replace("Status: ", "").trim().equals("Open");
            
            String dateStr = lines[8].replace("Date: ", "").trim();
            Date date = null;
            if (!dateStr.equals("N/A")) {
                date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr);
            }
            
            return new SportsClub(name, members, isPrivate, type, rating, hasEquipment, category, isOpen, date);
        } catch (Exception e) {
            return null;
        }
    }

    private void saveToFavorites(SportsClub club) {
        try (FileOutputStream fos = openFileOutput(FAVORITES_FILE, Context.MODE_APPEND);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            osw.write(club.toString() + "\n---\n");
            Toast.makeText(this, "Saved to favorites!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
