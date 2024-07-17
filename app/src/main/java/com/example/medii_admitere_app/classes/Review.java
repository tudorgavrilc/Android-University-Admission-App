package com.example.medii_admitere_app.classes;

import java.io.Serializable;
import java.util.List;

public class Review implements Serializable{
    private String userId;
    private String text;
    private List<Double> ratings;
    private double averageRating;
    private int likes;
    private int dislikes;
    private String documentPath;

    public Review() {

    }

    public Review(String userId, String text, List<Double> ratings, double averageRating, int likes, int dislikes) {
        this.userId = userId;
        this.text = text;
        this.ratings = ratings;
        this.averageRating = averageRating;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public Review(String userId, String text, List<Double> ratings, double averageRating, int likes, int dislikes, String documentPath) {
        this.userId = userId;
        this.text = text;
        this.ratings = ratings;
        this.averageRating = averageRating;
        this.likes = likes;
        this.dislikes = dislikes;
        this.documentPath = documentPath;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Double> getRatings() {
        return ratings;
    }

    public void setRatings(List<Double> ratings) {
        this.ratings = ratings;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }
}
