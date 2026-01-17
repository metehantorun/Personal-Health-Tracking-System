package com.example.kisiselsagliktakipsistem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProfileEditController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField heightField;
    @FXML private ComboBox<String> bloodTypeComboBox;

    private Stage dialogStage;
    private UserProfile userProfile;
    private boolean saved = false;

    @FXML
    private void initialize() {
        genderComboBox.getItems().addAll("Kadın", "Erkek", "Diğer");

        bloodTypeComboBox.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "0+", "0-");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
        firstNameField.setText(userProfile.getFirstName());
        lastNameField.setText(userProfile.getLastName());
        genderComboBox.setValue(userProfile.getGender());
        if (userProfile.getBirthDate() != null && !userProfile.getBirthDate().isEmpty()) {
            String birthDateString = userProfile.getBirthDate();
            LocalDate birthDate = null;
            DateTimeFormatter formatterDDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            DateTimeFormatter formatterYYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            try {
                birthDate = LocalDate.parse(birthDateString, formatterDDMMYYYY);
            } catch (Exception e1) {
                try {
                    birthDate = LocalDate.parse(birthDateString, formatterYYYYMMDD);
                } catch (Exception e2) {
                    System.err.println("Doğum tarihi parse edilirken hata: " + birthDateString + " - " + e2.getMessage());
                }
            }

            birthDatePicker.setValue(birthDate); 

        } else {
             birthDatePicker.setValue(null);
        }
        heightField.setText(String.valueOf(userProfile.getHeight()));
        bloodTypeComboBox.setValue(userProfile.getBloodType());
    }

    public boolean isSaved() {
        return saved;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    @FXML
    private void onSave() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String gender = genderComboBox.getValue();
        LocalDate birthDate = birthDatePicker.getValue();
        String heightStr = heightField.getText();
        String bloodType = bloodTypeComboBox.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || gender == null || birthDate == null || heightStr.isEmpty() || bloodType == null) {
            this.showAlert("Hata", "Lütfen tüm alanları doldurun.", Alert.AlertType.ERROR);
            return;
        }

        double height;
        try {
            height = Double.parseDouble(heightStr);
            if (height <= 0) {
                 this.showAlert("Hata", "Boy pozitif bir sayı olmalıdır.", Alert.AlertType.ERROR);
                 return;
            }
        } catch (NumberFormatException e) {
            this.showAlert("Hata", "Boy için geçerli bir sayı girin.", Alert.AlertType.ERROR);
            return;
        }

        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);
        userProfile.setGender(gender);
        userProfile.setBirthDate(birthDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        userProfile.setHeight(height);
        userProfile.setBloodType(bloodType);

        saved = true;
        dialogStage.close();
    }

    @FXML
    private void onCancel() {
        dialogStage.close(); 
    }

     private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 