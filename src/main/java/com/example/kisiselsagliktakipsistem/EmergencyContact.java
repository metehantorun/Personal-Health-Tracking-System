package com.example.kisiselsagliktakipsistem;

public class EmergencyContact {
    private String firstName;
    private String lastName;
    private String phone;
    private String relationship;

    public EmergencyContact(String firstName, String lastName, String phone, String relationship) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.relationship = relationship;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getRelationship() { return relationship; }
} 