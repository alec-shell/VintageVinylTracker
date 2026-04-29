/**
 * DBAccess.java:
 * alec-shell
 * 11/26/2025
 */

package org.example.Repository;


import org.example.Configurable.URICollection;
import org.example.DTO.Record;
import org.example.Infrastructure.database.ConnectionFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class DatabaseRepository {
    private Connection conn;

    public DatabaseRepository(Connection conn) {
        this.conn = conn;
    } // constructor

    public final Boolean addRecordEntry(int id, String bandName, String albumName, String year,
                                        String country, String catNo, String thumbUrl, boolean isOwned, double purchasePrice,
                                        double value, String condition) {
        if (!ConnectionFactory.isValidConnection(conn)) {
            conn = ConnectionFactory.initConnection(URICollection.DB_URI);
        }
        String entryStmt = "INSERT INTO Vinyl(id, band_name, album_name, year, " +
                "country, cat_no, thumb_url, is_owned, purchase_price, value, condition) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            System.err.println("SQL Exception... " + e.getMessage());
            return false;
        }
    } // addEntry()

    public final ArrayList<Record> searchRecordEntries(
            String bandName, String albumName, String year, String catNo, String isOwned) {
        if (!ConnectionFactory.isValidConnection(conn)) {
            conn = ConnectionFactory.initConnection(URICollection.DB_URI);
        }
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
            System.err.println("SQL Exception... " + e.getMessage());
        }
        return resultsList;
    } // searchRecordEntries()

    public boolean deleteRecordEntry(int id) {
        if (!ConnectionFactory.isValidConnection(conn)) {
            conn = ConnectionFactory.initConnection(URICollection.DB_URI);
        }
        String deleteStmt = "DELETE FROM Vinyl WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteStmt)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("SQL Exception... " + e.getMessage());
            return false;
        }
    } // deleteRecordEntry()

    private PreparedStatement buildRecordSearchStmt(String bandName, String albumName, String year,
                                                    String catNo, String isOwned) throws SQLException {
        if (!ConnectionFactory.isValidConnection(conn)) {
            conn = ConnectionFactory.initConnection(URICollection.DB_URI);
        }
        // build out search query conditionally
        StringBuilder stmtSB =  new StringBuilder();
        stmtSB.append("SELECT * FROM Vinyl WHERE 1=1");
        if (bandName != null) {stmtSB.append(" AND band_name LIKE ?");}
        if (albumName != null) {stmtSB.append(" AND album_name LIKE ?");}
        if (year != null) {stmtSB.append(" AND year LIKE ?");}
        if (catNo != null) {stmtSB.append(" AND cat_no LIKE ?");}
        if (isOwned != null && !isOwned.equals("null")) {stmtSB.append(" AND is_owned = ?");}
        // fill out prepared values conditionally
        int paramIndex = 1;
        PreparedStatement ps = conn.prepareStatement(stmtSB.toString());
        if (bandName != null) {ps.setString(paramIndex++, bandName);}
        if (albumName != null) {ps.setString(paramIndex++, albumName);}
        if (year != null) {ps.setString(paramIndex++, year);}
        if (catNo != null) {ps.setString(paramIndex++, catNo);}
        if (isOwned != null && !isOwned.equals("null")) {
            if (isOwned.equals("true")) ps.setBoolean(paramIndex, true);
            else if (isOwned.equals("false")) ps.setBoolean(paramIndex, false);
        }
        return ps;
    } // buildRecordSearchStmt()

    public boolean checkForOwnedPricingUpdate() {
        if (!ConnectionFactory.isValidConnection(conn)) {
            conn = ConnectionFactory.initConnection(URICollection.DB_URI);
        }
        String stmt = "SELECT last_update FROM Metadata";
        try (PreparedStatement ps = conn.prepareStatement(stmt)) {
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {return true;}
            Date date = rs.getDate("last_update");
            return date != null && !date.equals(Date.valueOf(LocalDate.now()));
        } catch (SQLException e) {
            System.err.println("SQL Exception... " + e.getMessage());
            return true;
        }
    } // checkForUpdate()

    public boolean updateRecordPrice(int id, double newPrice) {
        if (!ConnectionFactory.isValidConnection(conn)) {
            conn = ConnectionFactory.initConnection(URICollection.DB_URI);
        }
        String updateStmt =  "UPDATE Vinyl SET value = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateStmt)) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("SQL Exception... " + e.getMessage());
            return false;
        }
    } // updateRecordPrice()

    public void updateMetaDate() {
        if (!ConnectionFactory.isValidConnection(conn)) {
            conn = ConnectionFactory.initConnection(URICollection.DB_URI);
        }
        String dateStmt = "UPDATE Metadata SET last_update = ?";
        int rowsUpdated = 0;
        try (PreparedStatement ps = conn.prepareStatement(dateStmt)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            rowsUpdated = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Exception... " + e.getMessage());
        }
        if (rowsUpdated == 0) {
            String initStmt = "INSERT INTO Metadata (last_update) VALUES (?)";
            try (PreparedStatement ps = conn.prepareStatement(initStmt)) {
                ps.setDate(1, Date.valueOf(LocalDate.now()));
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("SQL Exception... " + e.getMessage());
            }
        }
    } // updateMetaDate()

} // DatabaseRepository
