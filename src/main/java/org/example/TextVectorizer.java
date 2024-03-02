package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TextVectorizer {
    private static final String OPENAI_API_KEY = "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";

    public static JSONArray getEmbeddings(String text) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("input", text);
        requestBodyJson.put("model", "text-embedding-3-small");
        String requestBody = requestBodyJson.toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/embeddings"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonResponse = new JSONObject(response.body());
        JSONArray embeddings = jsonResponse.getJSONArray("data").getJSONObject(0).getJSONArray("embedding");
        return embeddings;
    }
}
