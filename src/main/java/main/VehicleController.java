package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import config.MySQLConfig;

import entity.DeliveryVehicle;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @GetMapping
    public ResponseEntity<ObjectNode> getAllVehicles() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode vehicles = objectMapper.createArrayNode();

        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "SELECT * FROM deliveryVehicle";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ObjectNode vehicle = JsonNodeFactory.instance.objectNode();
                vehicle.put("number", rs.getString("number"));
                vehicle.put("make", rs.getString("make"));
                vehicle.put("entity", rs.getString("entity"));
                vehicle.put("make_year", rs.getInt("make_year"));
                vehicle.put("purchase_date", rs.getString("purchase_date"));
                vehicles.add(vehicle);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ObjectNode response = objectMapper.createObjectNode();
        response.set("vehicles", vehicles);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjectNode> getVehicleById(@PathVariable String id) {
        ObjectMapper objectMapper = new ObjectMapper();

        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "SELECT * FROM deliveryVehicle WHERE number = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ObjectNode vehicle = JsonNodeFactory.instance.objectNode();
                vehicle.put("number", rs.getString("number"));
                vehicle.put("make", rs.getString("make"));
                vehicle.put("entity", rs.getString("entity"));
                vehicle.put("make_year", rs.getInt("make_year"));
                vehicle.put("purchase_date", rs.getString("purchase_date"));
                return ResponseEntity.ok(vehicle);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping
    public ResponseEntity<String> addVehicle(@RequestBody DeliveryVehicle vehicle) {
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "INSERT INTO deliveryVehicle(number, make, model, make_year, purchase_date) " +
                    "VALUES(?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE make = VALUES(make), " +
                    "model = VALUES(model), make_year = VALUES(make_year), purchase_date = VALUES(purchase_date)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, vehicle.getNumber());
            pstmt.setString(2, vehicle.getMake());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setInt(4, vehicle.getMake_year());
            pstmt.setString(5, vehicle.getPurchase_date());
            pstmt.executeUpdate();

            return ResponseEntity.status(HttpStatus.CREATED).body("Delivery Vehicle information added/updated successfully.");
        } catch (Exception err) {
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding/updating information to the database.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateVehicle(@PathVariable String id, @RequestBody DeliveryVehicle vehicle) {
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "UPDATE deliveryVehicle SET make = ?, model = ?, make_year = ?, purchase_date = ? WHERE number = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, vehicle.getMake());
            pstmt.setString(2, vehicle.getModel());
            pstmt.setInt(3, vehicle.getMake_year());
            pstmt.setString(4, vehicle.getPurchase_date());
            pstmt.setString(5, id);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                return ResponseEntity.ok("Delivery Vehicle updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found.");
            }
        } catch (Exception err) {
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating vehicle information.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable String id) {
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "DELETE FROM deliveryVehicle WHERE number = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                return ResponseEntity.ok("Vehicle deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found.");
            }
        } catch (Exception err) {
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting vehicle.");
        }
    }
}
