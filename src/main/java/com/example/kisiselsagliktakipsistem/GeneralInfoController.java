package com.example.kisiselsagliktakipsistem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class GeneralInfoController {

    // FXML değişkenleri (GeneralInfoView.fxml'deki fx:id'lerle eşleşmeli)
    @FXML private ListView<String> allergyList;
    @FXML private ListView<String> prescriptionList;
    @FXML private ListView<String> vaccineList;
    @FXML private ListView<String> appointmentList;
    @FXML private ListView<String> exerciseList;
    @FXML private ListView<String> emergencyContactList;
    @FXML private ListView<String> medicalConditionList;
    @FXML private ListView<String> goalListView;
    @FXML private LineChart<Number, Number> goalChart;

    // Veri yapıları
    private MyLinkedList<Allergy> allergies = new MyLinkedList<>();
    private MyLinkedList<Prescription> prescriptions = new MyLinkedList<>();
    private MyLinkedList<Vaccine> vaccines = new MyLinkedList<>();
    private MyLinkedList<Appointment> appointments = new MyLinkedList<>();
    private MyLinkedList<Exercise> exercises = new MyLinkedList<>();
    private MyLinkedList<EmergencyContact> emergencyContacts = new MyLinkedList<>();
    private MyLinkedList<MedicalCondition> medicalConditions = new MyLinkedList<>();
    private MyLinkedList<Goal> goalList = new MyLinkedList<>();

    private double lastGoal = 70.0; // MainController'dan taşındı, ihtiyaca göre düzenlenebilir

    // Randevu Takibi
    private ObservableList<String> appointmentsObservable = FXCollections.observableArrayList();

    // Aşı Takibi
    private ObservableList<String> vaccinesObservable = FXCollections.observableArrayList();

    // Egzersiz ve Aktivite Takibi
    private ObservableList<String> exercisesObservable = FXCollections.observableArrayList();

    // Tıbbi Durumlar
    private ObservableList<String> medicalConditionsObservable = FXCollections.observableArrayList();

    // Acil Durum Bilgileri
    private ObservableList<String> emergencyContactsObservable = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Sayfa yüklendiğinde verileri yükle ve listeleri güncelle

        // Debug: Check if FXML elements are injected
        if (allergyList == null) System.err.println("allergyList is null!");
        if (prescriptionList == null) System.err.println("prescriptionList is null!");
        if (vaccineList == null) System.err.println("vaccineList is null!");
        if (exerciseList == null) System.err.println("exerciseList is null!");
        if (emergencyContactList == null) System.err.println("emergencyContactList is null!");
        if (medicalConditionList == null) System.err.println("medicalConditionList is null!");
        if (goalListView == null) System.err.println("goalListView is null!");
        if (goalChart == null) System.err.println("goalChart is null!");

        loadAllergiesFromFile();
        updateAllergyList();

        loadPrescriptionsFromFile();
        updatePrescriptionList();

        loadVaccinesFromFile();
        updateVaccineList();

        loadExercisesFromFile();
        updateExerciseList();

        loadEmergencyContactsFromFile();
        updateEmergencyContactList();

        loadMedicalConditionsFromFile();
        updateMedicalConditionList();

        loadGoalsFromFile();
        updateGoalListView();
        updateGoalChart();

        if (vaccineList != null) vaccineList.setItems(vaccinesObservable);
        if (exerciseList != null) exerciseList.setItems(exercisesObservable);
        if (emergencyContactList != null) emergencyContactList.setItems(emergencyContactsObservable);
        if (medicalConditionList != null) medicalConditionList.setItems(medicalConditionsObservable);
    }

    @FXML
    private void onAddAllergy() {
        Dialog<Allergy> dialog = new Dialog<>();
        dialog.setTitle("Yeni Alerji");
        dialog.setHeaderText("Yeni alerji bilgisi giriniz");

        Label l1 = new Label("Ad:");
        Label l2 = new Label("Reaksiyon:");
        Label l3 = new Label("Şiddet:");
        Label l4 = new Label("Tarih (dd.MM.yyyy):");

        TextField t1 = new TextField();
        TextField t2 = new TextField();
        TextField t3 = new TextField();
        TextField t4 = new TextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(l1, 0, 0); grid.add(t1, 1, 0);
        grid.add(l2, 0, 1); grid.add(t2, 1, 1);
        grid.add(l3, 0, 2); grid.add(t3, 1, 2);
        grid.add(l4, 0, 3); grid.add(t4, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String name = t1.getText().trim();
                String reaction = t2.getText().trim();
                String severity = t3.getText().trim();
                String date = t4.getText().trim();

                if (name.isEmpty() || reaction.isEmpty() || severity.isEmpty() || date.isEmpty()) {
                     showAlert("Hata", "Lütfen tüm alanları doldurun.", Alert.AlertType.ERROR);
                     return null;
                }


                return new Allergy(name, reaction, severity, date);
            }
            return null;
        });

        Optional<Allergy> result = dialog.showAndWait();
        result.ifPresent(allergy -> {
            allergies.add(allergy);
            updateAllergyList();
            saveAllergiesToFile();
        });
    }

    private void updateAllergyList() {
        if (allergyList == null) {
            System.err.println("allergyList is null in updateAllergyList!");
            return;
        }
        allergyList.getItems().clear();
        for (int i = 0; i < allergies.size(); i++) {
            Allergy allergy = allergies.get(i);
            allergyList.getItems().add(allergy.getDate() + ": " + allergy.getName() + " - " + allergy.getReaction() + " (" + allergy.getSeverity() + ")");
        }
        if (allergyList.getItems().isEmpty()) {
            allergyList.getItems().add("Alerji bilgisi yok.");
        }
    }

    private void saveAllergiesToFile() {
        try (FileWriter writer = new FileWriter("alerjiler.csv")) {
            for (int i = 0; i < allergies.size(); i++) {
                Allergy allergy = allergies.get(i);
                writer.write(allergy.getName() + "," + allergy.getReaction() + "," + allergy.getSeverity() + "," + allergy.getDate() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAllergiesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("alerjiler.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    allergies.add(new Allergy(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteAllergy() {
        if (allergyList == null) {
            System.err.println("allergyList is null in onDeleteAllergy!");
            showAlert("Hata", "Alerji listesi yüklenemedi.", Alert.AlertType.ERROR);
            return;
        }

        String selectedItemString = allergyList.getSelectionModel().getSelectedItem();

        if (selectedItemString != null && !selectedItemString.equals("Alerji bilgisi yok.")) {
            Allergy selectedAllergy = null;
            for (int i = 0; i < allergies.size(); i++) {
                Allergy a = allergies.get(i);
                if ((a.getDate() + ": " + a.getName() + " - " + a.getReaction() + " (" + a.getSeverity() + ")").equals(selectedItemString)) {
                    selectedAllergy = a;
                    break;
                }
            }

            if (selectedAllergy != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Silme Onayı");
                alert.setHeaderText(null);
                alert.setContentText("Seçili alerji bilgisini silmek istediğinizden emin misiniz?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean removed = allergies.remove(selectedAllergy);

                    if (removed) {
                        updateAllergyList();
                        saveAllergiesToFile();
                        showAlert("Başarılı", "Alerji bilgisi başarıyla silindi.", Alert.AlertType.INFORMATION);
                    } else {
                        System.err.println("Alerji linked listeden kaldırılamadı!");
                        showAlert("Hata", "Alerji silinirken bir hata oluştu.", Alert.AlertType.ERROR);
                    }
                }
            } else {
                System.err.println("Seçili alerji linked listede bulunamadı!");
                showAlert("Hata", "Silinecek alerji bulunamadı.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Uyarı", "Lütfen silmek için bir alerji seçin.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void onAddPrescription() {
        Dialog<Prescription> dialog = new Dialog<>();
        dialog.setTitle("Yeni Reçete");
        dialog.setHeaderText("Yeni reçete bilgisi giriniz");

        Label l1 = new Label("İsim:");
        Label l2 = new Label("Dozaj:");
        Label l3 = new Label("Başlangıç Tarihi (dd.MM.yyyy):");
        Label l4 = new Label("Bitiş Tarihi (dd.MM.yyyy):");
        Label l6 = new Label("Aç/Tok Karnına:");
        Label l7 = new Label("Açıklama:");
        Label l8 = new Label("Hatırlatma Saatleri (HH:mm, virgülle ayırın): "); // Yeni Label

        TextField t1 = new TextField();
        TextField t2 = new TextField();
        TextField t3 = new TextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        TextField t4 = new TextField(LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        ComboBox<String> t6 = new ComboBox<>();
        t6.getItems().addAll("Aç", "Tok", "Farketmez");
        t6.setValue("Farketmez");
        TextField t7 = new TextField();
        TextField t8 = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(l1, 0, 0); grid.add(t1, 1, 0);
        grid.add(l2, 0, 1); grid.add(t2, 1, 1);
        grid.add(l3, 0, 2); grid.add(t3, 1, 2);
        grid.add(l4, 0, 3); grid.add(t4, 1, 3);
        grid.add(l6, 0, 4); grid.add(t6, 1, 4);
        grid.add(l7, 0, 5); grid.add(t7, 1, 5);
        grid.add(l8, 0, 6); grid.add(t8, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String name = t1.getText().trim();
                String dosage = t2.getText().trim();
                String startDate = t3.getText().trim();
                String endDate = t4.getText().trim();
                String beforeAfterMeal = t6.getValue();
                String description = t7.getText().trim();
                String reminderTimes = t8.getText().trim();

                if (name.isEmpty() || dosage.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || beforeAfterMeal == null) {
                     showAlert("Hata", "Lütfen gerekli alanları doldurun (İsim, Dozaj, Başlangıç/Bitiş Tarihi, Aç/Tok Karnına).", Alert.AlertType.ERROR);
                     return null;
                }

                try {
                    LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                } catch (Exception e) {
                    showAlert("Hata", "Geçerli bir Başlangıç/Bitiş Tarihi (dd.MM.yyyy) formatı girin.", Alert.AlertType.ERROR);
                    return null;
                }

                return new Prescription(
                        name,
                        dosage,
                        startDate,
                        endDate,
                        beforeAfterMeal,
                        description,
                        reminderTimes
                );
            }
            return null;
        });

        Optional<Prescription> result = dialog.showAndWait();
        result.ifPresent(prescription -> {
            prescriptions.add(prescription);
            updatePrescriptionList();
            savePrescriptionsToFile();
        });
    }

    private void updatePrescriptionList() {
        prescriptionList.getItems().clear();
        for (int i = 0; i < prescriptions.size(); i++) {
            Prescription p = prescriptions.get(i);
            prescriptionList.getItems().add(p.getStartDate() + " - " + p.getEndDate() + ": " + p.getName() + " (" + p.getDosage() + ") - " + p.getBeforeAfterMeal() + " | Saatler: " + p.getReminderTimes());
        }
    }

    private void savePrescriptionsToFile() {
        try (FileWriter writer = new FileWriter("receteler.csv")) {
            for (int i = 0; i < prescriptions.size(); i++) {
                Prescription p = prescriptions.get(i);
                writer.write(p.getName() + "," + p.getDosage() + "," + p.getStartDate() + "," + p.getEndDate() + "," + p.getBeforeAfterMeal() + "," + p.getDescription() + "," + p.getReminderTimes() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPrescriptionsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("receteler.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) { // Ensure at least 6 parts for the core data
                    String name = parts[0];
                    String dosage = parts[1];
                    String startDate = parts[2];
                    String endDate = parts[3];
                    String beforeAfterMeal = parts[4];
                    String description = parts[5];
                    String reminderTimes = parts.length > 6 ? parts[6] : "";

                    prescriptions.add(new Prescription(name, dosage, startDate, endDate, beforeAfterMeal, description, reminderTimes));
                } else {
                    System.err.println("Skipping prescription data line due to incorrect format: " + line);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeletePrescription() {
        if (prescriptionList == null) {
            System.err.println("prescriptionList is null in onDeletePrescription!");
            showAlert("Hata", "Reçete listesi yüklenemedi.", Alert.AlertType.ERROR);
            return;
        }

        String selectedItemString = prescriptionList.getSelectionModel().getSelectedItem();

        if (selectedItemString != null) {
            Prescription selectedPrescription = null;
            for (int i = 0; i < prescriptions.size(); i++) {
                Prescription p = prescriptions.get(i);
                if ((p.getStartDate() + " - " + p.getEndDate() + ": " + p.getName() + " (" + p.getDosage() + ") - " + p.getBeforeAfterMeal() + " | Saatler: " + p.getReminderTimes()).equals(selectedItemString)) {
                    selectedPrescription = p;
                    break;
                }
            }

            if (selectedPrescription != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Silme Onayı");
                alert.setHeaderText(null);
                alert.setContentText("Seçili reçete bilgisini silmek istediğinizden emin misiniz?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean removed = prescriptions.remove(selectedPrescription);

                    if (removed) {
                        updatePrescriptionList();
                        savePrescriptionsToFile();
                        showAlert("Başarılı", "Reçete bilgisi başarıyla silindi.", Alert.AlertType.INFORMATION);
                    } else {
                        System.err.println("Reçete linked listeden kaldırılamadı!");
                        showAlert("Hata", "Reçete silinirken bir hata oluştu.", Alert.AlertType.ERROR);
                    }
                }
            } else {
                System.err.println("Seçili reçete linked listede bulunamadı!");
                showAlert("Hata", "Silinecek reçete bulunamadı.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Uyarı", "Lütfen silmek için bir reçete seçin.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void onAddVaccine() {
        Dialog<Vaccine> dialog = new Dialog<>();
        dialog.setTitle("Yeni Aşı");
        dialog.setHeaderText("Yeni aşı bilgisi giriniz");

        Label l1 = new Label("Adı:");
        Label l2 = new Label("Uygulama Tarihi (dd.MM.yyyy):");
        Label l3 = new Label("Hatırlatma Tarihi (dd.MM.yyyy, İsteğe Bağlı):");

        TextField t1 = new TextField();
        TextField t2 = new TextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        TextField t3 = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(l1, 0, 0); grid.add(t1, 1, 0);
        grid.add(l2, 0, 1); grid.add(t2, 1, 1);
        grid.add(l3, 0, 2); grid.add(t3, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String reminderDate = t3.getText().trim().isEmpty() ? null : t3.getText().trim();
                return new Vaccine(t1.getText().trim(), t2.getText().trim(), reminderDate);
            }
            return null;
        });

        Optional<Vaccine> result = dialog.showAndWait();
        result.ifPresent(vaccine -> {
            vaccines.add(vaccine);
            updateVaccineList();
            saveVaccinesToFile();
        });
    }

    private void updateVaccineList() {
        vaccineList.getItems().clear();
        for (int i = 0; i < vaccines.size(); i++) {
            Vaccine v = vaccines.get(i);
            String info = v.getDate() + ": " + v.getName();
            if (v.getReminderDate() != null && !v.getReminderDate().isEmpty()) {
                info += " (Hatırlatma: " + v.getReminderDate() + ")";
            }
            vaccineList.getItems().add(info);
        }
    }

    private void saveVaccinesToFile() {
        try (FileWriter writer = new FileWriter("asilar.csv")) {
            for (int i = 0; i < vaccines.size(); i++) {
                Vaccine vaccine = vaccines.get(i);
                writer.write(vaccine.getName() + "," + vaccine.getDate() + "," + (vaccine.getReminderDate() != null ? vaccine.getReminderDate() : "") + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadVaccinesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("asilar.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String reminderDate = parts.length > 2 && !parts[2].isEmpty() ? parts[2] : null;
                    vaccines.add(new Vaccine(parts[0], parts[1], reminderDate));
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteVaccine() {
        if (vaccineList == null) {
            System.err.println("vaccineList is null in onDeleteVaccine!");
            showAlert("Hata", "Aşı listesi yüklenemedi.", Alert.AlertType.ERROR);
            return;
        }

        String selectedItemString = vaccineList.getSelectionModel().getSelectedItem();

        if (selectedItemString != null) {
            Vaccine selectedVaccine = null;
            for (int i = 0; i < vaccines.size(); i++) {
                Vaccine v = vaccines.get(i);
                String info = v.getDate() + ": " + v.getName();
                if (v.getReminderDate() != null && !v.getReminderDate().isEmpty()) {
                    info += " (Hatırlatma: " + v.getReminderDate() + ")";
                }

                if (info.equals(selectedItemString)) {
                    selectedVaccine = v;
                    break;
                }
            }

            if (selectedVaccine != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Silme Onayı");
                alert.setHeaderText(null);
                alert.setContentText("Seçili aşı bilgisini silmek istediğinizden emin misiniz?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean removed = vaccines.remove(selectedVaccine);

                    if (removed) {
                        updateVaccineList();
                        saveVaccinesToFile();
                        showAlert("Başarılı", "Aşı bilgisi başarıyla silindi.", Alert.AlertType.INFORMATION);
                    } else {
                        System.err.println("Aşı linked listeden kaldırılamadı!");
                        showAlert("Hata", "Aşı silinirken bir hata oluştu.", Alert.AlertType.ERROR);
                    }
                }
            } else {
                System.err.println("Seçili aşı linked listede bulunamadı!");
                showAlert("Hata", "Silinecek aşı bulunamadı.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Uyarı", "Lütfen silmek için bir aşı seçin.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void onAddExercise() {
        Dialog<Exercise> dialog = new Dialog<>();
        dialog.setTitle("Yeni Egzersiz");
        dialog.setHeaderText("Yeni egzersiz bilgisi giriniz");

        Label l1 = new Label("Tarih (dd.MM.yyyy):");
        Label l2 = new Label("Tür:");
        Label l3 = new Label("Süre (dk):");
        Label l4 = new Label("Yakılan Kalori:");

        TextField t1 = new TextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        TextField t2 = new TextField();
        TextField t3 = new TextField();
        TextField t4 = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(l1, 0, 0); grid.add(t1, 1, 0);
        grid.add(l2, 0, 1); grid.add(t2, 1, 1);
        grid.add(l3, 0, 2); grid.add(t3, 1, 2);
        grid.add(l4, 0, 3); grid.add(t4, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    String date = t1.getText().trim();
                    String type = t2.getText().trim();
                    int duration = Integer.parseInt(t3.getText().trim());
                    int calories = Integer.parseInt(t4.getText().trim());
                    return new Exercise(date, type, duration, calories);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format for exercise duration or calories: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Exercise> result = dialog.showAndWait();
        result.ifPresent(exercise -> {
            exercises.add(exercise);
            updateExerciseList();
            saveExercisesToFile();
        });
    }

    private void updateExerciseList() {
        exerciseList.getItems().clear();
        for (int i = 0; i < exercises.size(); i++) {
            Exercise e = exercises.get(i);
            exerciseList.getItems().add(e.getDate() + ": " + e.getType() + " - " + e.getDuration() + " dk, " + e.getCalories() + " kcal");
        }
    }

    private void saveExercisesToFile() {
        try (FileWriter writer = new FileWriter("egzersizler.csv")) {
            for (int i = 0; i < exercises.size(); i++) {
                Exercise e = exercises.get(i);
                writer.write(e.getDate() + "," + e.getType() + "," + e.getDuration() + "," + e.getCalories() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadExercisesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("egzersizler.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    try {
                        String date = parts[0];
                        String type = parts[1];
                        int duration = Integer.parseInt(parts[2]);
                        int calories = Integer.parseInt(parts[3]);
                        exercises.add(new Exercise(date, type, duration, calories));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing exercise data: " + line + " - " + e.getMessage());
                    }
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteExercise() {
        if (exerciseList == null) {
            System.err.println("exerciseList is null in onDeleteExercise!");
            showAlert("Hata", "Egzersiz listesi yüklenemedi.", Alert.AlertType.ERROR);
            return;
        }

        String selectedItemString = exerciseList.getSelectionModel().getSelectedItem();

        if (selectedItemString != null) {
            Exercise selectedExercise = null;
            for (int i = 0; i < exercises.size(); i++) {
                Exercise e = exercises.get(i);
                if ((e.getDate() + ": " + e.getType() + " - " + e.getDuration() + " dk, " + e.getCalories() + " kcal").equals(selectedItemString)) {
                    selectedExercise = e;
                    break;
                }
            }

            if (selectedExercise != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Silme Onayı");
                alert.setHeaderText(null);
                alert.setContentText("Seçili egzersiz bilgisini silmek istediğinizden emin misiniz?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean removed = exercises.remove(selectedExercise);

                    if (removed) {
                        updateExerciseList();
                        saveExercisesToFile();
                        showAlert("Başarılı", "Egzersiz bilgisi başarıyla silindi.", Alert.AlertType.INFORMATION);
                    } else {
                        System.err.println("Egzersiz linked listeden kaldırılamadı!");
                        showAlert("Hata", "Egzersiz silinirken bir hata oluştu.", Alert.AlertType.ERROR);
                    }
                }
            } else {
                System.err.println("Seçili egzersiz linked listede bulunamadı!");
                showAlert("Hata", "Silinecek egzersiz bulunamadı.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Uyarı", "Lütfen silmek için bir egzersiz seçin.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void onAddEmergencyContact() {
        Dialog<EmergencyContact> dialog = new Dialog<>();
        dialog.setTitle("Yeni Acil Durum Kişisi");
        dialog.setHeaderText("Acil durum kişisi bilgisi giriniz");

        Label l1 = new Label("Ad:");
        Label l2 = new Label("Soyad:");
        Label l3 = new Label("Telefon:");
        Label l4 = new Label("Yakınlık Derecesi:");

        TextField t1 = new TextField();
        TextField t2 = new TextField();
        TextField t3 = new TextField();
        TextField t4 = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(l1, 0, 0); grid.add(t1, 1, 0);
        grid.add(l2, 0, 1); grid.add(t2, 1, 1);
        grid.add(l3, 0, 2); grid.add(t3, 1, 2);
        grid.add(l4, 0, 3); grid.add(t4, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new EmergencyContact(t1.getText().trim(), t2.getText().trim(), t3.getText().trim(), t4.getText().trim());
            }
            return null;
        });

        Optional<EmergencyContact> result = dialog.showAndWait();
        result.ifPresent(contact -> {
            emergencyContacts.add(contact);
            updateEmergencyContactList();
            saveEmergencyContactsToFile();
        });
    }

    private void updateEmergencyContactList() {
        emergencyContactList.getItems().clear();
        for (int i = 0; i < emergencyContacts.size(); i++) {
            EmergencyContact ec = emergencyContacts.get(i);
            emergencyContactList.getItems().add(ec.getFirstName() + " " + ec.getLastName() + " (" + ec.getRelationship() + ") - " + ec.getPhone());
        }
    }

    private void saveEmergencyContactsToFile() {
        try (FileWriter writer = new FileWriter("emergency_contacts.csv")) {
            for (int i = 0; i < emergencyContacts.size(); i++) {
                EmergencyContact ec = emergencyContacts.get(i);
                writer.write(ec.getFirstName() + "," + ec.getLastName() + "," + ec.getPhone() + "," + ec.getRelationship() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEmergencyContactsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("emergency_contacts.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    emergencyContacts.add(new EmergencyContact(parts[0], parts[1], parts[2], parts[3]));
                    emergencyContactsObservable.add(parts[0] + " " + parts[1] + " (" + parts[3] + ") - " + parts[2]);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteEmergencyContact() {
        if (emergencyContactList == null) {
            System.err.println("emergencyContactList is null in onDeleteEmergencyContact!");
            showAlert("Hata", "Acil durum kişileri listesi yüklenemedi.", Alert.AlertType.ERROR);
            return;
        }

        String selectedItemString = emergencyContactList.getSelectionModel().getSelectedItem();

        if (selectedItemString != null && !selectedItemString.equals("Acil durum kişisi bilgisi yok.")) {
            EmergencyContact selectedContact = null;
            for (int i = 0; i < emergencyContacts.size(); i++) {
                EmergencyContact ec = emergencyContacts.get(i);
                if ((ec.getFirstName() + " " + ec.getLastName() + " (" + ec.getRelationship() + ") - " + ec.getPhone()).equals(selectedItemString)) {
                    selectedContact = ec;
                    break;
                }
            }

            if (selectedContact != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Silme Onayı");
                alert.setHeaderText(null);
                alert.setContentText("Seçili acil durum kişisini silmek istediğinizden emin misiniz?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean removed = emergencyContacts.remove(selectedContact);

                    if (removed) {
                        updateEmergencyContactList();
                        saveEmergencyContactsToFile();
                        showAlert("Başarılı", "Acil durum kişisi başarıyla silindi.", Alert.AlertType.INFORMATION);
                    } else {
                        System.err.println("Acil durum kişisi linked listeden kaldırılamadı!");
                        showAlert("Hata", "Acil durum kişisi silinirken bir hata oluştu.", Alert.AlertType.ERROR);
                    }
                }
            } else {
                System.err.println("Seçili acil durum kişisi linked listede bulunamadı!");
                showAlert("Hata", "Silinecek acil durum kişisi bulunamadı.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Uyarı", "Lütfen silmek için bir acil durum kişisi seçin.", Alert.AlertType.WARNING);
        }
    }
    //endregion

    //region Tıbbi Durum Metotları
    @FXML
    private void onAddMedicalCondition() {
        Dialog<MedicalCondition> dialog = new Dialog<>();
        dialog.setTitle("Yeni Tıbbi Durum");
        dialog.setHeaderText("Tıbbi durum bilgisi giriniz");

        Label l1 = new Label("Adı:");
        Label l2 = new Label("Teşhis Tarihi (dd.MM.yyyy):");
        Label l3 = new Label("Notlar:");

        TextField t1 = new TextField();
        TextField t2 = new TextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        TextArea t3 = new TextArea();
        t3.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(l1, 0, 0); grid.add(t1, 1, 0);
        grid.add(l2, 0, 1); grid.add(t2, 1, 1);
        grid.add(l3, 0, 2); grid.add(t3, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new MedicalCondition(t1.getText().trim(), t2.getText().trim(), t3.getText().trim());
            }
            return null;
        });

        Optional<MedicalCondition> result = dialog.showAndWait();
        result.ifPresent(condition -> {
            medicalConditions.add(condition);
            updateMedicalConditionList();
            saveMedicalConditionsToFile();
        });
    }

    private void updateMedicalConditionList() {
        medicalConditionList.getItems().clear();
        for (int i = 0; i < medicalConditions.size(); i++) {
            MedicalCondition mc = medicalConditions.get(i);
            medicalConditionList.getItems().add(mc.getDiagnosisDate() + ": " + mc.getName() + (mc.getNotes() != null && !mc.getNotes().isEmpty() ? " (" + mc.getNotes() + ")" : ""));
        }
    }

    private void saveMedicalConditionsToFile() {
        try (FileWriter writer = new FileWriter("medical_conditions.csv")) {
            for (int i = 0; i < medicalConditions.size(); i++) {
                MedicalCondition mc = medicalConditions.get(i);
                writer.write(mc.getName() + "," + mc.getDiagnosisDate() + "," + mc.getNotes() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMedicalConditionsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("medical_conditions.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) { // Ensure correct number of parts
                    medicalConditions.add(new MedicalCondition(parts[0], parts[1], parts[2]));
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteMedicalCondition() {
        if (medicalConditionList == null) {
            System.err.println("medicalConditionList is null in onDeleteMedicalCondition!");
            showAlert("Hata", "Tıbbi durum listesi yüklenemedi.", Alert.AlertType.ERROR);
            return;
        }

        String selectedItemString = medicalConditionList.getSelectionModel().getSelectedItem();

        if (selectedItemString != null) {
            MedicalCondition selectedCondition = null;
            for (int i = 0; i < medicalConditions.size(); i++) {
                MedicalCondition mc = medicalConditions.get(i);
                if ((mc.getDiagnosisDate() + ": " + mc.getName() + (mc.getNotes() != null && !mc.getNotes().isEmpty() ? " (" + mc.getNotes() + ")" : "")).equals(selectedItemString)) {
                    selectedCondition = mc;
                    break;
                }
            }

            if (selectedCondition != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Silme Onayı");
                alert.setHeaderText(null);
                alert.setContentText("Seçili tıbbi durum bilgisini silmek istediğinizden emin misiniz?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean removed = medicalConditions.remove(selectedCondition);

                    if (removed) {
                        updateMedicalConditionList();
                        saveMedicalConditionsToFile();
                        showAlert("Başarılı", "Tıbbi durum bilgisi başarıyla silindi.", Alert.AlertType.INFORMATION);
                    } else {
                        System.err.println("Tıbbi durum linked listeden kaldırılamadı!");
                        showAlert("Hata", "Tıbbi durum silinirken bir hata oluştu.", Alert.AlertType.ERROR);
                    }
                }
            } else {
                System.err.println("Seçili tıbbi durum linked listede bulunamadı!");
                showAlert("Hata", "Silinecek tıbbi durum bulunamadı.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Uyarı", "Lütfen silmek için bir tıbbi durum seçin.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void onAddGoal() {
        Dialog<Goal> dialog = new Dialog<>();
        dialog.setTitle("Yeni Hedef Ekle");
        dialog.setHeaderText("Hedef Türü ve Değeri Giriniz");

        Label l1 = new Label("Tür:");
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Kilo", "Adım Sayısı", "Su Tüketimi (litre)", "Uyku Süresi (saat)");
        typeBox.setValue("Kilo");
        Label l2 = new Label("Değer:");
        TextField t2 = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(l1, 0, 0); grid.add(typeBox, 1, 0);
        grid.add(l2, 0, 1); grid.add(t2, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String type = typeBox.getValue();
                String valueStr = t2.getText();
                if (type != null && valueStr != null && !valueStr.trim().isEmpty()) {
                    try {
                        double value = Double.parseDouble(valueStr.trim());
                        return new Goal(type, value);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format for goal value");
                        return null;
                    }
                }
            }
            return null;
        });

        Optional<Goal> result = dialog.showAndWait();
        result.ifPresent(g -> {
            Goal existingGoal = null;
            for (int i = 0; i < goalList.size(); i++) {
                Goal goal = goalList.get(i);
                if (goal.getType().equals(g.getType())) {
                    existingGoal = goal;
                    break;
                }
            }
            if (existingGoal != null) {
                goalList.remove(existingGoal);
            }
            goalList.add(g);
            updateGoalListView();
            saveGoalsToFile();
        });
    }

    private void updateGoalListView() {
        goalListView.getItems().clear();
        for (int i = 0; i < goalList.size(); i++) {
            Goal g = goalList.get(i);
            String valueStr = (g.getValue() == (int)g.getValue()) ? String.valueOf((int)g.getValue()) : String.valueOf(g.getValue());
            goalListView.getItems().add(g.getType() + " Hedefi: " + valueStr);
        }
    }

    private void updateGoalChart() {
        goalChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(1, 75));
        series.getData().add(new XYChart.Data<>(2, 74));
        series.getData().add(new XYChart.Data<>(3, 73));
        series.getData().add(new XYChart.Data<>(4, 72));
        series.getData().add(new XYChart.Data<>(5, 71));
        series.getData().add(new XYChart.Data<>(6, 70));

        goalChart.getData().add(series);
    }

    private void saveGoalsToFile() {
        try (FileWriter writer = new FileWriter("hedefler.csv")) {
            for (int i = 0; i < goalList.size(); i++) {
                Goal g = goalList.get(i);
                writer.write(g.getType() + "," + g.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGoalsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("hedefler.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    try {
                        goalList.add(new Goal(parts[0], Double.parseDouble(parts[1])));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing goal value: " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteGoal() {
        if (goalListView == null) {
            System.err.println("goalListView is null in onDeleteGoal!");
            showAlert("Hata", "Hedef listesi yüklenemedi.", Alert.AlertType.ERROR);
            return;
        }

        String selectedItemString = goalListView.getSelectionModel().getSelectedItem();

        if (selectedItemString != null && !selectedItemString.equals("Hedef bilgisi yok.")) {
            Goal selectedGoal = null;
            for (int i = 0; i < goalList.size(); i++) {
                Goal g = goalList.get(i);
                String valueStr = (g.getValue() == (int)g.getValue()) ? String.valueOf((int)g.getValue()) : String.valueOf(g.getValue());
                String listItemString = g.getType() + " Hedefi: " + valueStr;

                if (listItemString.equals(selectedItemString)) {
                    selectedGoal = g;
                    break;
                }
            }

            if (selectedGoal != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Silme Onayı");
                alert.setHeaderText(null);
                alert.setContentText("Seçili hedef bilgisini silmek istediğinizden emin misiniz?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean removed = goalList.remove(selectedGoal);

                    if (removed) {
                        updateGoalListView();
                        saveGoalsToFile();
                        showAlert("Başarılı", "Hedef bilgisi başarıyla silindi.", Alert.AlertType.INFORMATION);
                    } else {
                        System.err.println("Hedef linked listeden kaldırılamadı!");
                        showAlert("Hata", "Hedef silinirken bir hata oluştu.", Alert.AlertType.ERROR);
                    }
                }
            } else {
                System.err.println("Seçili hedef linked listede bulunamadı!");
                showAlert("Hata", "Silinecek hedef bulunamadı.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Uyarı", "Lütfen silmek için bir hedef seçin.", Alert.AlertType.WARNING);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

} 