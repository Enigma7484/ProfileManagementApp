package com.example.profilemanagementapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.profilemanagementapp.R;
import com.example.profilemanagementapp.database.DatabaseHelper;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvFullName, tvDob, tvAddress, tvPhone;
    private Button btnEditProfile, btnViewDiary, btnLogout;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        tvFullName = findViewById(R.id.tvFullName);
        tvDob = findViewById(R.id.tvDob);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnViewDiary = findViewById(R.id.btnViewDiary);
        btnLogout = findViewById(R.id.btnLogout);

        // Pass userId to EditProfileActivity & DiaryActivity
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class).putExtra("userId", userId))
        );
        btnViewDiary.setOnClickListener(v ->
                startActivity(new Intent(this, DiaryActivity.class).putExtra("userId", userId))
        );
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        loadUserProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile(); // Reload data from the database
    }


    private void loadUserProfile() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // columns: 0=id, 1=full_name, 2=dob, 3=address, 4=phone, 5=username, 6=password, 7=profile_pic
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE id = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            tvFullName.setText("Name: " + cursor.getString(1));
            tvDob.setText("DOB: " + cursor.getString(2));
            tvAddress.setText("Address: " + cursor.getString(3));
            tvPhone.setText("Phone: " + cursor.getString(4));
        }
        cursor.close();
        db.close();
    }
}