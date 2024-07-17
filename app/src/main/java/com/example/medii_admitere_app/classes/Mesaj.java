package com.example.medii_admitere_app.classes;

import com.google.firebase.Timestamp;

public class Mesaj {
    private String userId;
    private String message;
    private Timestamp timestamp;
    private String username;
    private String profileImageUrl;


    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public Mesaj() {

    }

    public Mesaj(String userId, String message, Timestamp timestamp, String username, String profileImageUrl) {
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }
    public Mesaj(String userId, String message, Timestamp timestamp) {
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

}
