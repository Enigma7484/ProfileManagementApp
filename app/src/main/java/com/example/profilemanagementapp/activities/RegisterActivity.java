package com.example.profilemanagementapp.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.profilemanagementapp.R;
import com.example.profilemanagementapp.database.DatabaseHelper;
import com.example.profilemanagementapp.utils.EncryptionHelper;
import com.google.android.material.snackbar.Snackbar;

public class RegisterActivity extends AppCompatActivity {
    private EditText etFullName, etDob, etAddress, etPhone, etUsername, etPassword;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        etFullName = findViewById(R.id.etFullName);
        etDob = findViewById(R.id.etDob);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[@#$%^&+=].*")) {
            etPassword.setError("Password must be at least 8 chars, include a capital letter & special character");
            return;
        }

        String hashedPassword = EncryptionHelper.hashPassword(password);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", hashedPassword);

        long result = db.insert("users", null, values);
        if (result != -1) {
            Snackbar.make(btnRegister, "Registration Successful", Snackbar.LENGTH_SHORT).show();
            finish();
        } else {
            Snackbar.make(btnRegister, "Registration Failed", Snackbar.LENGTH_SHORT).show();
        }
    }
}