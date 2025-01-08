package main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import config.MySQLConfig;
import entity.Deliveries;
import entity.DeliveryDriver;
import entity.PaySlipInput;
import org.apache.pdfbox.pdmodel.PDDocument;
import utility.EmailSender;
import utility.PaySlipProcessor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utility.PdfDataReader;
import utility.PdfGenerator;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/payslip")
public class PaySlipController {

    @PostMapping("/upload")
    public ResponseEntity<ObjectNode> uploadPdfs(@RequestParam("files") MultipartFile[] files) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode results = objectMapper.createArrayNode();
        ArrayNode unListedDrivers = objectMapper.createArrayNode(); ;
        ArrayNode duplicatePaySlips = objectMapper.createArrayNode();

        if (files.length == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // Return error response for no files uploaded
        }

        for (MultipartFile file : files) {
            if (file.isEmpty() || !file.getContentType().equalsIgnoreCase("application/pdf")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null); // Return error response for invalid file
            }

            try (Connection conn = MySQLConfig.getConnection()){
                // Save the uploaded file temporarily
                File tempFile = File.createTempFile("uploaded-", ".pdf");
                file.transferTo(tempFile);

                // Use PdfDataReader to extract data
                ObjectNode extractedData = PdfDataReader.extractPDFData(tempFile.getAbsolutePath());
                if (extractedData.get("firstName") != null) { //checks if data reader read the file correctly

                    String sql = "SELECT email, rate_per_delivery FROM driver WHERE id = ?";

                        //checking if all drivers are listed and adding email and rate_per_delivery from database
                        String driverId = String.valueOf((extractedData.get("driverId").textValue()));
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1,driverId);
                        ResultSet rs = pstmt.executeQuery();
                        if(rs.next()) {
                            extractedData.put("email", rs.getString("email"));
                            extractedData.put("rate_per_delivery", rs.getString("rate_per_delivery"));
                        }
                        else {
                            ObjectNode unListedDriver = objectMapper.createObjectNode();
                            unListedDriver.put("driverId", driverId);
                            unListedDriver.put("firstName",String.valueOf(extractedData.get("firstName").textValue()));
                            unListedDriver.put("lastName", String.valueOf(extractedData.get("lastName").textValue()));
                            unListedDrivers.add(unListedDriver);
                        }

                        //cheking for duplicate payslip records
                        String sql2 = "select * from payslip where id = ?";
                        String payslipId = String.valueOf((extractedData.get("invoiceNumber").textValue()));
                        PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                        pstmt2.setString(1,payslipId);
                        ResultSet rs2 = pstmt2.executeQuery();
                        if(rs2.next()) {
                            ObjectNode duplicatePayslip = objectMapper.createObjectNode();
                            duplicatePayslip.put("weekNumber", (extractedData.get("weekNumber")));
                            duplicatePayslip.put("invoiceNumber", String.valueOf(extractedData.get("invoiceNumber").textValue()));
                            duplicatePaySlips.add(duplicatePayslip);
                        }
                        results.add(extractedData);
                }
                else{
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Upload a valid file");
                }

                // Delete the temp file
                tempFile.delete();
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null); // Return error response for processing failure
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


        // Return the extracted data for all uploaded files
        ObjectNode response = objectMapper.createObjectNode();
        if(results.size()==0 && duplicatePaySlips.size()==0 && duplicatePaySlips.size()==0) {
            response.put("message", "Please upload valid pdf file.");
        }
        response.put("results", results);
        response.put("unListedDrivers", unListedDrivers);
        response.put("duplicatePaySlips", duplicatePaySlips);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/process")
    public ResponseEntity<String> processPaySlips(@RequestBody PaySlipInput input) {
        input.getResults().forEach(paySlipDetails -> {

            // Create Driver object
            DeliveryDriver driver = new DeliveryDriver();
            driver.setId(paySlipDetails.getDriverId());
            driver.setFirstName(paySlipDetails.getFirstName());
            driver.setLastName(paySlipDetails.getLastName());
            driver.setEmail(paySlipDetails.getEmail());
            driver.setRatePerDelivery(paySlipDetails.getRate_per_delivery());

            // getting email and rate per delivery from database
            try (Connection conn = MySQLConfig.getConnection()) {
                String sql2 = "CALL getDriverYearToDateInfo(?)";
                PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setString(1, driver.getId());
                ResultSet rs2 = pstmt2.executeQuery();
                if (rs2.next()) {
                    paySlipDetails.setYtdEarnings(rs2.getDouble("earnings"));
                    paySlipDetails.setYtdDeliveries(rs2.getInt("deliveries"));
                }
                else {
                    return;
                }
            }
            catch (Exception err){
                System.out.println(err);
            }

            //calculating total deliveries made
            AtomicInteger totatDeliveries = new AtomicInteger();

            // Create Delivery objects
            List<Deliveries> deliveries = new ArrayList<>();
            paySlipDetails.getTransactions().forEach(transaction -> {
                Deliveries delivery = new Deliveries();
                delivery.setDate(transaction.getTransactionDate());
                delivery.setDay(getDayOfWeek(transaction.getTransactionDate()));
                delivery.setDeliveries(transaction.getDeliveries());
                totatDeliveries.addAndGet(transaction.getDeliveries());
                deliveries.add(delivery);
            });

            //calculating total payable amount
            double totalAmount = (totatDeliveries.get() * driver.getRatePerDelivery()) ;
            double adminFees = totalAmount * 0.02;
            double payableAmount = totalAmount - adminFees - paySlipDetails.getInsurance() - paySlipDetails.getDeductions() + paySlipDetails.getGasOrBonus() ;

            paySlipDetails.setPayableAmount(payableAmount);
            paySlipDetails.setTotalDeliveries(totatDeliveries.get());

            // Process and save to the database
            PaySlipProcessor paySlipProcessor = new PaySlipProcessor();
            paySlipProcessor.saveToDatabase(driver, paySlipDetails, deliveries);


            //send payslips to drivers
            PdfGenerator pdfGenerator = new PdfGenerator();
                PDDocument document = null;
                byte[] pdfBytes = new byte[0];

                try {
                    pdfBytes = pdfGenerator.generatePdfDocument(paySlipDetails,  deliveries, driver);
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

            emailSender.EmailSender(pdfBytes , driver.getEmail());
        });

        return ResponseEntity.status(HttpStatus.OK).body("PaySlips processed and saved to database");
    }

    // Helper method to determine the day of the week
    private String getDayOfWeek(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return localDate.getDayOfWeek().toString();
    }

}
