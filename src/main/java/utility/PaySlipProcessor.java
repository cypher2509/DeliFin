package utility;

import config.MySQLConfig;
import model.PaySlipDetails;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

@Service
public class PaySlipProcessor {

    public void saveToDatabase(PaySlipDetails paySlipDetails) {
        try (Connection conn = MySQLConfig.getConnection()) {

            // Save Driver to `driver` table
            String driverSql = "INSERT INTO driver (id, firstName, lastName) VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE firstName = VALUES(firstName), lastName = VALUES(lastName)";
            try (PreparedStatement driverStmt = conn.prepareStatement(driverSql)) {
                driverStmt.setString(1, paySlipDetails.getDriverId());
                driverStmt.setString(2, paySlipDetails.getFirstName());
                driverStmt.setString(3, paySlipDetails.getLastName());
                driverStmt.executeUpdate();
            }

            // Save PaySlip to `payslip` table
            String paySlipSql = "INSERT INTO payslip (id, weekNumber, driverId) VALUES (?, ?, ?) ";
            try (PreparedStatement paySlipStmt = conn.prepareStatement(paySlipSql)) {
                paySlipStmt.setString(1, paySlipDetails.getInvoiceNumber());
                paySlipStmt.setInt(2, paySlipDetails.getWeekNumber());
                paySlipStmt.setString(3, paySlipDetails.getDriverId());
                paySlipStmt.executeUpdate();
            }

            // Save Transactions to `deliveries` table
            String deliveriesSql = "INSERT INTO deliveries (paySlipId, day, date, deliveries) VALUES (?, ?, ?, ?)";
            try (PreparedStatement deliveriesStmt = conn.prepareStatement(deliveriesSql)) {
                for (var transaction : paySlipDetails.getTransactions()) {
                    deliveriesStmt.setString(1, paySlipDetails.getInvoiceNumber());
                    deliveriesStmt.setString(2, getDayOfWeek(transaction.getTransactionDate()));
                    deliveriesStmt.setString(3, transaction.getTransactionDate());
                    deliveriesStmt.setInt(4, transaction.getDeliveries());
                    deliveriesStmt.addBatch();
                }
                deliveriesStmt.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving to database", e);
        }
    }

    private String getDayOfWeek(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return localDate.getDayOfWeek().toString();
    }
}
