package entity;

import java.time.LocalDate;

public class RouteAssignment {
    private int id;
    private String driverId;
    private int routeId;
    private LocalDate date;  // 🔥 Changed from java.sql.Date to LocalDate
    private boolean isStarted;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public boolean isStarted() { return isStarted; }
    public void setStarted(boolean started) { isStarted = started; }
}