package utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class PdfDataReader {

    public static ObjectNode extractPDFData(String pdfPath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultJson = objectMapper.createObjectNode();

        try (PdfReader reader = new PdfReader(pdfPath);
             PdfDocument pdfDocument = new PdfDocument(reader)) {

            LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();
            PdfCanvasProcessor processor = new PdfCanvasProcessor(strategy);
            processor.processPageContent(pdfDocument.getPage(1));

            String pageText = strategy.getResultantText();

            // Extract Driver Details and Split Name
            String driverFullName = extractData(pageText, "Driver: [\\w\\d]+ (.*?)\\n");
            if (driverFullName != null) {
                String[] nameParts = driverFullName.split(" ");
                nameParts = nameParts[0].split("-");
                resultJson.put("firstName", nameParts[0]);
                resultJson.put("lastName", nameParts.length > 1 ? nameParts[1] : "");
            }

            // Extract Driver ID (ignore 'C0')
            String driverId = extractData(pageText, "Driver: C0(\\w+)");
            resultJson.put("driverId", driverId);

            // Extract Week Number from Invoice Number
            String invoiceNumber = extractData(pageText, "Invoice No: (\\S+)");
            if (invoiceNumber != null) {
                String[] invoiceParts = invoiceNumber.split("-");
                resultJson.put("weekNumber", Integer.parseInt(invoiceParts[1])); // Second part
            }
            resultJson.put("invoiceNumber", invoiceNumber);

            // Extract From and To Dates
            resultJson.put("from", extractData(pageText, "From: (\\d{4}-\\d{2}-\\d{2})"));
            resultJson.put("to", extractData(pageText, "To: (\\d{4}-\\d{2}-\\d{2})"));

            // Extract Transaction Summary
            ArrayNode transactions = resultJson.putArray("transactions");
            Pattern transactionPattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})\\s+\\d+\\s+(\\d+)\\s+.*?\\$\\d+\\.\\d+");
            Matcher transactionMatcher = transactionPattern.matcher(pageText);
            while (transactionMatcher.find()) {
                ObjectNode transaction = objectMapper.createObjectNode();
                transaction.put("transactionDate", transactionMatcher.group(1));
                transaction.put("deliveries", Integer.parseInt(transactionMatcher.group(2))); // Renamed
                transactions.add(transaction);
            }
        }

        return resultJson;
    }

    private static String extractData(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}

