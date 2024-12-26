package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import model.PaySlipInput;
import utility.PaySlipProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utility.PdfDataReader;

import java.io.File;
import java.io.IOException;

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
            PaySlipProcessor paySlipProcessor = new PaySlipProcessor();
             paySlipProcessor.saveToDatabase(paySlipDetails);
        });
        return ResponseEntity.status(HttpStatus.OK).body("PaySlips processed and saved to database");
    }

}
