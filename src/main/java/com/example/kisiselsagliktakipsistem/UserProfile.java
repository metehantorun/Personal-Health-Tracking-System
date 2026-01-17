package com.example.kisiselsagliktakipsistem;

public class UserProfile {
    private String firstName;
    private String lastName;
    private String gender;
    private String birthDate;
    private double height;
    private String bloodType;

    public UserProfile(String firstName, String lastName, String gender, String birthDate, double height) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.height = height;
        this.bloodType = "";
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getBirthDate() { return birthDate; }
    public double getHeight() { return height; }
    public String getBloodType() { return bloodType; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public void setHeight(double height) { this.height = height; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public int getAge() {
        java.time.LocalDate birth = java.time.LocalDate.parse(birthDate);
        java.time.LocalDate now = java.time.LocalDate.now();
        return java.time.Period.between(birth, now).getYears();
    }

    @Override
    public String toString() {
        return "UserProfile{" +
               "firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", gender='" + gender + '\'' +
               ", birthDate='" + birthDate + '\'' +
               ", height=" + height +
               ", bloodType='" + bloodType + '\'' +
               '}';
    }
}