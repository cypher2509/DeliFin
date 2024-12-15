package app;

import org.apache.pdfbox.pdmodel.PDDocument;
import paySlipGenerator.EmailSender;
import paySlipGenerator.PdfGenerator;
import config.MySQLConfig;
import paySlipGenerator.PaySlip;
import paySlipGenerator.Deliveries;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Main {
        public static void main(String[] args) {
            try (Connection conn = MySQLConfig.getConnection()) {
                // Get PaySlip data
                String sql = "SELECT * FROM PaySlip WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, 1); // Get PaySlip with ID = 1
                ResultSet rs = pstmt.executeQuery();

                PaySlip paySlip = null;
                if (rs.next()) {
                    paySlip = new PaySlip(
                            rs.getInt("id"),
                            rs.getString("driverName"),
                            rs.getInt("weekNumber"),
                            rs.getString("driverId"),
                            rs.getDouble("totalAmount")
                    );
                }

                // Get Deliveries data for the PaySlip
                List<Deliveries> deliveries = new ArrayList<>();
                sql = "SELECT * FROM Deliveries WHERE paySlipId = ?";
                pstmt = conn.prepareStatement(sql);
                assert paySlip != null;
                pstmt.setInt(1, paySlip.getId());
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    deliveries.add(new Deliveries(
                            rs.getInt("id"),
                            rs.getInt("paySlipId"),
                            rs.getString("day"),
                            rs.getString("date"),
                            rs.getInt("deliveries"),
                            rs.getDouble("pricePerDelivery"),
                            rs.getDouble("amount")
                    ));
                }

                paySlip.setDeliveries(deliveries);

                PdfGenerator pdfGenerator = new PdfGenerator();
                PDDocument document = null;
                byte[] pdfBytes = new byte[0];

                try {
                    pdfBytes = pdfGenerator.generatePdfDocument(paySlip, deliveries);
                    System.out.println("PDF generated successfully!");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (document != null) {
                        try {
                            document.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                EmailSender emailSender = new EmailSender();

                emailSender.EmailSender(pdfBytes);

                // Print PaySlip
                System.out.println("Driver Name: " + paySlip.getDriverName());
                System.out.println("Total Amount: " + paySlip.getTotalAmount());
                System.out.println("Deliveries: ");
                for (Deliveries delivery : paySlip.getDeliveries()) {
                    System.out.println(delivery.getDay() +" : "+delivery.getDate() + ": " + delivery.getAmount());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
