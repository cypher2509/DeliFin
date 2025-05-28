package main;

import entity.RouteAssignment;
import entity.RouteAssignmentResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//{
//        "driverId": "drv001",
//        "routeId": 3,
//        "date": "2025-05-21",
//        "teamId": 1,
//        "started": false
//        }
//

@RestController
@RequestMapping("/route-assignment")
public class RouteAssignmentController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RouteAssignmentController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ POST - Assign a route
    @PostMapping
    public ResponseEntity<String> createAssignment(@RequestBody RouteAssignment assignment) {
        String sql = "INSERT INTO route_assignment (driver_id, route_id, date, is_started) " +
                "VALUES (?, ?, ?, ?)";

        try {
            jdbcTemplate.update(sql,
                    assignment.getDriverId(),
                    assignment.getRouteId(),
                    assignment.getDate(),
                    assignment.isStarted()
            );
            return ResponseEntity.ok("Route assigned successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to assign route.");
        }
    }

    // ✅ PUT - Update an assignment
    @PutMapping("/{id}")
    public ResponseEntity<String> updateAssignment(@PathVariable int id, @RequestBody RouteAssignment assignment) {
        String sql = "UPDATE route_assignment SET driver_id = ?, route_id = ?, date = ?, is_started = ? " +
                "WHERE id = ?";

        try {
            int rows = jdbcTemplate.update(sql,
                    assignment.getDriverId(),
                    assignment.getRouteId(),
                    assignment.getDate(),
                    assignment.isStarted(),
                    id
            );
            if (rows > 0) {
                return ResponseEntity.ok("Assignment updated.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to update assignment.");
        }
    }

    // ✅ DELETE - Delete an assignment
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssignment(@PathVariable int id) {
        String sql = "DELETE FROM route_assignment WHERE id = ?";

        try {
            int rows = jdbcTemplate.update(sql, id);
            if (rows > 0) {
                return ResponseEntity.ok("Assignment deleted.");
            } else {
                return ResponseEntity.status(404).body("Assignment not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to delete assignment.");
        }
    }

    // ✅ GET - All assignments
    @GetMapping
    public ResponseEntity<List<RouteAssignment>> getAllAssignments() {
        String sql = "SELECT * FROM route_assignment";

        try {
            List<RouteAssignment> assignments = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RouteAssignment.class));
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

@GetMapping("/{driver_id}")
public ResponseEntity<RouteAssignmentResponse> getAssignmentByDriverId(@PathVariable String driver_id) {
    String sql = "SELECT * FROM route_assignment_with_route WHERE driver_id = ? AND `date` = CURRENT_DATE";
    try {
        System.out.println(driver_id);
        List<RouteAssignmentResponse> result = jdbcTemplate.query(
            sql, 
            new BeanPropertyRowMapper<>(RouteAssignmentResponse.class), 
            driver_id
        );

        if (result.isEmpty()) {
            return ResponseEntity.ok(null);  // Return null if no assignment found
        }

        return ResponseEntity.ok(result.get(0));  // Return the first matching assignment
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().build();  // Internal error
    }
}
}
