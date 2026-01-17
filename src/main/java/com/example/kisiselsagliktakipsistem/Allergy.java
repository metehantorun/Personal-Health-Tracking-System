package com.example.kisiselsagliktakipsistem;

public class Allergy {
    private String name;
    private String reaction;
    private String severity;
    private String date;

    public Allergy(String name, String reaction, String severity, String date) {
        this.name = name;
        this.reaction = reaction;
        this.severity = severity;
        this.date = date;
    }

    public String getName() { return name; }
    public String getReaction() { return reaction; }
    public String getSeverity() { return severity; }
    public String getDate() { return date; }
} 