package model;

import jakarta.persistence.*;

@Entity
@Table(name = "payslip") // Optional if the class name matches the table name
public class PaySlip {

    @Id
    private String id; // Maps directly to the "id" column in the database

    private int weekNumber; // Maps directly to the "weekNumber" column

    private String driverId; // Maps directly to the "driverId" column

    // No-argument constructor (required by JPA)
    public PaySlip() {}
    public PaySlip(String id, int weekNumber, String driverId) {
        this.id = id;
        this.weekNumber = weekNumber;
        this.driverId = driverId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
}
