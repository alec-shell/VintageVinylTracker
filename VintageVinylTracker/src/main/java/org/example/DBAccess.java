/**
 * DBAccess.java:
 * alec-shell
 * 11/26/2025
 */

package org.example;

import java.sql.*;
import java.util.ArrayList;

public class DBAccess {
    private final Connection conn;

    public DBAccess() {
        String url =  "jdbc:sqlite:VintageVinyl.db";
        try {
            conn = DriverManager.getConnection(url);
            initTables();
        } catch (SQLException e) {
            System.out.println("Connection failed... " + e.getMessage());
            throw new RuntimeException(e);
        }
    } // constructor

    private void initTables() throws SQLException {
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
        conn.prepareStatement(createVinylTable).execute();
    } // initTables()

    public final Boolean addRecordEntry(int id, String bandName, String albumName, String year,
                                        String country, String catNo, String thumbUrl, boolean isOwned, double purchasePrice,
                                        double value, String condition) {
        String entryStmt = "INSERT INTO Vinyl(id, band_name, album_name, year, country, cat_no, thumb_url, is_owned, purchase_price, value, condition) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(entryStmt)) {
            stmt.setInt(1, id);
            stmt.setString(2, bandName);
            stmt.setString(3, albumName);
            stmt.setString(4, year);
            stmt.setString(5, country);
            stmt.setString(6, catNo);
            stmt.setString(7, thumbUrl);
            stmt.setBoolean(8, isOwned);
            stmt.setDouble(9, purchasePrice);
            stmt.setDouble(10, value);
            stmt.setString(11, condition);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("SQL Exception... " + e.getMessage());
            return false;
        }
    } // addEntry()

    public final ArrayList<Record> searchRecordEntries(String bandName, String albumName, String year, String catNo, Boolean isOwned) {
        ArrayList<Record> resultsList = new ArrayList<>();
        try(PreparedStatement stmt = buildRecordSearchStmt(bandName, albumName, year, catNo, isOwned)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Record temp = new Record(rs.getInt("id"),
                        rs.getString("band_name"),
                        rs.getString("album_name"),
                        rs.getString("year"),
                        rs.getString("country"),
                        rs.getString("cat_no"),
                        rs.getString("thumb_url"),
                        rs.getBoolean("is_owned"),
                        rs.getDouble("purchase_price"),
                        rs.getDouble("value"),
                        rs.getString("condition")
                    );
                resultsList.add(temp);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception... " + e.getMessage());
        }
        return resultsList;
    } // searchRecordEntries()

    public boolean deleteRecordEntry(int id) {
        try {
            String deleteStmt = "DELETE FROM Vinyl WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteStmt);
            stmt.setInt(1, id);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("SQL Exception... " + e.getMessage());
            return false;
        }
    } // deleteRecordEntry()

    private PreparedStatement buildRecordSearchStmt(String bandName, String albumName, String year, String catNo, Boolean isOwned) throws SQLException {
        // build out search query conditionally
        StringBuilder stmtSB =  new StringBuilder();
        stmtSB.append("SELECT * FROM Vinyl WHERE 1=1");
        if (bandName != null) {stmtSB.append(" AND band_name LIKE ?");}
        if (albumName != null) {stmtSB.append(" AND album_name LIKE ?");}
        if (year != null) {stmtSB.append(" AND year LIKE ?");}
        if (catNo != null) {stmtSB.append(" AND cat_no LIKE ?");}
        if (isOwned != null) {stmtSB.append(" AND is_owned = ?");}
        // fill out prepared values conditionally
        int paramIndex = 1;
        PreparedStatement ps = conn.prepareStatement(stmtSB.toString());
        if (bandName != null) {ps.setString(paramIndex++, bandName);}
        if (albumName != null) {ps.setString(paramIndex++, albumName);}
        if (year != null) {ps.setString(paramIndex++, year);}
        if (catNo != null) {ps.setString(paramIndex++, catNo);}
        if (isOwned != null) {ps.setBoolean(paramIndex, isOwned);}
        return ps;
    } // buildRecordSearchStmt()

} // DBAccess class
