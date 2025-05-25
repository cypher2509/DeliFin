package main;

import entity.RouteHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//{
//        "driverId": "drv001",
//        "routeId": 3,
//        "vehicleId": 101,
//        "date": "2025-05-21",
//        "startTime": "08:30:00",
//        "endTime": "11:00:00",
//        "startKmr": 1200.0,
//        "endKmr": 1217.5,
//        "rating": 5,
//        "assignedPackages": 15,
//        "deliveredPackages": 14
//        }


@RestController
@RequestMapping("/route-history")
public class RouteHistoryController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RouteHistoryController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // ✅ GET - Get all route history
    @GetMapping
    public ResponseEntity<List<RouteHistory>> getAllRouteHistory() {
        String sql = "SELECT * FROM route_history";

        try {
            List<RouteHistory> historyList = jdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(RouteHistory.class)
            );
            return ResponseEntity.ok(historyList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ GET - Get route history by ID
    @GetMapping("/{id}")
    public ResponseEntity<RouteHistory> getRouteHistoryById(@PathVariable int id) {
        String sql = "SELECT * FROM route_history WHERE id = ?";

        try {
            List<RouteHistory> result = jdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(RouteHistory.class),
                    id
            );
            if (result.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

//    {
//        "driverId": "drv001",
//            "routeId": 5,
//            "vehicleId": "VH001",
//            "date": "2025-05-22",
//            "startTime": "08:30:00",
//            "startKmr": 1500.5,
//            "assignedPackages": 12
//    }
    // ✅ Driver starts the route
    @PostMapping("/start")
    public ResponseEntity<String> startRoute(@RequestBody RouteHistory history) {
        String sql = "INSERT INTO route_history (driver_id, route_id, vehicle_id, date, start_time, start_kmr, assigned_packages, route_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 'started')";

        try {
            jdbcTemplate.update(sql,
                    history.getDriverId(),
                    history.getRouteId(),
                    history.getVehicleId(),
                    history.getDate(),
                    history.getStartTime(),
                    history.getStartKmr(),
                    history.getAssignedPackages()
            );
            return ResponseEntity.ok("Route started and logged in route history.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to start route.");
        }
    }

//    {
//        "endTime": "11:00:00",
//            "endKmr": 1516.4,
//            "deliveredPackages": 11,
//            "rating": 4
//    }


    // ✅ Driver ends the route
    @PutMapping("/complete/{id}")
    public ResponseEntity<String> completeRoute(@PathVariable int id, @RequestBody RouteHistory history) {
        String sql = "UPDATE route_history SET " +
                "end_time = ?, end_kmr = ?, delivered_packages = ?, rating = ?, route_status = 'completed' " +
                "WHERE id = ? AND route_status = 'started'";

        try {
            int rows = jdbcTemplate.update(sql,
                    history.getEndTime(),
                    history.getEndKmr(),
                    history.getDeliveredPackages(),
                    history.getRating(),
                    id
            );

            if (rows > 0) {
                return ResponseEntity.ok("Route marked as completed.");
            } else {
                return ResponseEntity.status(404).body("Route not found or not started yet.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to complete route.");
        }
    }
}
