package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class MySQLConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/delifin?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "L0kesh@123";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to MySQL database successfully.");
            return connection;
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to DB: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
