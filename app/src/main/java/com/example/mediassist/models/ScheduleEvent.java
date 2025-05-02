package com.example.mediassist.models;

public class ScheduleEvent {
    private int id;
    private String title;
    private String time;
    private String description;
    private String type; // "medication" ou "appointment"

    public ScheduleEvent() {
    }

    public ScheduleEvent(int id, String title, String time, String description, String type) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.description = description;
        this.type = type;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}