package com.example.mediassist.models;

public class EmergencyContact {
    private int id;
    private String name;
    private String phone;
    private String imagePath;

    public EmergencyContact() {
    }

    public EmergencyContact(int id, String name, String phone, String imagePath) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}