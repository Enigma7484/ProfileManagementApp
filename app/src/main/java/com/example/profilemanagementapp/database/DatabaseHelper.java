package com.example.profilemanagementapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.profilemanagementapp.models.DiaryEntry;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "profileManagement.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, full_name TEXT, dob TEXT, address TEXT, phone TEXT, username TEXT UNIQUE, password TEXT, profile_pic BLOB)");
        db.execSQL("CREATE TABLE diary (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, title TEXT, content TEXT, timestamp TEXT, FOREIGN KEY(user_id) REFERENCES users(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
    public boolean updateDiaryEntry(DiaryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", entry.getTitle());
        values.put("content", entry.getContent());
        values.put("timestamp", entry.getTimestamp());

        int result = db.update("diary", values, "id = ?", new String[]{String.valueOf(entry.getId())});
        return result > 0;
    }

    // Delete a diary entry
    public boolean deleteDiaryEntry(int entryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("diary", "id = ?", new String[]{String.valueOf(entryId)});
        return result > 0;
    }
}