package com.example.profilemanagementapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.profilemanagementapp.R;
import com.example.profilemanagementapp.database.DatabaseHelper;
import com.example.profilemanagementapp.models.DiaryEntry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditDiaryActivity extends AppCompatActivity {
    private EditText etTitle, etContent;
    private Button btnSave, btnDelete;
    private DatabaseHelper dbHelper;
    private int userId, entryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_diary);

        dbHelper = new DatabaseHelper(this);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        userId = getIntent().getIntExtra("userId", -1);
        entryId = getIntent().getIntExtra("entryId", -1);

        if (entryId != -1) {
            loadDiaryEntry();
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> saveEntry());
        // Instead of directly deleting, show confirmation dialog
        btnDelete.setOnClickListener(v -> confirmDeleteEntry());
    }

    private void loadDiaryEntry() {
        DiaryEntry entry = dbHelper.getDiaryEntry(entryId);
        if (entry != null) {
            etTitle.setText(entry.getTitle());
            etContent.setText(entry.getContent());
        }
    }

    private void saveEntry() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and Content are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (entryId == -1) {
            DiaryEntry newEntry = new DiaryEntry(userId, title, content, timestamp);
            if (dbHelper.addDiaryEntry(newEntry)) {
                Toast.makeText(this, "Entry added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add entry", Toast.LENGTH_SHORT).show();
            }
        } else {
            DiaryEntry updatedEntry = new DiaryEntry(entryId, userId, title, content, timestamp);
            dbHelper.updateDiaryEntry(updatedEntry);
            Toast.makeText(this, "Entry updated successfully", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    // New confirmation dialog for diary entry deletion.
    private void confirmDeleteEntry() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this diary entry?")
                .setPositiveButton("Yes", (dialog, which) -> deleteEntry())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEntry() {
        if (dbHelper.deleteDiaryEntry(entryId)) {
            Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to delete entry", Toast.LENGTH_SHORT).show();
        }
    }
}