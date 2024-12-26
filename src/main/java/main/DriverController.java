package main;

import model.DeliveryDriver;
import config.MySQLConfig;
import model.DriverDeliverySummary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    // CREATE a new driver
    @PostMapping
    public ResponseEntity<String> createDriver(@RequestBody DeliveryDriver driver) {
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "INSERT INTO driver (id, firstName, lastName, email, rate_per_delivery) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE firstName = VALUES(firstName), lastName = VALUES(lastName), " +
                    "email = VALUES(email), rate_per_delivery = VALUES(rate_per_delivery)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, driver.getId());
                stmt.setString(2, driver.getFirstName());
                stmt.setString(3, driver.getLastName());
                stmt.setString(4, driver.getEmail());
                stmt.setFloat(5, driver.getRatePerDelivery());
                stmt.executeUpdate();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Driver created/updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating/updating driver.");
        }
    }

    // READ all drivers
    @GetMapping
    public ResponseEntity<List<DeliveryDriver>> getAllDrivers() {
        List<DeliveryDriver> drivers = new ArrayList<>();
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "SELECT * FROM driver";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DeliveryDriver driver = new DeliveryDriver();
                    driver.setId(rs.getString("id"));
                    driver.setFirstName(rs.getString("firstName"));
                    driver.setLastName(rs.getString("lastName"));
                    driver.setEmail(rs.getString("email"));
                    driver.setRatePerDelivery(rs.getFloat("rate_per_delivery"));
                    drivers.add(driver);
                }
            }
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // READ a specific driver by ID
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDriver> getDriverById(@PathVariable String id) {
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "SELECT * FROM driver WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        DeliveryDriver driver = new DeliveryDriver();
                        driver.setId(rs.getString("id"));
                        driver.setFirstName(rs.getString("firstName"));
                        driver.setLastName(rs.getString("lastName"));
                        driver.setEmail(rs.getString("email"));
                        driver.setRatePerDelivery(rs.getFloat("rate_per_delivery"));
                        return ResponseEntity.ok(driver);
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // UPDATE a driver
    @PutMapping("/{id}")
    public ResponseEntity<String> updateDriver(@PathVariable String id, @RequestBody DeliveryDriver updatedDriver) {
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "UPDATE driver SET firstName = ?, lastName = ?, email = ?, rate_per_delivery = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, updatedDriver.getFirstName());
                stmt.setString(2, updatedDriver.getLastName());
                stmt.setString(3, updatedDriver.getEmail());
                stmt.setFloat(4, updatedDriver.getRatePerDelivery());
                stmt.setString(5, id);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    return ResponseEntity.ok("Driver updated successfully.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating driver.");
        }
    }

    // DELETE a driver
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable String id) {
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "DELETE FROM driver WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting driver.");
        }
    }

    @GetMapping("/{driverId}/summary")
    public ResponseEntity<List<DriverDeliverySummary>> getDeliveriesByDriver(
            @PathVariable String driverId,
            @RequestParam(required = false) String period, // "weekly", "monthly", "yearly"
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer week) {

        List<DriverDeliverySummary> summaries = new ArrayList<>();

        try (Connection conn = MySQLConfig.getConnection()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT d.date, d.deliveries, dr.rate_per_delivery, " +
                            "       (d.deliveries * dr.rate_per_delivery) AS amount_paid " +
                            "FROM deliveries d " +
                            "JOIN paySlip ps ON d.paySlipId = ps.id " +
                            "JOIN driver dr ON ps.driverId = dr.id " +
                            "WHERE dr.id = ? "
            );

            if ("weekly".equalsIgnoreCase(period) && week != null) {
                sql.append("AND WEEK(d.date, 1) = ? ");
            } else if ("monthly".equalsIgnoreCase(period) && month != null) {
                sql.append("AND MONTH(d.date) = ? ");
            } else if ("yearly".equalsIgnoreCase(period) && year != null) {
                sql.append("AND YEAR(d.date) = ? ");
            }


            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                stmt.setString(1, driverId);

                if ("weekly".equalsIgnoreCase(period) && week != null) {
                    stmt.setInt(2, week);
                } else if ("monthly".equalsIgnoreCase(period) && month != null) {
                    stmt.setInt(2, month);
                } else if ("yearly".equalsIgnoreCase(period) && year != null) {
                    stmt.setInt(2, year);
                }
                System.out.println(stmt.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        DriverDeliverySummary summary = new DriverDeliverySummary();
                        summary.setDate(rs.getString("date"));
                        summary.setDeliveries(rs.getInt("deliveries"));
                        summary.setAmountPaid(rs.getDouble("amount_paid"));
                        summaries.add(summary);
                    }
                }
            }

            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
