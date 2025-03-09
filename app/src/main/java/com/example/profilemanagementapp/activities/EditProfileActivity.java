package com.example.profilemanagementapp.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.profilemanagementapp.R;
import com.example.profilemanagementapp.database.DatabaseHelper;
import com.example.profilemanagementapp.utils.EncryptionHelper;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etFullName, etDob, etAddress, etPhone, etUsername, etPassword;
    private Button btnSaveChanges, btnDeleteProfile;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        etFullName = findViewById(R.id.etFullName);
        etDob = findViewById(R.id.etDob);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnDeleteProfile = findViewById(R.id.btnDeleteProfile);

        loadUserProfile();
        btnSaveChanges.setOnClickListener(v -> saveChanges());
        btnDeleteProfile.setOnClickListener(v -> confirmDeleteProfile());
    }

    private void loadUserProfile() {
        dbHelper = new DatabaseHelper(this); // Initialize dbHelper before using it
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            etFullName.setText(cursor.getString(1));
            etDob.setText(cursor.getString(2));
            etAddress.setText(cursor.getString(3));
            etPhone.setText(cursor.getString(4));
            etUsername.setText(cursor.getString(5));
        }
        cursor.close();
    }


    private void saveChanges() {
        String password = etPassword.getText().toString().trim();
        ContentValues values = new ContentValues();

        if (!password.isEmpty()) {
            values.put("password", EncryptionHelper.hashPassword(password));
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updatedRows = db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
        if (updatedRows > 0) {
            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteProfile() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete your profile? This action is permanent.")
                .setPositiveButton("Yes", (dialog, which) -> deleteProfile())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProfile() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = db.delete("users", "id = ?", new String[]{String.valueOf(userId)});

        if (deletedRows > 0) {
            Toast.makeText(this, "Profile deleted", Toast.LENGTH_SHORT).show();
            finishAffinity();
        } else {
            Toast.makeText(this, "Failed to delete profile", Toast.LENGTH_SHORT).show();
        }
    }
}