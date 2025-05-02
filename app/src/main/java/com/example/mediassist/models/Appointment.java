package com.example.mediassist.models;

public class Appointment {
    private int id;
    private String date;
    private String time;
    private String type;
    private String doctor;
    private String notes;

    public Appointment() {
    }

    public Appointment(int id, String date, String time, String type, String doctor) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.type = type;
        this.doctor = doctor;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}