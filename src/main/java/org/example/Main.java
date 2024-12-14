package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws IOException {
        PDDocument document = new PDDocument();
        // creating page and page styling
        PDPage firstPage = new PDPage();
        PDPageContentStream contentStream = new PDPageContentStream(document, firstPage);

        document.addPage(firstPage);

        int pageWidth = (int) firstPage.getTrimBox().getWidth();
        int pageHeight= (int) firstPage.getTrimBox().getHeight();

        PDFont font = PDType1Font.HELVETICA;
        PDFont bold = PDType1Font.HELVETICA_BOLD;

        Format d_format = new SimpleDateFormat("dd/MM/yyyy");


        //basic info variables
        String name = "Lokesh Panchal";
        String week = "40";
        String driverId  = "87424";

        myTextClass header = new myTextClass(document, contentStream);



        PDImageXObject headImage= PDImageXObject.createFromFile("src/main/resources/img/logo.png", document);
        contentStream.drawImage(headImage, 0, pageHeight-110, pageWidth, 120);

        header.addSingleLineText("Driver name: "+ name, 25, pageHeight-130, font, 16, Color.BLACK);
        header.addSingleLineText("Week: "+ week , 25, pageHeight-150, font, 16, Color.BLACK);

        float textWidth = header.getTextWidth("Driver id: "+driverId, font , 16);
        header.addSingleLineText("Driver id: "+driverId, (int)(pageWidth -25-textWidth) , pageHeight-130, font, 16, Color.BLACK);

        float dateTextWidth = header.getTextWidth(d_format.format(new Date()), font, 16);

        header.addSingleLineText( d_format.format(new Date()), (int)(pageWidth -25 - dateTextWidth), pageHeight-150, font, 16, Color.BLACK);

        myTableClass myTable = new myTableClass(document, contentStream);

        int[] cellWidths = {120,100,80,130, 120 };
        myTable.setTable(cellWidths, 30 , 30, pageHeight-350);
        myTable.setTableFont(font, 16, Color.BLACK);

        Color tableHeadColor = new Color(35, 60, 230);
        Color tableBodyColor = new Color(	250, 249, 246);
        Color tableFooterColor = new Color(160, 232, 224);

        myTable.addCell("Day", tableHeadColor, true);
        myTable.addCell("Date", tableHeadColor, true);
        myTable.addCell("Delivery", tableHeadColor, true);
        myTable.addCell("price/delivery", tableHeadColor, true);
        myTable.addCell("Amount", tableHeadColor, true);

        myTable.addCell("Monday", tableBodyColor, false);
        myTable.addCell("14-Oct-24", tableBodyColor, false);
        myTable.addCell("150", tableBodyColor, false);
        myTable.addCell("1.4", tableBodyColor, false);
        myTable.addCell("200", tableBodyColor, false);


        myTable.addCell("Tuesday", tableBodyColor, false);
        myTable.addCell("15-Oct-24", tableBodyColor, false);
        myTable.addCell("150", tableBodyColor, false);
        myTable.addCell("1.4", tableBodyColor, false);
        myTable.addCell("200", tableBodyColor, false);

        myTable.addCell("Wednesday", tableBodyColor, false);
        myTable.addCell("16-Oct-24", tableBodyColor, false);
        myTable.addCell("150", tableBodyColor, false);
        myTable.addCell("1.4", tableBodyColor, false);
        myTable.addCell("200", tableBodyColor, false);

        myTable.addCell("Thursday", tableBodyColor, false);
        myTable.addCell("17-Oct-24", tableBodyColor, false);
        myTable.addCell("150", tableBodyColor, false);
        myTable.addCell("1.4", tableBodyColor, false);
        myTable.addCell("200", tableBodyColor, false);

        myTable.addCell("Friday", tableBodyColor, false);
        myTable.addCell("18-Oct-24", tableBodyColor, false);
        myTable.addCell("150", tableBodyColor, false);
        myTable.addCell("1.4", tableBodyColor, false);
        myTable.addCell("200", tableBodyColor, false);

        myTable.addCell("Saturday", tableBodyColor, false);
        myTable.addCell("19-Oct-24", tableBodyColor, false);
        myTable.addCell("150", tableBodyColor, false);
        myTable.addCell("1.4", tableBodyColor, false);
        myTable.addCell("200", tableBodyColor, false);

        myTable.addCell("Sunday", tableBodyColor, false);
        myTable.addCell("20-Oct-24", tableBodyColor, false);
        myTable.addCell("150", tableBodyColor, false);
        myTable.addCell("1.4", tableBodyColor, false);
        myTable.addCell("200", tableBodyColor, false);

        myTable.addCell("Total ", tableFooterColor, false);
        myTable.addCell("", tableFooterColor, false);
        myTable.addCell("", tableFooterColor, false);
        myTable.addCell("", tableFooterColor, false);
        myTable.addCell("1400", tableFooterColor, false);

        myTableClass summaryTable = new myTableClass(document, contentStream);

        int [] cellWidth = {200, 200};

        summaryTable.setTable(cellWidth, 30 , 30, pageHeight-650);
        summaryTable.setTableFont(bold, 16, Color.BLACK);

        summaryTable.addCell("Package Delivered", tableHeadColor, false);
        summaryTable.addCell("1050", tableHeadColor, false);

        summaryTable.addCell("Sub Total", tableHeadColor, false);
        summaryTable.addCell("1050", tableHeadColor, false);

        summaryTable.addCell("Admin fees", tableHeadColor, false);
        summaryTable.addCell("1050", tableHeadColor, false);

        summaryTable.addCell("Gas or Bonus", tableHeadColor, false);
        summaryTable.addCell("1050", tableHeadColor, false);

        summaryTable.addCell("Insurance", tableHeadColor, false);
        summaryTable.addCell("1050", tableHeadColor, false);

        summaryTable.addCell("Any other Deductions", tableHeadColor, false);
        summaryTable.addCell("1050", tableHeadColor, false);

        summaryTable.addCell("Payable Amount", tableHeadColor, false);
        summaryTable.addCell("1050", tableHeadColor, false);

        contentStream.close();
        document.save("C:\\Users\\lokes\\OneDrive\\Documents\\invoice.pdf");
        document.close();

        System.out.println("Pdf created");


    }


    private static class myTextClass{
        PDDocument document;
        PDPageContentStream contentStream;

        public myTextClass(PDDocument document, PDPageContentStream contentStream){
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

        void addMultiLineText(String [] textArray, float leading, int xPosition, int yPosition, PDFont font, float fontSize, Color color) throws IOException {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setNonStrokingColor(color);
            contentStream.setLeading(leading);
            contentStream.newLineAtOffset(xPosition, yPosition);

            for( String text : textArray)
            {
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

    private static class myTableClass{
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

        public myTableClass(PDDocument document, PDPageContentStream contentStream){

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


            if(colPosition == 4 || colPosition == 2){
                float fontWidth = font.getStringWidth(text)/1000 * fontSize;
                contentStream.newLineAtOffset(xPosition + colWidths[colPosition] -20 -fontWidth, yPosition + 10);;
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

