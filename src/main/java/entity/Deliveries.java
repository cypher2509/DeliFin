package entity;


import jakarta.persistence.*;

@Entity
@Table(name = "deliveries")

public class Deliveries {
    @Id
    private int id;
    private String paySlipId;
    private String day;
    private String date;
    private int deliveries;

    public Deliveries() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPaySlipId() {
        return paySlipId;
    }

    public void setPaySlipId(String paySlipId) {
        this.paySlipId = paySlipId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(int deliveries) {
        this.deliveries = deliveries;
    }

}
