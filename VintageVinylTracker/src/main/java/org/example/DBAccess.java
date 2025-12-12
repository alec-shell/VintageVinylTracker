/**
 * DBAccess.java:
 * alec-shell
 * 11/26/2025
 */

package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                    condition VARCHAR(40) NOT NULL,
                    is_master BOOLEAN NOT NULL,
                    is_owned BOOLEAN NOT NULL)
                    """;
        String createPriceTable = """
                CREATE TABLE IF NOT EXISTS
                Price( id INT PRIMARY KEY,
                paid REAL,
                estimated_retail REAL,
                estimate_date DATE,
                FOREIGN KEY (id) REFERENCES Vinyl(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE)
                """;
        conn.prepareStatement(createVinylTable).execute();
        conn.prepareStatement(createPriceTable).execute();
    } // initTables()


    public final void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Database connection failed... " + e.getMessage());
        }
    } // closeConnection()


    public final Boolean addRecordEntry(int id, String bandName, String albumName, String year, String country, String condition, boolean isMaster, boolean isOwned) {
        String entryStmt = "INSERT INTO Vinyl(id, band_name, album_name, year, country, condition, is_master, is_owned) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(entryStmt)) {
            stmt.setInt(1, id);
            stmt.setString(2, bandName);
            stmt.setString(3, albumName);
            stmt.setString(4, year);
            stmt.setString(5, country);
            stmt.setString(6, condition);
            stmt.setBoolean(7, isMaster);
            stmt.setBoolean(8, isOwned);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("SQL Exception... " + e.getMessage());
        }
        return false;
    } // addEntry()


    public final List<Record> searchRecordEntries(String bandName, String albumName, String year, Boolean isOwned) {
        List<Record> resultsList = new ArrayList<>();
        try(PreparedStatement stmt = buildRecordSearchStmt(bandName, albumName, year, isOwned)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Record temp = new Record(rs.getInt("id"),
                        rs.getString("band_name"),
                        rs.getString("album_name"),
                        rs.getString("year"),
                        rs.getString("condition"),
                        rs.getString("country"),
                        rs.getBoolean("is_master"),
                        rs.getBoolean("is_owned")
                    );
                resultsList.add(temp);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception... " + e.getMessage());
        }
        return resultsList;
    } // searchRecordEntries()


    private PreparedStatement buildRecordSearchStmt(String bandName, String albumName, String year, Boolean isOwned) throws SQLException {
        // build out search query conditionally
        StringBuilder stmtSB =  new StringBuilder();
        stmtSB.append("SELECT * FROM Vinyl WHERE 1=1");
        if (bandName != null) {stmtSB.append(" AND band_name LIKE ?");}
        if (albumName != null) {stmtSB.append(" AND album_name LIKE ?");}
        if (year != null) {stmtSB.append(" AND year LIKE ?");}
        if (isOwned != null) {stmtSB.append(" AND is_owned = ?");}
        // fill out prepared values conditionally
        int paramIndex = 1;
        PreparedStatement ps = conn.prepareStatement(stmtSB.toString());
        if (bandName != null) {ps.setString(paramIndex++, bandName);}
        if (albumName != null) {ps.setString(paramIndex++, albumName);}
        if (year != null) {ps.setString(paramIndex++, year);}
        if (isOwned != null) {ps.setBoolean(paramIndex, isOwned);}
        return ps;
    } // buildRecordSearchStmt()


    public final void addPriceEntry(int id, double pricePaid, double estimatedRetail) throws SQLException {
        String cmd = "INSERT INTO Price VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(cmd);
        ps.setInt(1, id);
        ps.setDouble(2, pricePaid);
        ps.setDouble(3, estimatedRetail);
        ps.setDate(4, new java.sql.Date(new java.util.Date().getTime()));
        ps.execute();
    } // addPriceEntry()


    public final void updatePriceEntry(int id, Double pricePaid, Double estimatedRetail) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE Price SET");
        if (pricePaid != null) {sb.append(" paid = ?,");}
        if (estimatedRetail != null) {sb.append(" estimated_retail = ?,");}
        sb.append(" estimate_date = ? WHERE id = ?");
        int paramIndex = 1;
        PreparedStatement ps = conn.prepareStatement(sb.toString());
        if (pricePaid != null) {ps.setDouble(paramIndex++, pricePaid);}
        if (estimatedRetail != null) {ps.setDouble(paramIndex++, estimatedRetail);}
        ps.setDate(paramIndex++, new java.sql.Date(new java.util.Date().getTime()));
        ps.setInt(paramIndex, id);
        ps.execute();
    } // updatePriceEntry()


    public static void main(String[] args) {
        DBAccess dbAccess = new DBAccess();
        List<Record> results = dbAccess.searchRecordEntries("America", null, null, null);
        for (Record record : results) {
            System.out.println(record);
        }
    } // main()

} // DBAccess class
