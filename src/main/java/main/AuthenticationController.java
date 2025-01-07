package main;

import config.MySQLConfig;
import entity.ChangePasswordRequest;
import entity.LoginRequest;
import entity.LoginResponse;
import entity.SignUpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utility.JwtUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtUtil jwtUtil;

    public AuthenticationController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "SELECT password, firstName, lastName FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, loginRequest.getUsername());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                String dbPassword = rs.getString("password");
                String dbFirstName = rs.getString("firstName");
                String dbLastName = rs.getString("lastName");

                System.out.println(dbPassword + dbFirstName + dbLastName);

                // Replace with password hashing check for production
                if (!loginRequest.getPassword().equals(dbPassword)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
                }

                String token = jwtUtil.generateToken(loginRequest.getUsername(), dbFirstName, dbLastName);
                return ResponseEntity.ok(new LoginResponse(token, loginRequest.getUsername(), dbFirstName,dbLastName ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during login");
        }
    }
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        try (Connection conn = MySQLConfig.getConnection()) {
            // Check if the username already exists
            String checkUserSql = "SELECT username FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkUserSql);
            checkStmt.setString(1, signUpRequest.getUsername());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }

            // Insert the new user into the database
            String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, signUpRequest.getUsername());
            insertStmt.setString(2, signUpRequest.getPassword()); // Replace with hashed password in production
            int rowsAffected = insertStmt.executeUpdate();

            if (rowsAffected > 0) {
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
        try (Connection conn = MySQLConfig.getConnection()) {
            String sql = "SELECT password FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, changePasswordRequest.getUsername());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                // Replace with password hashing check for production
                if (!changePasswordRequest.getOldPassword().equals(dbPassword)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Old password is incorrect");
                }

                String updateSql = "UPDATE users SET password = ? WHERE username = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, changePasswordRequest.getNewPassword()); // Replace with hashed password in production
                updateStmt.setString(2, changePasswordRequest.getUsername());

                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    return ResponseEntity.ok("Password updated successfully");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update password");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during password change");
        }
    }
}
