package com.example.kisiselsagliktakipsistem;

public class MedicalCondition {
    private String name;
    private String diagnosisDate;
    private String notes;

    public MedicalCondition(String name, String diagnosisDate, String notes) {
        this.name = name;
        this.diagnosisDate = diagnosisDate;
        this.notes = notes;
    }

    public String getName() { return name; }
    public String getDiagnosisDate() { return diagnosisDate; }
    public String getNotes() { return notes; }
} 