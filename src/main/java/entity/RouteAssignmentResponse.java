package entity;

import java.time.LocalDate;

public class RouteAssignmentResponse extends Route {  // 🔥 Inherit from Route
    private String driverId;
    private LocalDate date;  // 🔥 Use LocalDate
    private boolean isStarted;

    // Getters and Setters
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public boolean isStarted() { return isStarted; }
    public void setStarted(boolean started) { isStarted = started; }
}