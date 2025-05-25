package entity;

import java.sql.Date;

public class RouteAssignment {
    private int id;
    private String driverId;
    private int routeId;
    private Date date;
    private boolean isStarted;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }


    public boolean isStarted() { return isStarted; }
    public void setStarted(boolean started) { isStarted = started; }
}
