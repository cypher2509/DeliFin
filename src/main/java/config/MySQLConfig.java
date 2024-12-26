package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConfig {
    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/delifin";
    private static final String USER = "root";
    private static final String PASSWORD = "L0kesh@123";

    /**
     * Establishes and returns a connection to the MySQL database.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL driver (optional in recent versions)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Connected to MySQL database successfully.");
        return connection;
    }

}
