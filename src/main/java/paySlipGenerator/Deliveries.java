package paySlipGenerator;

public class Deliveries {
    private int id;
    private int paySlipId;
    private String day;
    private String date;
    private int deliveries;
    private double pricePerDelivery;
    private double amount;

    public Deliveries(int id, int paySlipId, String day, String date, int deliveries, double pricePerDelivery, double amount) {
        this.id = id;
        this.paySlipId = paySlipId;
        this.day = day;
        this.date = date;
        this.deliveries = deliveries;
        this.pricePerDelivery = pricePerDelivery;
        this.amount = amount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPaySlipId() {
        return paySlipId;
    }

    public void setPaySlipId(int paySlipId) {
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

    public double getPricePerDelivery() {
        return pricePerDelivery;
    }

    public void setPricePerDelivery(double pricePerDelivery) {
        this.pricePerDelivery = pricePerDelivery;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
