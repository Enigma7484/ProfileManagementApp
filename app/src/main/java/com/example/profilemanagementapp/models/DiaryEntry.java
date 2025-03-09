package com.example.profilemanagementapp.models;

public class DiaryEntry {
    private int id;
    private int userId;
    private String title;
    private String content;
    private String timestamp;

    // Constructor for creating a new entry
    public DiaryEntry(int userId, String title, String content, String timestamp) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Constructor for fetching an entry from the database
    public DiaryEntry(int id, int userId, String title, String content, String timestamp) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}