package com.example.profilemanagementapp.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.profilemanagementapp.database.DatabaseHelper;
import com.example.profilemanagementapp.models.User;
import com.example.profilemanagementapp.utils.EncryptionHelper;

public class AuthenticationService {
    private DatabaseHelper dbHelper;

    public AuthenticationService(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public User authenticate(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String hashedPassword = EncryptionHelper.hashPassword(password);

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, hashedPassword});

        if (cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getBlob(7)
            );
            cursor.close();
            return user;
        }
        cursor.close();
        return null; // Invalid credentials
    }
}