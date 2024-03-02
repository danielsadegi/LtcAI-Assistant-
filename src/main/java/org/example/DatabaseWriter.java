package org.example;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseWriter {
    private static final String URL = "URL";
    private static final String USER = "postgres";
    private static final String PASSWORD = "PASSWORD";

    public static void insertParagraph(String paragraph, JSONArray paragraphEmbeddings) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            createTable(connection);
            String sql = "INSERT INTO mydocuments (text, embedding) VALUES (?, ?::JSONB)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, paragraph);
                statement.setString(2, paragraphEmbeddings.toString());
                statement.executeUpdate();
            }
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS mydocuments (id bigserial PRIMARY KEY, text TEXT, embedding JSONB)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }
}

