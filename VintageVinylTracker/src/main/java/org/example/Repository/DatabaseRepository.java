/**
 * DBAccess.java:
 * alec-shell
 * 11/26/2025
 */

package org.example.Repository;


import org.example.DTO.Record;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;

public class DatabaseRepository {

    public boolean addRecordEntry(int id, String bandName, String albumName, String year,
                                        String country, String catNo, String thumbUrl, boolean isOwned, double purchasePrice,
                                        double value, String condition, Connection conn) {
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
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("SQL Exception... " + e.getMessage());
            return false;
        }
    } // addRecordEntry()

    public ArrayList<Record> searchRecordEntries(String bandName, String albumName, String year, String catNo,
                                                 String isOwned, Connection conn) {
        ArrayList<Record> resultsList = new ArrayList<>();
        try(PreparedStatement stmt = buildRecordSearchStmt(bandName, albumName, year, catNo, isOwned, conn)) {
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

    public boolean deleteRecordEntry(int id, Connection conn) {
        String deleteStmt = "DELETE FROM Vinyl WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteStmt)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("SQL Exception... " + e.getMessage());
            return false;
        }
    } // deleteRecordEntry()

    public boolean checkForUpdate(Connection conn) {
        String stmt = "SELECT last_update FROM Metadata WHERE id = 1;";
        try (PreparedStatement ps = conn.prepareStatement(stmt)) {
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) { return true; }
            Timestamp stamp = rs.getTimestamp("last_update");
            return stamp == null || stamp.toInstant().plus(24, ChronoUnit.HOURS).isBefore(Instant.now());
        } catch (SQLException e) {
            System.err.println("SQL Exception on last_update... " + e.getMessage());
            return true;
        }
    } // checkForUpdate()

    public boolean updateRecordPrice(int id, double newPrice, Connection conn) {
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

    public void updateMetaDate(Connection conn) {
        String sql = "UPDATE Metadata SET last_update = ? WHERE id = 1;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    } // updateMetaDate()

    private PreparedStatement buildRecordSearchStmt(String bandName, String albumName, String year,
                                                    String catNo, String isOwned, Connection conn) throws SQLException {
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

} // DatabaseRepository
