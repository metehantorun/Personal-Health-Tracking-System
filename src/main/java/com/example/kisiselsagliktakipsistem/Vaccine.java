package com.example.kisiselsagliktakipsistem;

public class Vaccine {
    private String name;
    private String date;
    private String reminderDate;

    public Vaccine(String name, String date, String reminderDate) {
        this.name = name;
        this.date = date;
        this.reminderDate = reminderDate;
    }

    public String getName() { return name; }
    public String getDate() { return date; }
    public String getReminderDate() { return reminderDate; }
} 