//package main;
//
//import org.apache.pdfbox.pdmodel.PDDocument;
//import utility.EmailSender;
//import utility.PdfGenerator;
//import model.PaySlip;
//import model.Deliveries;
//
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Main {
//        public static void main(String[] args) {
//            try (Connection conn = MySQLConfig.getConnection()) {
//                // Get PaySlip data
//                String sql = "SELECT * FROM PaySlip WHERE id = ?";
//                PreparedStatement pstmt = conn.prepareStatement(sql);
//                pstmt.setInt(1, 3); // Get PaySlip with ID = 1
//                ResultSet rs = pstmt.executeQuery();
//
//                PaySlip paySlip = null;
//                if (rs.next()) {
//                    paySlip = new PaySlip(); // Create an empty PaySlip object
//
//                    // Call setter methods to populate fields
//                    paySlip.setId(rs.getString("id"));
//                    paySlip.setDriverName(rs.getString("driverName"));
//                    paySlip.setWeekNumber(rs.getInt("weekNumber"));
//                    paySlip.setDriverId(rs.getString("driverId"));
//                    paySlip.setTotalAmount(rs.getDouble("totalAmount"));
//                }
//
//                // Get Deliveries data for the PaySlip
//                List<Deliveries> deliveries = new ArrayList<>();
//                sql = "SELECT * FROM Deliveries WHERE paySlipId = ?";
//                pstmt = conn.prepareStatement(sql);
//                assert paySlip != null;
//                pstmt.setString(1, paySlip.getId());
//                rs = pstmt.executeQuery();
//
////                while (rs.next()) {
////                    deliveries.add(new Deliveries(
////                            rs.getInt("id"),
////                            rs.getString("paySlipId"),
////                            rs.getString("day"),
////                            rs.getString("date"),
////                            rs.getInt("deliveries"),
////                            rs.getDouble("pricePerDelivery"),
////                            rs.getDouble("amount")
////                    ));
////                }
//
//                PdfGenerator pdfGenerator = new PdfGenerator();
//                PDDocument document = null;
//                byte[] pdfBytes = new byte[0];
//
//                try {
//                    pdfBytes = pdfGenerator.generatePdfDocument(paySlip, deliveries);
//                    System.out.println("PDF generated successfully!");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (document != null) {
//                        try {
//                            document.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                System.out.println(paySlip.getDriverId());
//
//                String email = "";
//                sql = "SELECT email FROM driver WHERE id = ?";
//                pstmt = conn.prepareStatement(sql);
//                pstmt.setString(1, paySlip.getDriverId());
//                rs = pstmt.executeQuery();
//                if (rs.next()) {
//                    email = rs.getString("email");
//                }
//
//                System.out.println(email);
//
//                EmailSender emailSender = new EmailSender();
//
//                emailSender.EmailSender(pdfBytes , email);
//
//                // Print PaySlip
//                System.out.println("Driver Name: " + paySlip.getDriverName());
//                System.out.println("Total Amount: " + paySlip.getTotalAmount());
//                System.out.println("Deliveries: ");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//}
