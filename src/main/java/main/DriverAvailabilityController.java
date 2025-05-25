package main;

import entity.WeeklyAvailabilityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/driver")
public class DriverAvailabilityController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DriverAvailabilityController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ Update or Insert Weekly Availability
    @PutMapping("/availability")
    public ResponseEntity<String> updateAvailability(@RequestBody WeeklyAvailabilityRequest request) {
        String sql = "REPLACE INTO driver_availability " +
                "(driver_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.update(sql,
                    request.getDriverId(),
                    request.getMonday(),
                    request.getTuesday(),
                    request.getWednesday(),
                    request.getThursday(),
                    request.getFriday(),
                    request.getSaturday(),
                    request.getSunday()
            );
            return ResponseEntity.ok("Availability updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating availability.");
        }
    }

    // ✅ Get Weekly Availability
    @GetMapping("/availability/{driverId}")
    public ResponseEntity<Object> getAvailability(@PathVariable String driverId) {
        String sql = "SELECT * FROM driver_availability WHERE driver_id = ?";

        try {
            Map<String, Object> availability = jdbcTemplate.queryForMap(sql, driverId);

            if (availability.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Availability not found for driver: " + driverId);
            }

            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving availability.");
        }
    }
}
