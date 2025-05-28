package main;

import entity.ChangePasswordRequest;
import entity.LoginRequest;
import entity.LoginResponse;
import entity.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import utility.JwtUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            System.out.println("Login attempt for ID: " + loginRequest.getId());
            String sql = "SELECT password, firstName, lastName FROM driver WHERE id = ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, loginRequest.getId());

            if (results.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            Map<String, Object> user = results.get(0);
            String dbHashedPassword = (String) user.get("password");
            String dbFirstName = (String) user.get("firstName");
            String dbLastName = (String) user.get("lastName");

            if (!BCrypt.checkpw(loginRequest.getPassword(), dbHashedPassword)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            String accessToken = jwtUtil.generateToken(loginRequest.getId(), dbFirstName, dbLastName);
            String refreshToken = UUID.randomUUID().toString();

            // Store refresh token in the database (or you could use a separate table for security)
            String updateRefreshTokenSql = "UPDATE driver SET refresh_token = ? WHERE id = ?";
            jdbcTemplate.update(updateRefreshTokenSql, refreshToken, loginRequest.getId());

            return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken, loginRequest.getId(), dbFirstName, dbLastName));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during login");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Missing refresh token");
        }

        try {
            String sql = "SELECT id, firstName, lastName FROM driver WHERE refresh_token = ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, refreshToken);

            if (results.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            Map<String, Object> user = results.get(0);
            String id = (String) user.get("id");
            String firstName = (String) user.get("firstName");
            String lastName = (String) user.get("lastName");

            String newAccessToken = jwtUtil.generateToken(id, firstName, lastName);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("accessToken", newAccessToken));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error refreshing token");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        try {
            String checkUserSql = "SELECT COUNT(*) FROM driver WHERE id = ?";
            Integer count = jdbcTemplate.queryForObject(checkUserSql, Integer.class, signUpRequest.getId());

            if (count != null && count > 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("ID already exists");
            }

            String hashedPassword = BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt());
            String insertSql = "INSERT INTO driver (id, password, firstName, lastName) VALUES (?, ?, ?, ?)";
            int rows = jdbcTemplate.update(insertSql, signUpRequest.getId(), hashedPassword, signUpRequest.getFirstName(), signUpRequest.getLastName());

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
            String sql = "SELECT password FROM driver WHERE id = ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, changePasswordRequest.getId());

            if (results.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            String dbHashedPassword = (String) results.get(0).get("password");

            if (!BCrypt.checkpw(changePasswordRequest.getOldPassword(), dbHashedPassword)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Old password is incorrect");
            }

            String newHashedPassword = BCrypt.hashpw(changePasswordRequest.getNewPassword(), BCrypt.gensalt());
            String updateSql = "UPDATE driver SET password = ? WHERE id = ?";
            int rows = jdbcTemplate.update(updateSql, newHashedPassword, changePasswordRequest.getId());

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