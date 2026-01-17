package com.example.kisiselsagliktakipsistem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import java.io.IOException;
import javafx.event.ActionEvent;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.awt.Desktop;
import java.net.URI;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.time.LocalDateTime;
import javafx.scene.control.TextInputDialog; // TextInputDialog için import

public class MainController {

    @FXML private TableView<HealthData> healthDataTable;
    @FXML private TableColumn<HealthData, String> colDate;
    @FXML private TableColumn<HealthData, String> colTime;
    @FXML private TableColumn<HealthData, Double> colWeight;
    @FXML private TableColumn<HealthData, Integer> colPulse, colBpHigh, colBpLow, colSugar;
    @FXML private TableColumn<HealthData, String> colStatus;

    @FXML private Label lastWeight, lastPulse, lastSugar, lastBP;
    @FXML private Label warningLabel, bmiLabel;

    @FXML private LineChart<Number, Number> goalChart;
    @FXML private ListView<String> goalListView;
    @FXML private ImageView appLogoImageView;

    // Yeni eklenen profil bilgisi etiketleri
    @FXML private Label profileNameLabel;
    @FXML private Label profileGenderLabel;
    @FXML private Label profileBirthDateLabel;
    @FXML private Label profileHeightLabel;
    @FXML private Label profileBloodTypeLabel;

    @FXML private ListView<String> emergencyContactListView;
    @FXML private Button editProfileButton; // Profil Düzenleme Butonu


    @FXML private Label detailDateLabel;
    @FXML private Label detailTimeLabel;
    @FXML private Label detailWeightLabel;
    @FXML private Label detailPulseLabel;
    @FXML private Label detailBpLabel;
    @FXML private Label detailSugarLabel;
    @FXML private Label detailStatusLabel;

    // Yeni eklenen Yaklaşan Randevular ListView'ı
    @FXML private ListView<String> upcomingAppointmentsListView;

    // Sağlık verileri için listeler
    private MyLinkedList<HealthData> healthDataList = new MyLinkedList<>();
    // Geri alma ve yineleme için stack'ler
    private MyStack<HealthData> undoStack = new MyStack<>();
    private MyStack<HealthData> redoStack = new MyStack<>();

    private UserProfile userProfile = new UserProfile("", "", "", "", 0);

    // Çeşitli sağlık verileri için veri yapıları (MainController'a taşındı)
    private MyLinkedList<Allergy> allergies = new MyLinkedList<>(); // MainController'da tutulmalı mı yoksa GeneralInfoController'da mı? GeneralInfoController'da kullanılıyor. Buradan kaldırılabilir.
    private MyLinkedList<Prescription> prescriptions = new MyLinkedList<>(); // GeneralInfoController'da kullanılıyor. Buradan kaldırılabilir.
    private MyLinkedList<Vaccine> vaccines = new MyLinkedList<>(); // GeneralInfoController'da kullanılıyor. Buradan kaldırılabilir.
    private MyLinkedList<Appointment> appointments = new MyLinkedList<>(); // Hem MainController'da (upcoming) hem GeneralInfoController'da kullanılıyor. Ortak bir yerde tutulmalı veya GeneralInfoController'dan çekilmeli. Şimdilik burada kalsın.
    private MyLinkedList<Exercise> exercises = new MyLinkedList<>(); // GeneralInfoController'da kullanılıyor. Buradan kaldırılabilir.
    private MyLinkedList<EmergencyContact> emergencyContacts = new MyLinkedList<>(); // Hem MainController'da hem GeneralInfoController'da kullanılıyor. Ortak bir yerde tutulmalı veya GeneralInfoController'dan çekilmeli. Şimdilik burada kalsın.
    private MyLinkedList<MedicalCondition> medicalConditions = new MyLinkedList<>(); // GeneralInfoController'da kullanılıyor. Buradan kaldırılabilir.
    private MyLinkedList<Goal> goalList = new MyLinkedList<>(); // Hem MainController'da (chart, list) hem GeneralInfoController'da kullanılıyor. Ortak bir yerde tutulmalı veya GeneralInfoController'dan çekilmeli. Şimdilik burada kalsın.


    private double lastGoal = 70.0;

    private MyQueue<Appointment> upcomingAppointmentsQueue = new MyQueue<>();

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        colPulse.setCellValueFactory(new PropertyValueFactory<>("pulse"));
        colBpHigh.setCellValueFactory(new PropertyValueFactory<>("bpHigh"));
        colBpLow.setCellValueFactory(new PropertyValueFactory<>("bpLow"));
        colSugar.setCellValueFactory(new PropertyValueFactory<>("bloodSugar"));

        loadHealthDataFromFile();

        healthDataTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateDetailLabels(newValue);
        });

        sortHealthDataTable();
        updateLastValues();
        loadUserProfileFromFile();
        updateProfileLabels();
        updateBMI();

        try {
            Image appIcon = new Image(getClass().getResourceAsStream("/com/example/kisiselsagliktakipsistem/images/arkaplan.png"));
            appLogoImageView.setImage(appIcon);
        } catch (Exception e) {
            System.err.println("Logo yüklenirken hata oluştu: " + e.getMessage());
        }


        loadEmergencyContactsFromFile();
        updateEmergencyContactList();

        loadGoalsFromFile();
        updateGoalListView();
        updateGoalChart();

        loadAppointmentsFromFile();
        updateUpcomingAppointmentsList();

        healthDataTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateDetailLabels(newValue);
        });

        sortHealthDataTable();
    }

    private void sortHealthDataTable() {
        healthDataTable.getSortOrder().clear();
        if (colDate != null && colTime != null) {
             healthDataTable.getSortOrder().add(colDate);
             healthDataTable.getSortOrder().add(colTime);
             colDate.setSortType(TableColumn.SortType.DESCENDING);
             colTime.setSortType(TableColumn.SortType.DESCENDING);
             healthDataTable.sort();
        } else {
            System.err.println("Tablo sütunları initialize edilmedi, sıralama yapılamıyor.");
        }
    }

    private void updateLastValues() {
        if (lastWeight == null || lastPulse == null || lastSugar == null || lastBP == null) {
            System.err.println("FXML yüklenirken son değer etiketleri bulunamadı.");
            return;
        }

        ObservableList<HealthData> items = healthDataTable.getItems();
        if (items != null && !items.isEmpty()) {
            HealthData latestData = items.get(0);
            lastWeight.setText("Son Kilo: " + latestData.getWeight() + " kg");
            lastPulse.setText("Son Nabız: " + latestData.getPulse());
            lastSugar.setText("Son Şeker: " + (latestData.getBloodSugar() == 0 ? "-" : latestData.getBloodSugar()));
            lastBP.setText("Son BP: " + latestData.getBpHigh() + "/" + latestData.getBpLow());
        } else {
            lastWeight.setText("Son Kilo: -");
            lastPulse.setText("Son Nabız: -");
            lastSugar.setText("Son Şeker: -");
            lastBP.setText("Son BP: -");
        }
    }

    private void updateGoalChart() {
        if (goalChart == null) {
            System.err.println("FXML yüklenirken kilo grafiği bulunamadı.");
            return;
        }

        goalChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Kilo Takibi");

        ObservableList<HealthData> sortedData = FXCollections.observableArrayList(healthDataTable.getItems());
        sortedData.sort(Comparator.comparing(hd -> LocalDate.parse(hd.getDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))));

        for (int i = 0; i < sortedData.size(); i++) {
            series.getData().add(new XYChart.Data<>(i, sortedData.get(i).getWeight()));
        }

        goalChart.getData().add(series);

        Optional<Goal> kiloGoal = Optional.empty();
        for (int i = 0; i < goalList.size(); i++) {
            Goal goal = goalList.get(i);
            if ("Kilo".equals(goal.getType())) {
                kiloGoal = Optional.of(goal);
                break;
            }
        }

        if (kiloGoal.isPresent()) {
            XYChart.Series<Number, Number> goalSeries = new XYChart.Series<>();
            goalSeries.setName("Hedef Kilo");
            double targetWeight = kiloGoal.get().getValue();
            for (int i = 0; i < sortedData.size(); i++) {
                goalSeries.getData().add(new XYChart.Data<>(i, targetWeight));
            }
            goalChart.getData().add(goalSeries);
        }


        goalChart.getXAxis().setLabel("Ölçüm Sırası");
        goalChart.getYAxis().setLabel("Kilo (kg)");
    }


    private void updateBMI() {
        if (bmiLabel == null || warningLabel == null) {
            System.err.println("FXML yüklenirken BMI/Durum etiketleri bulunamadı.");
            return;
        }

        ObservableList<HealthData> items = healthDataTable.getItems();

        if (items != null && !items.isEmpty() && userProfile != null && userProfile.getHeight() > 0) {
            HealthData latestData = items.get(0);
            double weight = latestData.getWeight();
            double heightInMeters = userProfile.getHeight() / 100.0;
            if (heightInMeters > 0) {
                double bmi = weight / (heightInMeters * heightInMeters);
                bmiLabel.setText(String.format("BMI: %.2f", bmi));

                if (bmi < 18.5) {
                    warningLabel.setText("Durum: Zayıf");
                    warningLabel.setStyle("-fx-text-fill: orange;");
                } else if (bmi < 25) {
                    warningLabel.setText("Durum: Normal");
                    warningLabel.setStyle("-fx-text-fill: green;");
                } else if (bmi < 30) {
                    warningLabel.setText("Durum: Kilolu");
                    warningLabel.setStyle("-fx-text-fill: orange;");
                } else {
                    warningLabel.setText("Durum: Obez");
                    warningLabel.setStyle("-fx-text-fill: red;");
                }
            } else {
                bmiLabel.setText("BMI: -");
                warningLabel.setText("Durum: Boy bilgisi gerekli"); // Boy 0 veya negatif
                warningLabel.setStyle("-fx-text-fill: black;");
            }
        } else {
            bmiLabel.setText("BMI: -");
            if (userProfile == null || userProfile.getHeight() <= 0) {
                 warningLabel.setText("Durum: Profil veya Boy bilgisi gerekli"); // Kullanıcı yok veya boy 0
            } else {
                 warningLabel.setText("Durum: Veri Gerekli"); // Sağlık verisi yok
            }
            warningLabel.setStyle("-fx-text-fill: black;");
        }
    }

    private void saveHealthDataToFile() {
        try (FileWriter writer = new FileWriter("veriler.csv")) {
            // MyLinkedList iterable olmadığı için manuel döngü
            for (int i = 0; i < healthDataList.size(); i++) {
                HealthData data = healthDataList.get(i);
                writer.write(data.getDate() + "," + data.getTime() + "," + data.getWeight() + "," + data.getPulse() + "," + data.getBpHigh() + "," + data.getBpLow() + "," + data.getBloodSugar() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHealthDataFromFile() {
        healthDataList.clear();
        healthDataTable.getItems().clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("veriler.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    try {
                        String date = parts[0].trim();
                        String time = parts[1].trim();
                        double weight = Double.parseDouble(parts[2].trim());
                        int pulse = Integer.parseInt(parts[3].trim());
                        int bpHigh = Integer.parseInt(parts[4].trim());
                        int bpLow = Integer.parseInt(parts[5].trim());
                        int bloodSugar = parts.length > 6 && !parts[6].trim().isEmpty() ? Integer.parseInt(parts[6].trim()) : 0; // Eğer 7. parça varsa ve boş değilse oku, yoksa veya boşsa 0 yap

                        HealthData data = new HealthData(date, time, weight, pulse, bpHigh, bpLow, bloodSugar);
                        healthDataList.add(data);
                        healthDataTable.getItems().add(data);
                    } catch (NumberFormatException e) {
                        System.err.println("CSV dosyasından sayısal değer okunurken hata: " + line + " - " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("CSV satırı işlenirken hata: " + line + " - " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Hatalı formatta sağlık verisi CSV satırı atlandı (Beklenen en az 6 parça): " + line);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("veriler.csv dosyası bulunamadı, yeni dosya oluşturulacak.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Sağlık verileri yüklenirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }

    private boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Onay");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void loadEmergencyContactsFromFile() {
        emergencyContacts.clear();
        if (emergencyContactListView != null) emergencyContactListView.getItems().clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("emergency_contacts.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        String firstName = parts[0].trim();
                        String lastName = parts[1].trim();
                        String phone = parts[2].trim();
                        String relationship = parts[3].trim();

                        EmergencyContact contact = new EmergencyContact(firstName, lastName, phone, relationship);
                        emergencyContacts.add(contact);
                    } catch (Exception e) {
                        System.err.println("Acil durum kişisi CSV satırı işlenirken hata: " + line + " - " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                     System.err.println("Hatalı formatta acil durum kişisi CSV satırı atlandı (Beklenen en az 4 parça: Ad,Soyad,Telefon,İlişki): " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("emergency_contacts.csv dosyası bulunamadı.");
        } catch (IOException e) {
            e.printStackTrace();
             if (emergencyContactListView != null) emergencyContactListView.getItems().add("Acil durum kişileri yüklenirken hata oluştu.");
        }
        updateEmergencyContactList();
    }

    private void updateEmergencyContactList() {
        if (emergencyContactListView == null) {
            System.err.println("emergencyContactListView is null in updateEmergencyContactList!");
            return;
        }

        emergencyContactListView.getItems().clear();
        if (emergencyContacts != null && emergencyContacts.size() > 0) {
             for (int i = 0; i < emergencyContacts.size(); i++) {
                EmergencyContact ec = emergencyContacts.get(i);
                String displayString = ec.getFirstName() + " " + ec.getLastName() + " " + ec.getPhone() + " (" + ec.getRelationship() + ")";
                emergencyContactListView.getItems().add(displayString);
            }
        } else {
            emergencyContactListView.getItems().add("Acil durum kişisi bilgisi yok.");
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
             showAlert("Hata", "Acil durum kişileri kaydedilirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }

    private void updateGoalSummary() {
        updateGoalListView();
    }

    private void updateGoalListView() {
        if (goalListView == null) {
            System.err.println("FXML yüklenirken hedef listesi bulunamadı.");
            return;
        }

        goalListView.getItems().clear();
        if (goalList != null) {
             for (int i = 0; i < goalList.size(); i++) {
                Goal g = goalList.get(i);
                String valueStr = (g.getValue() == (int)g.getValue()) ? String.valueOf((int)g.getValue()) : String.valueOf(g.getValue());
                goalListView.getItems().add(g.getType() + " Hedefi: " + valueStr);
            }
        }


        if (goalListView.getItems().isEmpty()) {
            goalListView.getItems().add("Hedef bilgisi yok.");
        }
    }

    private void loadGoalsFromFile() {
        goalList.clear();
        if (goalListView != null) goalListView.getItems().clear(); // Clear before loading
        try (BufferedReader reader = new BufferedReader(new FileReader("hedefler.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                 String[] parts = line.split(",");
                 if(parts.length >= 2) {
                    try {
                        String type = parts[0].trim();
                        double value = Double.parseDouble(parts[1].trim());
                        Goal goal = new Goal(type, value);
                        
                         if ("Kilo".equals(goal.getType())) {
                            Goal existingGoal = null;
                            for (int i = 0; i < goalList.size(); i++) {
                                Goal g = goalList.get(i);
                                if ("Kilo".equals(g.getType())) {
                                    existingGoal = g;
                                    break;
                                }
                            }
                            if (existingGoal != null) {
                                goalList.remove(existingGoal);
                            }
                        }
                        goalList.add(goal);
                        
                         if (goalListView != null) {
                            String valueStr = (value == (int)value) ? String.valueOf((int)value) : String.valueOf(value);
                            goalListView.getItems().add(type + " Hedefi: " + valueStr);
                         }
                    } catch (NumberFormatException e) {
                         System.err.println("Hedef değeri CSV'den okunurken hata: " + line);
                    } catch (Exception e) {
                         System.err.println("Hedef CSV satırı işlenirken hata: " + line + " - " + e.getMessage());
                    }
                 } else {
                     System.err.println("Hatalı formatta hedef CSV satırı atlandı (Beklenen en az 2 parça): " + line);
                 }
            }
        } catch (FileNotFoundException e) {
             System.out.println("hedefler.csv dosyası bulunamadı.");
             if (goalListView != null) goalListView.getItems().add("Hedef bilgisi yok.");
        } catch (IOException e) {
            e.printStackTrace();
             if (goalListView != null) goalListView.getItems().add("Hedefler yüklenirken hata oluştu.");
        }
    }

    private void loadAppointmentsFromFile() {
        appointments.clear();
        if (upcomingAppointmentsListView != null) upcomingAppointmentsListView.getItems().clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("randevular.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                 if (parts.length >= 5) {
                    String title = parts[0].trim();
                    String date = parts[1].trim();
                    String time = parts[2].trim();
                    String location = parts[3].trim();
                    String notes = parts[4].trim();
                    String doctor = parts.length > 5 ? parts[5].trim() : "";

                    Appointment appointment = new Appointment(title, date, time, location, doctor, notes);
                    appointments.add(appointment);
                 } else {
                     System.err.println("Hatalı formatta randevu CSV satırı atlandı (Beklenen en az 5 parça): " + line);
                 }
            }
        } catch (FileNotFoundException e) {
             System.out.println("randevular.csv dosyası bulunamadı.");
             if (upcomingAppointmentsListView != null) upcomingAppointmentsListView.getItems().add("Randevu bilgisi yok."); // Dosya yoksa veya boşsa bu mesajı göster
        } catch (IOException e) {
            e.printStackTrace();
            if (upcomingAppointmentsListView != null) upcomingAppointmentsListView.getItems().add("Randevular yüklenirken hata oluştu.");
        }
        updateUpcomingAppointmentsList(); // Dosya yüklendikten sonra yaklaşan randevuları ana ekranda göster
    }

    @FXML
    private void onAddAppointment() {
        Dialog<Appointment> dialog = new Dialog<>();
        dialog.setTitle("Yeni Randevu");
        dialog.setHeaderText("Yeni randevu bilgisi giriniz");

        Label l1 = new Label("Başlık:");
        Label l2 = new Label("Tarih (dd.MM.yyyy):");
        Label l3 = new Label("Saat (HH:mm):");
        Label l4 = new Label("Yer:");
        Label l5 = new Label("Doktor:");
        Label l6 = new Label("Notlar:");

        TextField t1 = new TextField();
        TextField t2 = new TextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        TextField t3 = new TextField();
        TextField t4 = new TextField();
        TextField t5 = new TextField();
        TextArea t6 = new TextArea();
        t6.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(l1, 0, 0); grid.add(t1, 1, 0);
        grid.add(l2, 0, 1); grid.add(t2, 1, 1);
        grid.add(l3, 0, 2); grid.add(t3, 1, 2);
        grid.add(l4, 0, 3); grid.add(t4, 1, 3);
        grid.add(l5, 0, 4); grid.add(t5, 1, 4);
        grid.add(l6, 0, 5); grid.add(t6, 1, 5);


        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String title = t1.getText().trim();
                String date = t2.getText().trim();
                String time = t3.getText().trim();
                String location = t4.getText().trim();
                String doctor = t5.getText().trim();
                String notes = t6.getText().trim();

                 if (title.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty()) {
                     showAlert("Hata", "Lütfen gerekli randevu alanlarını doldurun (Başlık, Tarih, Saat, Yer).", Alert.AlertType.ERROR);
                     return null;
                 }

                 // Tarih ve saat formatı validasyonu
                 try {
                     LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                     LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
                 } catch (Exception e) {
                     showAlert("Hata", "Geçerli bir Tarih (dd.MM.yyyy) veya Saat (HH:mm) formatı girin.", Alert.AlertType.ERROR);
                     return null;
                 }

                return new Appointment(title, date, time, location, doctor, notes); // Constructor 6 argüman alıyor
            }
            return null;
        });

        Optional<Appointment> result = dialog.showAndWait();
        result.ifPresent(appointment -> {
            appointments.add(appointment);
            saveAppointmentsToFile();
            updateUpcomingAppointmentsList();
        });
    }

    private void saveAppointmentsToFile() {
        try (FileWriter writer = new FileWriter("randevular.csv")) {
            for (int i = 0; i < appointments.size(); i++) {
                Appointment a = appointments.get(i);
                writer.write(a.getTitle() + "," + a.getDate() + "," + a.getTime() + "," + a.getLocation() + "," + a.getNotes() + "," + a.getDoctor() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Randevular kaydedilirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }

    private void updateUpcomingAppointmentsList() {
        if (upcomingAppointmentsListView == null) {
            System.err.println("upcomingAppointmentsListView is null!");
            return;
        }

        upcomingAppointmentsListView.getItems().clear();
        upcomingAppointmentsQueue = new MyQueue<>();

        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<Appointment> sortedAppointments = new ArrayList<>();
         for (int i = 0; i < appointments.size(); i++) {
            sortedAppointments.add(appointments.get(i));
        }
        sortedAppointments.sort(Comparator.comparing((Appointment a) -> LocalDate.parse(a.getDate(), dateFormatter))
                                         .thenComparing((Appointment a) -> LocalTime.parse(a.getTime(), timeFormatter))); // Saate göre de sırala, lambda ifadesine açık tip belirttim


        boolean hasUpcoming = false;
        for (Appointment appointment : sortedAppointments) {
            try {
                LocalDate appointmentDate = LocalDate.parse(appointment.getDate(), dateFormatter);
                LocalTime appointmentTime = LocalTime.parse(appointment.getTime(), timeFormatter); // Saat bilgisini de parse et
                LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
                LocalDateTime now = LocalDateTime.now();

                if (appointmentDateTime.isAfter(now)) {
                    upcomingAppointmentsQueue.enqueue(appointment);
                    hasUpcoming = true;
                }
            } catch (Exception e) {
                System.err.println("Yaklaşan randevu tarihi/saati işlenirken hata: " + appointment.getDate() + " " + appointment.getTime() + " - " + e.getMessage());
            }
        }

        while (!upcomingAppointmentsQueue.isEmpty()) {
            Appointment upcomingAppointment = upcomingAppointmentsQueue.dequeue();
             upcomingAppointmentsListView.getItems().add(upcomingAppointment.getDate() + " " + upcomingAppointment.getTime() + " - " + upcomingAppointment.getTitle() + " (" + upcomingAppointment.getLocation() + ")");
        }

        if (!hasUpcoming) {
            upcomingAppointmentsListView.getItems().add("Yaklaşan randevunuz bulunmamaktadır.");
        }
    }


    @FXML
    private void showGeneralInfo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GeneralInfoView.fxml"));
            Parent generalInfoViewParent = loader.load();
            Scene generalInfoScene = new Scene(generalInfoViewParent);

            Stage generalInfoWindow = new Stage();
            generalInfoWindow.setTitle("Detaylı Bilgileri Düzenle");
            generalInfoWindow.setScene(generalInfoScene);
            generalInfoWindow.showAndWait();


            loadUserProfileFromFile();
            updateProfileLabels();
            updateBMI();

            loadEmergencyContactsFromFile();
            updateEmergencyContactList();

            loadGoalsFromFile();
            updateGoalListView();
            updateGoalChart(); //

            loadAppointmentsFromFile();
            updateUpcomingAppointmentsList();


        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Genel Bilgiler Yüklenemedi: " + e.getMessage(), Alert.AlertType.ERROR); // Hata mesajı eklendi
        }
    }

    @FXML
    private void onExit() {
        if (confirm("Çıkmak ve verileri kaydetmek istiyor musunuz?")) {
            saveHealthDataToFile();
            saveUserProfileToFile();
            saveEmergencyContactsToFile();
            saveGoalsToFile();
            saveAppointmentsToFile();

            Stage stage = (Stage) ((Node) healthDataTable).getScene().getWindow();
            stage.close();
        }
    }

    private void saveUserProfileToFile() {
        try (FileWriter writer = new FileWriter("user_profile.csv")) {
            writer.write(userProfile.getFirstName() + "," + userProfile.getLastName() + "," +
                    userProfile.getGender() + "," + userProfile.getBirthDate() + "," +
                    userProfile.getHeight() + "," + (userProfile.getBloodType() != null ? userProfile.getBloodType() : "") + "\n"); // Kan grubu null ise boş kaydet
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserProfileFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("user_profile.csv"))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    userProfile.setFirstName(parts[0].trim());
                    userProfile.setLastName(parts[1].trim());
                    userProfile.setGender(parts[2].trim());
                    
                    String birthDateString = parts[3].trim();
                    try {
                         LocalDate birthDate = LocalDate.parse(birthDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                         userProfile.setBirthDate(birthDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    } catch (Exception e) {
                         userProfile.setBirthDate("");
                    }

                    try {
                        userProfile.setHeight(Double.parseDouble(parts[4].trim()));
                    } catch (NumberFormatException e) {
                        userProfile.setHeight(0);
                    }
                    if (parts.length > 5) {
                        userProfile.setBloodType(parts[5].trim());
                    } else {
                        userProfile.setBloodType(null);
                    }
                } else {
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("user_profile.csv dosyası bulunamadı, varsayılan profil kullanılıyor.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateProfileLabels() {
        if (profileNameLabel == null || profileGenderLabel == null || profileBirthDateLabel == null || profileHeightLabel == null || profileBloodTypeLabel == null) {
            System.err.println("FXML yüklenirken profil etiketleri bulunamadı.");
            return;
        }

        if (userProfile != null) {
            profileNameLabel.setText("Ad Soyad: " + userProfile.getFirstName() + " " + userProfile.getLastName());
            profileGenderLabel.setText("Cinsiyet: " + (userProfile.getGender() != null ? userProfile.getGender() : "-"));
            profileBirthDateLabel.setText("Doğum Tarihi: " + (userProfile.getBirthDate() != null ? userProfile.getBirthDate() : "-"));
            profileHeightLabel.setText("Boy: " + userProfile.getHeight() + " cm");
            profileBloodTypeLabel.setText("Kan Grubu: " + (userProfile.getBloodType() != null && !userProfile.getBloodType().isEmpty() ? userProfile.getBloodType() : "Bilgi Yok"));
        } else {
            // userProfile null ise etiketleri boş veya varsayılan yap
            profileNameLabel.setText("Ad Soyad: -");
            profileGenderLabel.setText("Cinsiyet: -");
            profileBirthDateLabel.setText("Doğum Tarihi: -");
            profileHeightLabel.setText("Boy: -");
            profileBloodTypeLabel.setText("Kan Grubu: -");
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        if (javafx.application.Platform.isFxApplicationThread()) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        } else {
            javafx.application.Platform.runLater(() -> {
                 Alert alert = new Alert(type);
                 alert.setTitle(title);
                 alert.setHeaderText(null);
                 alert.setContentText(content);
                 alert.showAndWait();
            });
        }
    }

    @FXML
    private void onOpenMhrs() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("https://www.mhrs.gov.tr/"));
            } else {
                showAlert("Uyarı", "Tarayıcı açılamadı. Lütfen MHRS web sitesini manuel olarak ziyaret edin.", Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hata", "MHRS sayfası açılamadı: " + e.getMessage(), Alert.AlertType.ERROR); // Hata mesajı eklendi
        }
    }

    @FXML
    private void onAddHealthData() {
        Dialog<HealthData> dialog = new Dialog<>();
        dialog.setTitle("Yeni Sağlık Verisi");
        dialog.setHeaderText("Yeni ölçüm bilgisi giriniz");

        Label l1 = new Label("Tarih (dd.MM.yyyy):");
        Label l2 = new Label("Saat (HH:mm):");
        Label l3 = new Label("Kilo (kg):");
        Label l4 = new Label("Nabız:");
        Label l5 = new Label("BP (Üst):");
        Label l6 = new Label("BP (Alt):");
        Label l7 = new Label("Şeker:");

        TextField t1 = new TextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        TextField t2 = new TextField(LocalTime.now().truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ofPattern("HH:mm")));
        TextField t3 = new TextField();
        TextField t4 = new TextField();
        TextField t5 = new TextField();
        TextField t6 = new TextField();
        TextField t7 = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(l1, 0, 0); grid.add(t1, 1, 0);
        grid.add(l2, 0, 1); grid.add(t2, 1, 1);
        grid.add(l3, 0, 2); grid.add(t3, 1, 2);
        grid.add(l4, 0, 3); grid.add(t4, 1, 3);
        grid.add(l5, 0, 4); grid.add(t5, 1, 4);
        grid.add(l6, 0, 5); grid.add(t6, 1, 5);
        grid.add(l7, 0, 6); grid.add(t7, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    String date = t1.getText().trim();
                    String time = t2.getText().trim();
                    String weightStr = t3.getText().trim();
                    String pulseStr = t4.getText().trim();
                    String bpHighStr = t5.getText().trim();
                    String bpLowStr = t6.getText().trim();
                    String bloodSugarStr = t7.getText().trim();


                    if (date.isEmpty() || time.isEmpty() || weightStr.isEmpty() || pulseStr.isEmpty() || bpHighStr.isEmpty() || bpLowStr.isEmpty() || bloodSugarStr.isEmpty()) { // Tüm alanlar zorunlu
                        showAlert("Hata", "Lütfen tüm alanları doldurun.", Alert.AlertType.ERROR);
                        return null;
                    }

                    double weight;
                    int pulse, bpHigh, bpLow, bloodSugar;
                    try {
                        weight = Double.parseDouble(weightStr);
                        pulse = Integer.parseInt(pulseStr);
                        bpHigh = Integer.parseInt(bpHighStr);
                        bpLow = Integer.parseInt(bpLowStr);
                        bloodSugar = Integer.parseInt(bloodSugarStr);

                         if (weight <= 0 || pulse <= 0 || bpHigh <= 0 || bpLow <= 0 || bloodSugar < 0) { // Şeker 0 olabilir
                              showAlert("Hata", "Kilo, Nabız ve Tansiyon değerleri pozitif sayılar olmalıdır.", Alert.AlertType.ERROR);
                              return null;
                         }

                    } catch (NumberFormatException e) {
                        showAlert("Hata", "Lütfen sayısal alanlara (Kilo, Nabız, BP, Şeker) geçerli değerler girin.", Alert.AlertType.ERROR);
                        return null;
                    }


                    try {
                        LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
                    } catch (Exception e) {
                        showAlert("Hata", "Geçerli bir Tarih (dd.MM.yyyy) veya Saat (HH:mm) formatı girin.", Alert.AlertType.ERROR);
                        return null;
                    }

                    return new HealthData(date, time, weight, pulse, bpHigh, bpLow, bloodSugar);

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Hata", "Veri eklenirken beklenmedik bir hata oluştu: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<HealthData> result = dialog.showAndWait();
        result.ifPresent(healthData -> {
            healthDataList.add(healthData);
            healthDataTable.getItems().add(0, healthData);
            updateAfterHealthDataChange();
            saveHealthDataToFile();
            undoStack.push(healthData);
            redoStack = new MyStack<>();
            showAlert("Başarılı", "Sağlık verisi başarıyla eklendi.", Alert.AlertType.INFORMATION); // Başarı mesajı
        });
    }

    @FXML
    private void onUndo() {
        if (undoStack != null && undoStack.size() > 0) {
            HealthData undone = undoStack.pop();

            if (redoStack == null) redoStack = new MyStack<>();
            redoStack.push(undone);

             boolean removedFromList = healthDataList.remove(undone);

             if (removedFromList) {
                 healthDataTable.getItems().clear();
                 for (int i = 0; i < healthDataList.size(); i++) {
                     healthDataTable.getItems().add(healthDataList.get(i));
                 }

                saveHealthDataToFile();
                sortHealthDataTable();
                updateLastValues();
                updateBMI();

                 showAlert("Geri Al", "Son sağlık verisi geri alındı.", Alert.AlertType.INFORMATION);
              } else {
                  System.err.println("Geri alınacak veri linked listede bulunamadı!");
                  showAlert("Hata", "Geri alınacak veri bulunamadı.", Alert.AlertType.ERROR);
                   undoStack = new MyStack<>();
              }

        } else {
            showAlert("Geri Al", "Geri alınacak işlem yok.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void onRedo() {
        if (redoStack != null && redoStack.size() > 0) {
            HealthData redone = redoStack.pop();
            
            if (undoStack == null) undoStack = new MyStack<>();
            undoStack.push(redone);

            healthDataList.add(redone);
            healthDataTable.getItems().add(redone);

            saveHealthDataToFile();
            sortHealthDataTable();
            updateLastValues();
            updateBMI();

            showAlert("Yinele", "Son geri alınan sağlık verisi yinelendi.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Yinele", "Yinelenecek işlem yok.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void onDeleteHealthData() {
        HealthData selectedItem = healthDataTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Silme Onayı");
            alert.setHeaderText(null);
            alert.setContentText("Seçili sağlık verisini silmek istediğinizden emin misiniz?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean removedFromList = healthDataList.remove(selectedItem);
                boolean removedFromTable = healthDataTable.getItems().remove(selectedItem);

                 if (removedFromList || removedFromTable) {
                    updateAfterHealthDataChange();

                    // Silinen veriyi undoStack'e ekle
                    undoStack.push(selectedItem);
                    redoStack = new MyStack<>();

                    showAlert("Başarılı", "Sağlık verisi başarıyla silindi.", Alert.AlertType.INFORMATION);
                 } else {
                     System.err.println("Silinecek veri listede veya tabloda bulunamadı!");
                     showAlert("Hata", "Silinecek veri bulunamadı.", Alert.AlertType.ERROR);
                 }
            }
        } else {
            showAlert("Uyarı", "Lütfen silmek için bir sağlık verisi seçin.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void onEditProfileButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileEditView.fxml"));
            Parent root = loader.load();

            ProfileEditController controller = loader.getController();
            if (userProfile != null) {
                 controller.setUserProfile(userProfile);
            } else {
                 UserProfile defaultProfile = new UserProfile("", "", "", "", 0);
                 controller.setUserProfile(defaultProfile);
            }


            Stage stage = new Stage();
            stage.setTitle("Profil Düzenle");
            stage.setScene(new Scene(root));
            controller.setDialogStage(stage);

            stage.showAndWait();


            if (controller.isSaved()) {
                userProfile = controller.getUserProfile();
                saveUserProfileToFile();
                updateProfileLabels();
                updateBMI();
                showAlert("Başarılı", "Profil bilgileri güncellendi.", Alert.AlertType.INFORMATION);
            } else {
                 System.out.println("Profil düzenleme iptal edildi veya kaydedilmedi.");
            }


        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Profil düzenleme penceresi açılamadı: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
             e.printStackTrace();
             showAlert("Hata", "Profil düzenlenirken beklenmedik bir hata oluştu: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateDetailLabels(HealthData selectedItem) {
        if (selectedItem != null) {
            detailDateLabel.setText("Tarih: " + selectedItem.getDate());
            detailTimeLabel.setText("Saat: " + selectedItem.getTime());
            String weightText = "Vücut Kitle Endeksi: " + selectedItem.getWeight() + " kg";
            if (userProfile != null && userProfile.getHeight() > 0) {
                double weight = selectedItem.getWeight();
                double heightInMeters = userProfile.getHeight() / 100.0;
                if (heightInMeters > 0) {
                    double bmi = weight / (heightInMeters * heightInMeters);
                    weightText += String.format(" (BMI: %.2f)", bmi);
                }
            }
            detailWeightLabel.setText(weightText);

            detailPulseLabel.setText("Nabız: " + selectedItem.getPulse() + " (Normal: 60-100)");
            detailBpLabel.setText("Tansiyon: " + selectedItem.getBpHigh() + "/" + selectedItem.getBpLow() + " (Normal: Alt 80 / Üst 120)");
            detailSugarLabel.setText("Şeker: " + (selectedItem.getBloodSugar() == 0 ? "-" : selectedItem.getBloodSugar()) + " (Açlık Normal: 70-100)");
            String statusText = "Durum: ";
             if (selectedItem.getBloodSugar() > 100) {
                 statusText += "Şeker Yüksek. ";
            } else if (selectedItem.getBloodSugar() >= 70 && selectedItem.getBloodSugar() <= 100) {
                 statusText += "Şeker Normal. ";
            } else if (selectedItem.getBloodSugar() < 70 && selectedItem.getBloodSugar() > 0) {
                statusText += "Şeker Düşük. ";
            }
            if (selectedItem.getPulse() < 60) {
                statusText += "Nabız Düşük. ";
            } else if (selectedItem.getPulse() > 100) {
                statusText += "Nabız Yüksek. ";
            } else {
                 statusText += "Nabız Normal. ";
            }
             if (selectedItem.getBpHigh() < 120 && selectedItem.getBpLow() < 80) {
                 statusText += "Tansiyon Normal. ";
             } else if (selectedItem.getBpHigh() >= 120 && selectedItem.getBpHigh() <= 129 && selectedItem.getBpLow() < 80) {
                 statusText += "Tansiyon Yüksek Normal. ";
             } else if ((selectedItem.getBpHigh() >= 130 && selectedItem.getBpHigh() <= 139) || (selectedItem.getBpLow() >= 80 && selectedItem.getBpLow() <= 89)) {
                 statusText += "Tansiyon Evre 1 Hipertansiyon. ";
             } else if (selectedItem.getBpHigh() >= 140 || selectedItem.getBpLow() >= 90) {
                 statusText += "Tansiyon Evre 2 Hipertansiyon. ";
             } else if (selectedItem.getBpHigh() > 180 || selectedItem.getBpLow() > 120) {
                 statusText += "Tansiyon Hipertansif Kriz! ";
             } else {
                 statusText += "Tansiyon Belirsiz. ";
             }

            if (statusText.equals("Durum: ")) {
                statusText += "Normal.";
            }
            detailStatusLabel.setText(statusText.trim());

        } else {
            detailDateLabel.setText("Tarih: -");
            detailTimeLabel.setText("Saat: -");
            detailWeightLabel.setText("Kilo: - (Normal BMI: 18.5-24.9)");
            detailPulseLabel.setText("Nabız: - (Normal: 60-100)");
            detailBpLabel.setText("BP: -/- (Normal: Alt 80 / Üst 120)");
            detailSugarLabel.setText("Şeker: - (Açlık Normal: 70-100)");
            detailStatusLabel.setText("Durum: -");
        }
    }

    private void saveGoalsToFile() {
        try (FileWriter writer = new FileWriter("hedefler.csv")) {
            for (int i = 0; i < goalList.size(); i++) {
                Goal g = goalList.get(i);
                writer.write(g.getType() + "," + g.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
             showAlert("Hata", "Hedefler kaydedilirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }

    private void updateAfterHealthDataChange() {
        healthDataTable.refresh();
        sortHealthDataTable();
        updateLastValues();
        updateBMI();
        saveHealthDataToFile();
    }
}