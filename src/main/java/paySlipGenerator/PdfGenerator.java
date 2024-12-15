package paySlipGenerator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PdfGenerator {
    private List<Deliveries> deliveries;
    private PaySlip paySlip;

    public PDDocument generatePdfDocument(PaySlip paySlip,List<Deliveries> deliveries) throws IOException {
        PDDocument document = new PDDocument();
        PDPage firstPage = new PDPage();
        document.addPage(firstPage);

        PDPageContentStream contentStream = new PDPageContentStream(document, firstPage);

        int pageWidth = (int) firstPage.getTrimBox().getWidth();
        int pageHeight= (int) firstPage.getTrimBox().getHeight();

        PDFont font = PDType1Font.HELVETICA;
        PDFont bold = PDType1Font.HELVETICA_BOLD;

        Format d_format = new SimpleDateFormat("dd/MM/yyyy");

        // logo
        PDImageXObject headImage= PDImageXObject.createFromFile("src/main/resources/img/logo.png", document);
        contentStream.drawImage(headImage, 0, pageHeight-110, pageWidth, 120);

        //basic info variables
        String name = paySlip.getDriverName();
        int week = paySlip.getWeekNumber();
        String driverId  = paySlip.getDriverId();

        // driver info and date
        myTextClass header = new myTextClass(document, contentStream);

        header.addSingleLineText("Driver name: "+ name, 25, pageHeight-130, font, 16, Color.BLACK);
        header.addSingleLineText("Week: "+ week , 25, pageHeight-150, font, 16, Color.BLACK);

        float textWidth = header.getTextWidth("Driver id: "+driverId, font , 16);
        header.addSingleLineText("Driver id: "+driverId, (int)(pageWidth -25-textWidth) , pageHeight-130, font, 16, Color.BLACK);

        float dateTextWidth = header.getTextWidth(d_format.format(new Date()), font, 16);
        header.addSingleLineText(d_format.format(new Date()), (int)(pageWidth -25 - dateTextWidth), pageHeight-150, font, 16, Color.BLACK);

        //pay details
        myTableClass payDetails = new myTableClass(document, contentStream);

        int[] cellWidths = {120,100,80,130, 120 };
        payDetails.setTable(cellWidths, 25 , 30, pageHeight-200);
        payDetails.setTableFont(font, 14, Color.BLACK);

        Color tableHeadColor = new Color(35, 60, 230);
        Color tableBodyColor = new Color(250, 249, 246);
        Color tableFooterColor = new Color(160, 232, 224);

        payDetails.addCell("Day", tableHeadColor, true);
        payDetails.addCell("Date", tableHeadColor, true);
        payDetails.addCell("Delivery", tableHeadColor, true);
        payDetails.addCell("price/delivery", tableHeadColor, true);
        payDetails.addCell("Amount", tableHeadColor, true);

        double total = 0;
        int totalDeliveries = 0;


        // Example rows
        for (Deliveries delivery : deliveries) {
            payDetails.addCell(delivery.getDay(), tableBodyColor, false);
            payDetails.addCell(delivery.getDate(), tableBodyColor, false);
            payDetails.addCell(String.valueOf(delivery.getDeliveries()), tableBodyColor, false);
            payDetails.addCell( String.valueOf(delivery.getPricePerDelivery()), tableBodyColor, false);
            payDetails.addCell(String.valueOf(delivery.getAmount()), tableBodyColor, false);
            total += delivery.getAmount();
            totalDeliveries += delivery.getDeliveries();
        }

        payDetails.addCell("Total", tableFooterColor, false);
        payDetails.addCell("", tableFooterColor, false);
        payDetails.addCell("", tableFooterColor, false);
        payDetails.addCell("", tableFooterColor, false);
        payDetails.addCell(String.valueOf(total), tableFooterColor, false);

        myTableClass summaryTable = new myTableClass(document, contentStream);

        int [] cellWidth = {160, 60};

        Color summaryTableColor = Color.WHITE;

        summaryTable.setTable(cellWidth, 25 , 30, pageHeight-450);
        summaryTable.setTableFont(font, 14, Color.BLACK);

        summaryTable.addCell("Packages Delivered", summaryTableColor, false);
        summaryTable.addCell( String.valueOf(totalDeliveries), summaryTableColor, false);

        summaryTable.addCell("Sub Total", summaryTableColor, false);
        summaryTable.addCell(String.valueOf(total), summaryTableColor, false);

        summaryTable.addCell("Admin fees", summaryTableColor, false);
        summaryTable.addCell("1050", summaryTableColor, false);

        summaryTable.addCell("Gas or Bonus", summaryTableColor, false);
        summaryTable.addCell("1050", summaryTableColor, false);

        summaryTable.addCell("Insurance", summaryTableColor, false);
        summaryTable.addCell("1050", summaryTableColor, false);

        summaryTable.addCell("Any other Deductions", summaryTableColor, false);
        summaryTable.addCell("1050", summaryTableColor, false);

        summaryTable.addCell("Payable Amount", summaryTableColor, false);
        summaryTable.addCell("1050", summaryTableColor, false);

        contentStream.close();

        // Return the PDDocument so that the caller can decide where and how to save/close it.
        return document;
    }

    private static class myTextClass {
        PDDocument document;
        PDPageContentStream contentStream;

        public myTextClass(PDDocument document, PDPageContentStream contentStream) {
            this.document = document;
            this.contentStream = contentStream;
        }

        void addSingleLineText(String text, int xPosition, int yPosition, PDFont font, float fontSize, Color color) throws IOException {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setNonStrokingColor(color);
            contentStream.newLineAtOffset(xPosition, yPosition);
            contentStream.showText(text);
            contentStream.endText();
            contentStream.moveTo(0, 0);
        }

        void addMultiLineText(String[] textArray, float leading, int xPosition, int yPosition, PDFont font, float fontSize, Color color) throws IOException {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setNonStrokingColor(color);
            contentStream.setLeading(leading);
            contentStream.newLineAtOffset(xPosition, yPosition);

            for (String text : textArray) {
                contentStream.showText(text);
                contentStream.newLine();
            }

            contentStream.endText();
            contentStream.moveTo(0, 0);
        }

        float getTextWidth(String text, PDFont font, float fontSize) throws IOException {
            return font.getStringWidth(text)/1000 * fontSize;
        }
    }

    private static class myTableClass {
        PDDocument document;
        PDPageContentStream contentStream;
        private int[] colWidths;
        private int cellHeights;
        private int yPosition;
        private int xPosition = 30;
        private int colPosition =0 ;
        private int xInitialPosition;
        private float fontSize;
        private PDFont font;
        private Color fontColor;

        public myTableClass(PDDocument document, PDPageContentStream contentStream) {
            this.document = document;
            this.contentStream = contentStream;
        }

        void setTable(int[] colWidths, int cellHeights, int xPosition,  int yPosition){
            this.colWidths = colWidths;
            this.cellHeights = cellHeights;
            this.yPosition = yPosition;
            this.xInitialPosition = xPosition;
        }

        void setTableFont(PDFont font, float fontSize, Color fontColor) throws IOException {
            this.fontSize = fontSize;
            this.fontColor = fontColor;
            this.font = font;
            contentStream.setFont(font, fontSize);
        }

        void addCell(String text, Color fillColor, boolean isHeader ) throws IOException {
            contentStream.setStrokingColor(1f);
            contentStream.addRect(xPosition, yPosition, colWidths[colPosition], cellHeights);

            if(fillColor!= null){
                contentStream.setNonStrokingColor(fillColor);
            }

            if(fillColor == null){
                contentStream.stroke();
            }
            else {
                contentStream.fillAndStroke();
            }

            if (isHeader){
                contentStream.setNonStrokingColor(Color.WHITE);
            }
            else{
                contentStream.setNonStrokingColor(fontColor);
            }

            contentStream.beginText();

            if(colPosition == 4 || colPosition == 2 || colPosition == 1){
                float fontWidth = font.getStringWidth(text)/1000 * fontSize;
                contentStream.newLineAtOffset(xPosition + colWidths[colPosition] -20 -fontWidth, yPosition + 10);
            }
            else {
                contentStream.newLineAtOffset(xPosition +20 , yPosition+ 10);
            }

            contentStream.showText(text);
            contentStream.endText();

            xPosition = colWidths[colPosition] + xPosition ;
            colPosition ++;

            if (colPosition == colWidths.length){
                colPosition = 0;
                xPosition = xInitialPosition;
                yPosition -= cellHeights;
            }

        }
    }
}
