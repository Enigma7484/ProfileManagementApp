package com.example.profilemanagementapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.profilemanagementapp.R;
import com.example.profilemanagementapp.database.DatabaseHelper;
import com.example.profilemanagementapp.models.DiaryEntry;
import com.example.profilemanagementapp.adapters.DiaryAdapter;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button btnAddEntry;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        btnAddEntry = findViewById(R.id.btnAddEntry);
        userId = getIntent().getIntExtra("userId", -1);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnAddEntry.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditDiaryActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        loadDiaryEntries();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDiaryEntries();
    }

    private void loadDiaryEntries() {
        List<DiaryEntry> entries = dbHelper.getDiaryEntries(userId);
        DiaryAdapter adapter = new DiaryAdapter(this, entries, userId);
        recyclerView.setAdapter(adapter);
    }
}