package utility;

import config.MySQLConfig;
import model.Deliveries;
import model.DeliveryDriver;
import model.PaySlip;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaySlipProcessor {

    public void saveToDatabase(DeliveryDriver driver, PaySlip paySlip, List<Deliveries> deliveries) {
        try (Connection conn = MySQLConfig.getConnection()) {
            conn.setAutoCommit(false); // Enable transaction

            String checkDriverSql = "SELECT COUNT(*) FROM driver WHERE id = ?";
            try (PreparedStatement checkDriverStmt = conn.prepareStatement(checkDriverSql)) {
                checkDriverStmt.setString(1, driver.getId());
                try (ResultSet rs = checkDriverStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        throw new RuntimeException("Driver with ID " + driver.getId() + " does not exist. Please create the driver first.");
                    }
                }
            }

            // Save PaySlip to `payslip` table
            String paySlipSql = "INSERT INTO payslip (id, weekNumber, driverId, payableAmount, totalDeliveries, gasOrBonus, insurance, deductions) VALUES (?, ?, ?, ?, ? ,? ,?, ?) "+ "ON DUPLICATE KEY UPDATE weekNumber = VALUES(weekNumber), driverId = VALUES(driverId)";
            try (PreparedStatement paySlipStmt = conn.prepareStatement(paySlipSql)) {
                paySlipStmt.setString(1, paySlip.getId());
                paySlipStmt.setInt(2, paySlip.getWeekNumber());
                paySlipStmt.setString(3, driver.getId());
                paySlipStmt.setDouble(4, paySlip.getPayableAmount());
                paySlipStmt.setInt(5, paySlip.getTotalDeliveries());
                paySlipStmt.setDouble(6, paySlip.getGasOrBonus());
                paySlipStmt.setDouble(7, paySlip.getInsurance());
                paySlipStmt.setDouble(8, paySlip.getDeductions());
                paySlipStmt.executeUpdate();
            }

            // Save Deliveries to `deliveries` table
            String deliveriesSql = "INSERT INTO deliveries (paySlipId, day, date, deliveries) VALUES (?, ?, ?, ?)";
            try (PreparedStatement deliveriesStmt = conn.prepareStatement(deliveriesSql)) {
                for (Deliveries delivery : deliveries) {
                    deliveriesStmt.setString(1, paySlip.getId());
                    deliveriesStmt.setString(2, delivery.getDay());
                    deliveriesStmt.setString(3, delivery.getDate());
                    deliveriesStmt.setInt(4, delivery.getDeliveries());
                    deliveriesStmt.addBatch();
                }
                deliveriesStmt.executeBatch();
            }

            conn.commit(); // Commit transaction
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
