package org.example.Infrastructure.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initTables(Connection conn) {
        String createVinylTable = """
                    CREATE TABLE IF NOT EXISTS
                    Vinyl(id INT PRIMARY KEY,
                    band_name VARCHAR(40) NOT NULL,
                    album_name VARCHAR(40) NOT NULL,
                    year VARCHAR(4) NOT NULL,
                    country VARCHAR(20) NOT NULL,
                    cat_no VARCHAR(40) NOT NULL,
                    thumb_url VARCHAR(200) NOT NULL,
                    is_owned BOOLEAN NOT NULL,
                    purchase_price REAL NOT NULL,
                    value REAL NOT NULL,
                    condition VARCHAR(40) NOT NULL)
                    """;
        try (PreparedStatement statement = conn.prepareStatement(createVinylTable)) {
            statement.execute();
        } catch (SQLException e) {
            System.err.println("Failed to initialize collection table: " + e.getMessage());
        }
        String createMetadataTable = """
                CREATE TABLE IF NOT EXISTS
                Metadata(last_update DATE NOT NULL)
                """;
        try (PreparedStatement statement = conn.prepareStatement(createMetadataTable)) {
            statement.execute();
        } catch (SQLException e) {
            System.err.println("Failed to initialize metadata table: " + e.getMessage());
        }
    } // initTables()

} // DatabaseInitializer
