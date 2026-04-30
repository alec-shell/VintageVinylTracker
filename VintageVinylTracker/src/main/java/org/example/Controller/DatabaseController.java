package org.example.Controller;

import org.example.DTO.Record;
import org.example.Service.DatabaseService;

import java.util.ArrayList;

public class DatabaseController {
    private DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public boolean addRecordEntry(int id, String bandName, String albumName, String year,
                                  String country, String catNo, String thumbUrl, boolean isOwned, double purchasePrice,
                                  double value, String condition) {
        return databaseService.addRecordEntry(id, bandName, albumName, year, country, catNo,
                thumbUrl, isOwned, purchasePrice, value,condition);
    }

    public ArrayList<Record> searchRecordEntries(String bandName, String albumName, String year, String catNo,
                                                 String isOwned) {
        return databaseService.searchRecordEntries(bandName, albumName, year, catNo, isOwned);
    }

    public boolean deleteRecordEntry(int id) {
        return databaseService.deleteRecordEntry(id);
    }

    public boolean checkForOwnedPricingUpdate() {
        return databaseService.checkForOwnedPricingUpdate();
    }

    public boolean updateRecordPrice(int id, double newPrice) {
        return databaseService.updateRecordPrice(id, newPrice);
    }

    public void updateMetaDate() {
        databaseService.updateMetaDate();
    }

} // DatabaseController
