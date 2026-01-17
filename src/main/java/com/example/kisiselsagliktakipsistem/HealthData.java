package com.example.kisiselsagliktakipsistem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;

public class HealthData {
    private SimpleStringProperty date;
    private SimpleStringProperty time;
    private SimpleDoubleProperty weight;
    private SimpleIntegerProperty pulse;
    private SimpleIntegerProperty bpHigh;
    private SimpleIntegerProperty bpLow;
    private SimpleIntegerProperty bloodSugar;
    private String status;

    public HealthData(String date, String time, double weight, int pulse, int bpHigh, int bpLow, int bloodSugar) {
        this.date = new SimpleStringProperty(date);
        this.time = new SimpleStringProperty(time);
        this.weight = new SimpleDoubleProperty(weight);
        this.pulse = new SimpleIntegerProperty(pulse);
        this.bpHigh = new SimpleIntegerProperty(bpHigh);
        this.bpLow = new SimpleIntegerProperty(bpLow);
        this.bloodSugar = new SimpleIntegerProperty(bloodSugar);
        this.status = calculateStatus();
    }

    public String getDate() {
        return date.get();
    }

    public String getTime() {
        return time.get();
    }

    public double getWeight() {
        return weight.get();
    }

    public int getPulse() {
        return pulse.get();
    }

    public int getBpHigh() {
        return bpHigh.get();
    }

    public int getBpLow() {
        return bpLow.get();
    }

    public int getBloodSugar() {
        return bloodSugar.get();
    }

    public String getStatus() {
        return status;
    }

    private String calculateStatus() {
        StringBuilder statusText = new StringBuilder();

        if (pulse.get() < 60) {
            statusText.append("Nabız: Düşük. ");
        } else if (pulse.get() > 100) {
            statusText.append("Nabız: Yüksek. ");
        } else {
            statusText.append("Nabız: Normal. ");
        }

        if (bpHigh.get() < 120 && bpLow.get() < 80) {
            statusText.append("BP: Normal. ");
        } else if ((bpHigh.get() >= 120 && bpHigh.get() <= 129) && bpLow.get() < 80) {
            statusText.append("BP: Yüksek Normal. ");
        } else if ((bpHigh.get() >= 130 && bpHigh.get() <= 139) || (bpLow.get() >= 80 && bpLow.get() <= 89)) {
            statusText.append("BP: Evre 1 Hipertansiyon. ");
        } else if (bpHigh.get() >= 140 || bpLow.get() >= 90) {
            statusText.append("BP: Evre 2 Hipertansiyon. ");
        } else {
            statusText.append("BP: Belirsiz. ");
        }

        if (bloodSugar.get() > 0) {
            if (bloodSugar.get() < 70) {
                statusText.append("Şeker: Düşük. ");
            } else if (bloodSugar.get() <= 100) {
                statusText.append("Şeker: Normal. ");
            } else if (bloodSugar.get() <= 125) {
                statusText.append("Şeker: Prediyabet. ");
            } else {
                statusText.append("Şeker: Yüksek. ");
            }
        } else {
            statusText.append("Şeker: Yok. ");
        }

        statusText.append("Kilo: ").append(weight.get()).append(" kg.");

        return statusText.toString().trim();
    }
}