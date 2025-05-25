package main;

import entity.ChangePasswordRequest;
import entity.LoginRequest;
import entity.LoginResponse;
import entity.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import utility.JwtUtil;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtUtil jwtUtil;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthenticationController(JwtUtil jwtUtil, JdbcTemplate jdbcTemplate) {
        this.jwtUtil = jwtUtil;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        try {
            String sql = "SELECT password, firstName, lastName FROM users WHERE username = ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, loginRequest.getUsername());

            if (results.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            Map<String, Object> user = results.get(0);
            String dbPassword = (String) user.get("password");
            String dbFirstName = (String) user.get("firstName");
            String dbLastName = (String) user.get("lastName");

            // Replace with hashed password check in production
            if (!loginRequest.getPassword().equals(dbPassword)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            String token = jwtUtil.generateToken(loginRequest.getUsername(), dbFirstName, dbLastName);
            return ResponseEntity.ok(new LoginResponse(token, loginRequest.getUsername(), dbFirstName, dbLastName));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during login");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        try {
            String checkUserSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            Integer count = jdbcTemplate.queryForObject(checkUserSql, Integer.class, signUpRequest.getUsername());

            if (count != null && count > 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }

            String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";
            int rows = jdbcTemplate.update(insertSql, signUpRequest.getUsername(), signUpRequest.getPassword());

            if (rows > 0) {
                return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during sign-up");
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            String sql = "SELECT password FROM users WHERE username = ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, changePasswordRequest.getUsername());

            if (results.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            String dbPassword = (String) results.get(0).get("password");

            // Replace with hashed password check in production
            if (!changePasswordRequest.getOldPassword().equals(dbPassword)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Old password is incorrect");
            }

            String updateSql = "UPDATE users SET password = ? WHERE username = ?";
            int rows = jdbcTemplate.update(updateSql, changePasswordRequest.getNewPassword(), changePasswordRequest.getUsername());

            if (rows > 0) {
                return ResponseEntity.ok("Password updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update password");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during password change");
        }
    }
}
