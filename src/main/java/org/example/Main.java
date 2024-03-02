package org.example;

import org.json.JSONArray;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\User\\OneDrive\\Рабочий стол\\text.pdf";
        try {
            // Получение списка абзацев из PDF
            List<String> paragraphs = PdfReader.getBlocksFromPdf(filePath);

            // Запись абзацев и их нормализованных векторов в базу данных
            for (String paragraph : paragraphs) {
                // Получение векторов для каждого абзаца
                JSONArray paragraphEmbeddings = TextVectorizer.getEmbeddings(paragraph);

                // Нормализация вектора
                JSONArray normalizedEmbeddings = VectorNormalizer.normalize(paragraphEmbeddings);

                // Запись абзаца и его нормализованного вектора в базу данных
                DatabaseWriter.insertParagraph(paragraph, normalizedEmbeddings);


            }
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }
}
