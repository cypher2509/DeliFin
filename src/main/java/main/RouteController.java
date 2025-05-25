package main;

import entity.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/route")
public class RouteController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RouteController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ POST - Create a new route
    @PostMapping
    public ResponseEntity<String> createRoute(@RequestBody Route route) {
        String sql = "INSERT INTO route (route_no, wave, est_time, est_distance, volume, difficulty, stops) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.update(sql,
                    route.getRouteNo(),
                    route.getWave(),
                    route.getEstTime(),
                    route.getEstDistance(),
                    route.getVolume(),
                    route.getDifficulty(),
                    route.getStops()
            );
            return ResponseEntity.ok("Route created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to create route.");
        }
    }

    // ✅ PUT - Update route by ID
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRoute(@PathVariable int id, @RequestBody Route route) {
        String sql = "UPDATE route SET route_no = ?, wave = ?, est_time = ?, est_distance = ?, " +
                "volume = ?, difficulty = ?, stops = ? WHERE id = ?";

        try {
            int rows = jdbcTemplate.update(sql,
                    route.getRouteNo(),
                    route.getWave(),
                    route.getEstTime(),
                    route.getEstDistance(),
                    route.getVolume(),
                    route.getDifficulty(),
                    route.getStops(),
                    id
            );
            if (rows > 0) {
                return ResponseEntity.ok("Route updated successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to update route.");
        }
    }

    // ✅ DELETE - Delete route by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable int id) {
        String sql = "DELETE FROM route WHERE id = ?";

        try {
            int rows = jdbcTemplate.update(sql, id);
            if (rows > 0) {
                return ResponseEntity.ok("Route deleted successfully.");
            } else {
                return ResponseEntity.status(404).body("Route not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to delete route.");
        }
    }

    // ✅ GET - Get all routes
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        String sql = "SELECT * FROM route";
        try {
            List<Route> routes = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Route.class));
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ GET - Get route by ID
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable int id) {
        String sql = "SELECT * FROM route WHERE id = ?";
        try {
            List<Route> routes = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Route.class), id);
            if (routes.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(routes.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}


