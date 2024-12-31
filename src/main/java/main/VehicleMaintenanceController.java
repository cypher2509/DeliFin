package main;

import model.VehicleMaintenance;
import config.MySQLConfig;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/maintenance")
public class VehicleMaintenanceController {

    // CREATE: Add a new maintenance record
    @PostMapping
    public ResponseEntity<String> addMaintenance(@RequestBody VehicleMaintenance maintenance) {
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "INSERT INTO VehicleMaintenance (number, cost, type, date, comments) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maintenance.getNumber());
            pstmt.setDouble(2, maintenance.getCost());
            pstmt.setString(3, maintenance.getType());
            pstmt.setDate(4, Date.valueOf(maintenance.getDate())); // Convert string to SQL date
            pstmt.setString(5, maintenance.getComments());
            pstmt.executeUpdate();
            return ResponseEntity.status(HttpStatus.CREATED).body("Maintenance record added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding maintenance record.");
        }
    }

    // READ: Get all maintenance records
    @GetMapping
    public ResponseEntity<List<VehicleMaintenance>> getAllMaintenanceRecords() {
        List<VehicleMaintenance> maintenanceList = new ArrayList<>();
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "SELECT * FROM VehicleMaintenance";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                VehicleMaintenance maintenance = new VehicleMaintenance();
                maintenance.setNumber(rs.getString("number"));
                maintenance.setCost(rs.getDouble("cost"));
                maintenance.setType(rs.getString("type"));
                maintenance.setDate(rs.getDate("date").toString());
                maintenance.setComments(rs.getString("comments"));
                maintenanceList.add(maintenance);
            }
            return ResponseEntity.ok(maintenanceList);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // READ: Get a single maintenance record by ID
    @GetMapping("/{id}")
    public ResponseEntity<VehicleMaintenance> getMaintenanceById(@PathVariable int id) {
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "SELECT * FROM VehicleMaintenance WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                VehicleMaintenance maintenance = new VehicleMaintenance();
                maintenance.setNumber(rs.getString("number"));
                maintenance.setCost(rs.getDouble("cost"));
                maintenance.setType(rs.getString("type"));
                maintenance.setDate(rs.getDate("date").toString());
                maintenance.setComments(rs.getString("comments"));
                return ResponseEntity.ok(maintenance);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Add this method to your VehicleMaintenanceController class

    // READ: Get all maintenance records for a specific vehicle
    @GetMapping("/vehicle/{number}")
    public ResponseEntity<List<VehicleMaintenance>> getMaintenanceByVehicleNumber(@PathVariable String number) {
        List<VehicleMaintenance> maintenanceList = new ArrayList<>();
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "SELECT * FROM VehicleMaintenance WHERE number = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, number);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                VehicleMaintenance maintenance = new VehicleMaintenance();
                maintenance.setNumber(rs.getString("number"));
                maintenance.setCost(rs.getDouble("cost"));
                maintenance.setType(rs.getString("type"));
                maintenance.setDate(rs.getDate("date").toString());
                maintenance.setComments(rs.getString("comments"));
                maintenanceList.add(maintenance);
            }

            if (maintenanceList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok(maintenanceList);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // UPDATE: Update a maintenance record by ID
    @PutMapping("/{id}")
    public ResponseEntity<String> updateMaintenance(@PathVariable int id, @RequestBody VehicleMaintenance maintenance) {
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "UPDATE VehicleMaintenance SET number = ?, cost = ?, type = ?, date = ?, comments = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maintenance.getNumber());
            pstmt.setDouble(2, maintenance.getCost());
            pstmt.setString(3, maintenance.getType());
            pstmt.setDate(4, Date.valueOf(maintenance.getDate())); // Convert string to SQL date
            pstmt.setString(5, maintenance.getComments());
            pstmt.setInt(6, id);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                return ResponseEntity.ok("Maintenance record updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Maintenance record not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating maintenance record.");
        }
    }

    // DELETE: Delete a maintenance record by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMaintenance(@PathVariable int id) {
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "DELETE FROM VehicleMaintenance WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                return ResponseEntity.ok("Maintenance record deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Maintenance record not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting maintenance record.");
        }
    }
}
