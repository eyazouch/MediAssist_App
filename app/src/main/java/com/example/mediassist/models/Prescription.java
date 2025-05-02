package com.example.mediassist.models;

public class Prescription {
    private int id;
    private String date;
    private String imagePath;
    private String description;

    public Prescription() {
    }

    public Prescription(int id, String date, String imagePath, String description) {
        this.id = id;
        this.date = date;
        this.imagePath = imagePath;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}