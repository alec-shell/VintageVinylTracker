package org.example.Service;

import org.example.DTO.Record;
import org.example.Infrastructure.database.ConnectionFactory;
import org.example.Repository.DatabaseRepository;

import java.sql.Connection;
import java.util.ArrayList;

public class DatabaseService {
    private Connection conn;
    private DatabaseRepository databaseRepository;
    private String dbURI;

    public DatabaseService(Connection conn, DatabaseRepository repository,  String dbURI) {
        this.conn = conn;
        this.databaseRepository = repository;
        this.dbURI = dbURI;
    } // constructor

    public boolean addRecordEntry(int id, String bandName, String albumName, String year,
                                  String country, String catNo, String thumbUrl, boolean isOwned, double purchasePrice,
                                  double value, String condition) {
        checkConnection();
        return databaseRepository.addRecordEntry(id, bandName, albumName, year,
                country, catNo, thumbUrl, isOwned, purchasePrice, value,
                condition, conn);
    } // addRecordEntry()

    public ArrayList<Record> searchRecordEntries(String bandName, String albumName, String year, String catNo,
                                                 String isOwned) {
        checkConnection();
        return databaseRepository.searchRecordEntries(bandName, albumName, year, catNo, isOwned, conn);
    } // searchRecordEntries()

    public boolean deleteRecordEntry(int id) {
        checkConnection();
        return databaseRepository.deleteRecordEntry(id, conn);
    } // deleteRecordEntry()

    public boolean checkForOwnedPricingUpdate() {
        checkConnection();
        return databaseRepository.checkForUpdate(conn);
    } // checkForOwnedPricingUpdate()

    public boolean updateRecordPrice(int id, double newPrice) {
        checkConnection();
        return databaseRepository.updateRecordPrice(id, newPrice, conn);
    } // updateRecordPrice()

    public void updateMetaDate() {
        checkConnection();
        updateMetaDate();
    } // updateMetaDate()

    private void checkConnection() {
        if (!ConnectionFactory.isValidConnection(conn)) {
            ConnectionFactory.initConnection(dbURI);
        }
    } // checkConnection()
} // DatabaseService
