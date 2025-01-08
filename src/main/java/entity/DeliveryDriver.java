package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "driver")
public class DeliveryDriver {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private double ratePerDelivery;

    // Constructors
    public DeliveryDriver() {}

    public DeliveryDriver(String id, String firstName, String lastName, String email, Float ratePerDelivery) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.ratePerDelivery = ratePerDelivery;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getRatePerDelivery() {
        return ratePerDelivery;
    }

    public void setRatePerDelivery(double ratePerDelivery) {
        this.ratePerDelivery = ratePerDelivery;
    }
}
