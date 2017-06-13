package com.codecool.service;

import com.codecool.model.Entry;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;

@Slf4j
public class EntryService {

    public ArrayList<Entry> getByWordAnywhere(String word) {
        String sql = "SELECT * FROM mongolian_dictionary WHERE word OR transliteration_scientific OR transliteration_hungarian LIKE ? COLLATE NOCASE";
        return getQueryResult(sql, "%" + word + "%");
    }

    public ArrayList<Entry> getByWordWhole(String word) {
        String sql = "SELECT * FROM mongolian_dictionary WHERE word OR transliteration_scientific OR transliteration_hungarian LIKE ? COLLATE NOCASE";
        return getQueryResult(sql, word);
    }

    public ArrayList<Entry> getByWordBeginning(String word) {
        String sql = "SELECT * FROM mongolian_dictionary WHERE word OR transliteration_scientific OR transliteration_hungarian LIKE ? COLLATE NOCASE";
        return getQueryResult(sql, word + "%");
    }

    public ArrayList<Entry> getByWordEnd(String word) {
        String sql = "SELECT * FROM mongolian_dictionary WHERE word OR transliteration_scientific OR transliteration_hungarian LIKE ? COLLATE NOCASE";
        return getQueryResult(sql, "%" + word);
    }
    
    public ArrayList<Entry> getByDescription(String description) {
        String sql = "SELECT * FROM mongolian_dictionary WHERE description LIKE ? COLLATE NOCASE";
        return getQueryResult(sql, description);
    }

    public ArrayList<Entry> getRandomEntry() {
        ArrayList<Entry> entries = null;
        String sql = "SELECT * FROM mongolian_dictionary ORDER BY RANDOM() LIMIT 1";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            entries = createEntryList(resultSet);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        return entries;
    }

    private ArrayList<Entry> getQueryResult(String sql, String searchedString) {
        ArrayList<Entry> entries = null;
        PreparedStatement preparedStatement;
        Connection connection;

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, searchedString);
            ResultSet resultSet = preparedStatement.executeQuery();

            entries = createEntryList(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return entries;
    }

    private ArrayList<Entry> createEntryList(ResultSet resultSet) {
        ArrayList<Entry> entries = new ArrayList<>();

        try {
            while (resultSet.next()) {
                Entry entry = new Entry();
                entry.setWord(resultSet.getString("word"));
                entry.setDescription(resultSet.getString("description"));
                entry.setId(resultSet.getInt("id"));
                entries.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    private Connection connect() {
        String url = "jdbc:sqlite:src/main/resources/mongolian-dictionary.sqlite";
        Connection connection = null;
        try {
            log.info("trying to connect to the database: {}", url);
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            log.error("something happened while trying to connect to the database: {}", e.getMessage());
        }
        return connection;
    }
}
