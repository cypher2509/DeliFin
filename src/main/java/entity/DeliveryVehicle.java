package entity;

public class DeliveryVehicle {
    private String number;
    private String make;
    private String model;
    private int make_year;
    private String purchase_date;

    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public int getMake_year() {
        return make_year;
    }
    public void setYear(int year) {
        this.make_year = year;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getPurchase_date() {
        return purchase_date;
    }
    public void setPurchase_date(String purchase_date) {
        this.purchase_date = purchase_date;
    }
}
