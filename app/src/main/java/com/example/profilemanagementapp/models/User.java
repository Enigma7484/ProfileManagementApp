package com.example.profilemanagementapp.models;

public class User {
    private int id;
    private String fullName;
    private String dob;
    private String address;
    private String phone;
    private String username;
    private String password;
    private byte[] profilePic;

    // Constructor for new user creation
    public User(String fullName, String dob, String address, String phone, String username, String password) {
        this.fullName = fullName;
        this.dob = dob;
        this.address = address;
        this.phone = phone;
        this.username = username;
        this.password = password;
    }

    // Constructor for database retrieval
    public User(int id, String fullName, String dob, String address, String phone, String username, String password, byte[] profilePic) {
        this.id = id;
        this.fullName = fullName;
        this.dob = dob;
        this.address = address;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.profilePic = profilePic;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }
}