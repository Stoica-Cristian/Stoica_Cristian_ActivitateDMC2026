package com.example.proiect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proiect.data.CaseNote;
import com.example.proiect.data.ThreatDatabaseHelper;
import com.example.proiect.ui.CaseNoteAdapter;

import java.util.List;

public class CasesActivity extends AppCompatActivity {
    private ThreatDatabaseHelper database;
    private CaseNoteAdapter adapter;
    private TextView tvStatus;
    private View bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cases);

        database = ThreatDatabaseHelper.getInstance(this);
        adapter = new CaseNoteAdapter(this);
        tvStatus = findViewById(R.id.tvCasesStatus);

        ListView lvCases = findViewById(R.id.lvCases);
        Button btnAdd = findViewById(R.id.btnAddCase);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        lvCases.setAdapter(adapter);
        lvCases.setOnItemClickListener((parent, view, position, id) -> openCase(adapter.getItem(position).getId()));
        lvCases.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmDelete(adapter.getItem(position));
            return true;
        });

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddEditCaseActivity.class)));
        AppNavigation.setup(this, bottomNavigation, R.id.nav_cases);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            AppNavigation.select(bottomNavigation, R.id.nav_cases);
        }
        loadCases();
    }

    private void loadCases() {
        List<CaseNote> cases = database.getAllCases();
        adapter.setData(cases);
        if (cases.isEmpty()) {
            tvStatus.setText("Nu exista cazuri.");
            return;
        }

        int closedCount = 0;
        for (CaseNote note : cases) {
            if ("Inchis".equalsIgnoreCase(note.getStatus())) {
                closedCount++;
            }
        }
        int openCount = cases.size() - closedCount;
        tvStatus.setText("Total: " + cases.size()
                + " | Deschise: " + openCount
                + " | Inchise: " + closedCount);
    }

    private void openCase(long caseId) {
        Intent intent = new Intent(this, AddEditCaseActivity.class);
        intent.putExtra(AppConstants.EXTRA_CASE_ID, caseId);
        startActivity(intent);
    }

    private void confirmDelete(CaseNote note) {
        new AlertDialog.Builder(this)
                .setTitle("Stergere caz")
                .setMessage("Stergi cazul \"" + note.getTitle() + "\"?")
                .setPositiveButton("Sterge", (dialog, which) -> {
                    database.deleteCase(note.getId());
                    Toast.makeText(this, "Caz sters.", Toast.LENGTH_SHORT).show();
                    loadCases();
                })
                .setNegativeButton("Anuleaza", null)
                .show();
    }
}
