package com.example.kisiselsagliktakipsistem;

public class Prescription {
    private String name;
    private String dosage;
    private String startDate;
    private String endDate;
    private String reminderTimes;
    private String beforeAfterMeal;
    private String description;

    public Prescription(String name, String dosage, String startDate, String endDate, String beforeAfterMeal, String description, String reminderTimes) {
        this.name = name;
        this.dosage = dosage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.beforeAfterMeal = beforeAfterMeal;
        this.description = description;
        this.reminderTimes = reminderTimes;
    }

    public String getName() { return name; }
    public String getDosage() { return dosage; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getBeforeAfterMeal() { return beforeAfterMeal; }
    public String getDescription() { return description; }
    public String getReminderTimes() { return reminderTimes; }
} 