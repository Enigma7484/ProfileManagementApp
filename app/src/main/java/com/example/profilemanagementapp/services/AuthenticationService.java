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
        // Hash the input password to match stored hashed password
        String hashedPassword = EncryptionHelper.hashPassword(password);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username = ? AND password = ?",
                new String[]{username, hashedPassword}
        );

        User user = null;
        if (cursor.moveToFirst()) {
            // columns: 0=id, 1=full_name, 2=dob, 3=address, 4=phone, 5=username, 6=password, 7=profile_pic
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getBlob(7)
            );
        }
        cursor.close();
        db.close();

        return user; // null if invalid credentials
    }
}