package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PdfReader {

    public static List<String> getBlocksFromPdf(String filePath) throws IOException {

        List<String> paragraphs = new ArrayList<>();
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            String[] splitText = text.split("\\r?\\n\\s*\\r?\\n");
            for (String paragraph : splitText) {
                paragraphs.add(paragraph);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Extracted Blocks:");
        for (String block : paragraphs) {
            System.out.println("Block:");
            System.out.println(block);
        }

        return paragraphs;
    }
}
