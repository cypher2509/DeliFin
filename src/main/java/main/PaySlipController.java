package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import config.MySQLConfig;
import model.Deliveries;
import model.DeliveryDriver;
import model.PaySlipInput;
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

        if (files.length == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // Return error response for no files uploaded
        }

        for (MultipartFile file : files) {
            if (file.isEmpty() || !file.getContentType().equalsIgnoreCase("application/pdf")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null); // Return error response for invalid file
            }

            try {
                // Save the uploaded file temporarily
                File tempFile = File.createTempFile("uploaded-", ".pdf");
                file.transferTo(tempFile);

                // Use PdfDataReader to extract data
                ObjectNode extractedData = PdfDataReader.extractPDFData(tempFile.getAbsolutePath());
                results.add(extractedData);

                // Delete the temp file
                tempFile.delete();
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null); // Return error response for processing failure
            }
        }

        // Return the extracted data for all uploaded files
        ObjectNode response = objectMapper.createObjectNode();
        response.set("results", results);
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
            // getting email and rate per delivery from database
            try (Connection conn = MySQLConfig.getConnection()) {
                String sql = "SELECT email, rate_per_delivery FROM driver WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, driver.getId());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    driver.setEmail(rs.getString("email"));
                    driver.setRatePerDelivery(rs.getFloat("rate_per_delivery"));
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
