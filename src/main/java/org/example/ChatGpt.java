package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.Scanner;

public class ChatGpt {
    private static final String DATABASE_URL = "DATA_BASE_URL";
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "PASSWORD";
    private static final String OPENAI_API_KEY = "API-KEY";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();
            if ("bye".equalsIgnoreCase(userInput)) {
                break;
            }

            try {
                SearchResult closestText = findClosestText(userInput);

                if (closestText != null) {
                    System.out.println("Closest text in database: " + closestText.getText());
                    System.out.println("Similarity: " + closestText.getSimilarity());
                    String aiResponse = communicate(userInput, closestText.getText());
                    System.out.println("AI: " + aiResponse);
                } else {
                    System.out.println("Text not found in the database.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        scanner.close();
    }

    private static SearchResult findClosestText(String userText) throws SQLException, IOException, InterruptedException {
        SearchResult closestText = new SearchResult(null, Double.MIN_VALUE);
        JSONArray userEmbeddings = TextVectorizer.getEmbeddings(userText);

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            String sql = "SELECT id, text, embedding FROM mydocuments";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String text = resultSet.getString("text");
                JSONArray embedding = new JSONArray(resultSet.getString("embedding"));
                double similarity = CosineSimilarityCalculator.cosineSimilarity(userEmbeddings, embedding);

                if (similarity > closestText.getSimilarity()) {
                    closestText = new SearchResult(text, similarity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return closestText;
    }

    public static String communicate(String userQuestion, String additionalInfo) throws Exception {
        JSONArray userVector = getEmbeddings(userQuestion + " " + additionalInfo);

        HttpClient httpClient = HttpClient.newHttpClient();

        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("model", "gpt-3.5-turbo");

        // Создаем массив сообщений
        JSONArray messagesArray = new JSONArray();

        // Добавляем сообщение пользователя
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", userQuestion);
        messagesArray.put(userMessage);

        // Добавляем сообщение ассистента с дополнительной информацией
        JSONObject assistantMessage = new JSONObject();
        assistantMessage.put("role", "assistant");
        assistantMessage.put("content", additionalInfo);
        messagesArray.put(assistantMessage);

        requestBodyJson.put("messages", messagesArray);

        requestBodyJson.put("temperature", 0.7);

        String requestBody = requestBodyJson.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonResponse = new JSONObject(response.body());

        String aiResponse = extractAiResponse(jsonResponse);

        return aiResponse;
    }




    private static String extractAiResponse(JSONObject jsonResponse) {
        System.out.println("JSON response from OpenAI API: " + jsonResponse.toString());
        JSONArray choices = jsonResponse.getJSONArray("choices");
        if (choices != null && choices.length() > 0) {
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            return message.getString("content");
        }
        return "No response from AI";
    }



    private static JSONArray getEmbeddings(String text) throws Exception {
        return TextVectorizer.getEmbeddings(text);
    }

    private static class SearchResult {
        private final String text;
        private final double similarity;

        public SearchResult(String text, double similarity) {
            this.text = text;
            this.similarity = similarity;
        }

        public String getText() {
            return text;
        }

        public double getSimilarity() {
            return similarity;
        }
    }

    // Метод для выполнения запроса к базе данных и извлечения информации
    private static JSONArray executeDatabaseQuery(String query) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            JSONArray results = new JSONArray();
            while (resultSet.next()) {
                JSONObject result = new JSONObject();
                result.put("id", resultSet.getInt("id"));
                result.put("text", resultSet.getString("text"));
                results.put(result);
            }
            return results;
        }
    }
}
