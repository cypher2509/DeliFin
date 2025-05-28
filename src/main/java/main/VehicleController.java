package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.MySQLConfig;
import entity.DeliveryVehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public ResponseEntity<ObjectNode> getAllVehicles() {
        String sql = "SELECT * FROM deliveryVehicle";

        List<ObjectNode> vehicles = jdbcTemplate.query(sql, (rs, rowNum) -> mapVehicle(rs));

        ArrayNode vehiclesArray = objectMapper.createArrayNode();
        vehiclesArray.addAll(vehicles);

        ObjectNode response = objectMapper.createObjectNode();
        response.set("vehicles", vehiclesArray);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjectNode> getVehicleById(@PathVariable String id) {
        String sql = "SELECT * FROM deliveryVehicle WHERE number = ?";

        List<ObjectNode> vehicles = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> mapVehicle(rs));

        if (vehicles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok(vehicles.get(0));
        }
    }

    @PostMapping
    public ResponseEntity<String> addVehicle(@RequestBody DeliveryVehicle vehicle) {
        String sql = "INSERT INTO deliveryVehicle(number, make, model, make_year, purchase_date) " +
                "VALUES(?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE make = VALUES(make), " +
                "model = VALUES(model), make_year = VALUES(make_year), purchase_date = VALUES(purchase_date)";
        try {
            jdbcTemplate.update(sql,
                    vehicle.getNumber(),
                    vehicle.getMake(),
                    vehicle.getModel(),
                    vehicle.getMake_year(),
                    vehicle.getPurchase_date());
            return ResponseEntity.status(HttpStatus.CREATED).body("Delivery Vehicle information added/updated successfully.");
        } catch (Exception err) {
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding/updating information to the database.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateVehicle(@PathVariable String id, @RequestBody DeliveryVehicle vehicle) {
        String sql = "UPDATE deliveryVehicle SET make = ?, model = ?, make_year = ?, purchase_date = ? WHERE number = ?";
        try {
            int rowsUpdated = jdbcTemplate.update(sql,
                    vehicle.getMake(),
                    vehicle.getModel(),
                    vehicle.getMake_year(),
                    vehicle.getPurchase_date(),
                    id);
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
        String sql = "DELETE FROM deliveryVehicle WHERE number = ?";
        try {
            int rowsDeleted = jdbcTemplate.update(sql, id);
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

    private ObjectNode mapVehicle(ResultSet rs) throws SQLException {
        ObjectNode vehicle = JsonNodeFactory.instance.objectNode();
        vehicle.put("number", rs.getString("number"));
        vehicle.put("make", rs.getString("make"));
        vehicle.put("model", rs.getString("model"));
        vehicle.put("make_year", rs.getInt("make_year"));
        vehicle.put("purchase_date", rs.getString("purchase_date"));
        return vehicle;
    }
}