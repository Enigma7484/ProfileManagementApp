package com.example.profilemanagementapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.profilemanagementapp.models.DiaryEntry;
import com.example.profilemanagementapp.models.User;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "profileManagement.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Enable foreign key constraints
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT, " +
                "dob TEXT, " +
                "address TEXT, " +
                "phone TEXT, " +
                "username TEXT UNIQUE, " +
                "password TEXT, " +
                "profile_pic BLOB" +
                ")");
        // Create diary table with ON DELETE CASCADE
        db.execSQL("CREATE TABLE diary (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "title TEXT, " +
                "content TEXT, " +
                "timestamp TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop and recreate tables
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS diary");
        onCreate(db);
    }

    // Retrieve all diary entries for a user
    public List<DiaryEntry> getDiaryEntries(int userId) {
        List<DiaryEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM diary WHERE user_id = ?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                DiaryEntry entry = new DiaryEntry(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                entries.add(entry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entries;
    }

    // Add a new diary entry
    public boolean addDiaryEntry(DiaryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", entry.getUserId());
        values.put("title", entry.getTitle());
        values.put("content", entry.getContent());
        values.put("timestamp", entry.getTimestamp());

        long result = db.insert("diary", null, values);
        return result != -1;
    }

    // Retrieve a single diary entry by ID
    public DiaryEntry getDiaryEntry(int entryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM diary WHERE id = ?", new String[]{String.valueOf(entryId)});

        if (cursor.moveToFirst()) {
            DiaryEntry entry = new DiaryEntry(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
            return entry;
        }
        cursor.close();
        return null;
    }

    // Update an existing diary entry
    public void updateDiaryEntry(DiaryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", entry.getTitle());
        values.put("content", entry.getContent());
        values.put("timestamp", entry.getTimestamp());

        db.update("diary", values, "id = ?", new String[]{String.valueOf(entry.getId())});
    }

    // Delete a diary entry
    public boolean deleteDiaryEntry(int entryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("diary", "id = ?", new String[]{String.valueOf(entryId)});
        return result > 0;
    }

    // Clear the database (for testing purposes)
    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM users");
        db.execSQL("DELETE FROM diary");
        db.close();
    }

    // Register a new user
    public boolean registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", user.getFullName());
        values.put("dob", user.getDob());
        values.put("address", user.getAddress());
        values.put("phone", user.getPhone());
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        long result = db.insert("users", null, values);
        db.close();
        return result != -1;
    }

    // Validate user login
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?",
                new String[]{username, password});
        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return userExists;
    }
}