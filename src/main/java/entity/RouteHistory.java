package entity;

import java.sql.Date;
import java.sql.Time;

public class RouteHistory {
    private int id;
    private String driverId;
    private int routeId;
    private String vehicleId;
    private Date date;
    private Time startTime;
    private Time endTime;
    private String routeStatus;
    private double startKmr;
    private double endKmr;
    private int assignedPackages;
    private int deliveredPackages;
    private int rating;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getRouteStatus() {
        return routeStatus;
    }

    public void setRouteStatus(String routeStatus) {
        this.routeStatus = routeStatus;
    }

    public double getStartKmr() {
        return startKmr;
    }

    public void setStartKmr(double startKmr) {
        this.startKmr = startKmr;
    }

    public double getEndKmr() {
        return endKmr;
    }

    public void setEndKmr(double endKmr) {
        this.endKmr = endKmr;
    }

    public int getAssignedPackages() {
        return assignedPackages;
    }

    public void setAssignedPackages(int assignedPackages) {
        this.assignedPackages = assignedPackages;
    }

    public int getDeliveredPackages() {
        return deliveredPackages;
    }

    public void setDeliveredPackages(int deliveredPackages) {
        this.deliveredPackages = deliveredPackages;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
