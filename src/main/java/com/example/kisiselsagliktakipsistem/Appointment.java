package com.example.kisiselsagliktakipsistem;

public class Appointment {
    private String title;
    private String date;
    private String time;
    private String location;
    private String notes;
    private String doctor;

    public Appointment(String title, String date, String time, String location, String doctor, String notes) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.doctor = doctor;
        this.notes = notes;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public String getDoctor() { return doctor; }
    public String getNotes() { return notes; }
} 