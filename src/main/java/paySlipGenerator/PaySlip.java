package paySlipGenerator;

import java.util.List;

public class PaySlip {
    private int id;
    private String driverName;
    private int weekNumber;
    private String driverId;
    private double totalAmount;
    private List<Deliveries> deliveries;

    // Constructor
    public PaySlip(int id, String driverName, int weekNumber, String driverId, double totalAmount) {
        this.id = id;
        this.driverName = driverName;
        this.weekNumber = weekNumber;
        this.driverId = driverId;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Deliveries> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<Deliveries> deliveries) {
        this.deliveries = deliveries;
    }
}
