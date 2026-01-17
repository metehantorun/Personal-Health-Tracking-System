package com.example.kisiselsagliktakipsistem;

public class Goal {
    private String type;
    private double value;

    public Goal(String type, double value) {
        this.type = type;
        this.value = value;
    }

    public String getType() { return type; }
    public double getValue() { return value; }
}