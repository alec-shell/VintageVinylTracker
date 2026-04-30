package integration;

import org.example.DTO.Record;
import org.example.Infrastructure.database.ConnectionFactory;
import org.example.Infrastructure.database.DatabaseInitializer;
import org.example.Repository.DatabaseRepository;
import org.junit.jupiter.api.*;
import unit.TestRecordsArrayList;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseRepositoryTest {
    Connection conn;
    DatabaseRepository databaseRepository;
    ArrayList<Record> testRecords;

    @BeforeEach
    void setUp() {
        conn = ConnectionFactory.initConnection("jdbc:sqlite::memory:");
        DatabaseInitializer.initTables(conn);
        databaseRepository = new DatabaseRepository();
        testRecords = TestRecordsArrayList.buildTestRecords();
    } // setUp()

    @AfterEach
    void tearDown() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        }
    } // tearDown()

    @Test
    public void addRecordEntry_addValidRecords() {
        for (Record record : testRecords) {
            assertTrue(addRecord(record));
        }
    }

    @Test
    public void addRecordEntry_addInvalidRecord() {
        addRecord(testRecords.get(0));
        assertFalse(addRecord(testRecords.get(0)));
    }

    @Test
    public void searchRecordEntries_searchForValidEntries() {
        for (Record record : testRecords) {addRecord(record);}

        ArrayList<Record> validName = databaseRepository.searchRecordEntries("Pink Floyd", null,
                null, null, null, conn);
        assertEquals(1, validName.size());
        assertEquals("Pink Floyd", validName.get(0).getArtistName());

        ArrayList<Record> validAlbum = databaseRepository.searchRecordEntries(null, "Abbey Road",
                null, null, null, conn);
        assertEquals(1, validAlbum.size());
        assertEquals("Abbey Road", validAlbum.get(0).getAlbumName());

        ArrayList<Record> validYear =  databaseRepository.searchRecordEntries(null, null,
                "1977", null, null, conn);
        assertEquals(1, validYear.size());
        assertEquals("1977", validYear.get(0).getYear());

        ArrayList<Record> validCatNo = databaseRepository.searchRecordEntries(null, null,
                null, "SD 7208", null, conn);
        assertEquals(1, validCatNo.size());
        assertEquals("SD 7208", validCatNo.get(0).getCatNo());

        ArrayList<Record> validIsOwned = databaseRepository.searchRecordEntries(null, null,
                null, null, "true", conn);
        assertEquals(10, validIsOwned.size());
        assertTrue(validIsOwned.get(7).isOwned());
    }

    @Test
    public void searchRecordEntries_searchForInvalidEntry() {
        ArrayList<Record> invalidSearch = databaseRepository.searchRecordEntries("The Police", null,
                null, null, null, conn);
        assertEquals(0, invalidSearch.size());
    }

    @Test
    public void deleteRecordEntry_removeValidRecord() {
        addRecord(testRecords.get(0));
        assertTrue(databaseRepository.deleteRecordEntry(testRecords.get(0).getID(), conn));
    }

    @Test
    public void deleteRecordEntry_removeInvalidRecord() {
        assertFalse(databaseRepository.deleteRecordEntry(-1, conn));
    }

    @Test
    public void checkForOwnedPricingUpdate_isUpdatable() {
        assertTrue(databaseRepository.checkForUpdate(conn));
    }

    @Test
    public void checkForOwnedPricingUpdate_isNotUpdatableAfterUpdateMetaDate() {
        databaseRepository.updateMetaDate(conn);
        assertFalse(databaseRepository.checkForUpdate(conn));
    }

    @Test
    public void updateRecordPrice_updateValidRecord() {
        addRecord(testRecords.get(0));
        assertTrue(databaseRepository.updateRecordPrice(testRecords.get(0).getID(), 32.99, conn));
    }

    @Test
    public void updateRecordPrice_updateInvalidRecord() {
        assertFalse(databaseRepository.updateRecordPrice(-1, 32.99, conn));
    }

    private boolean addRecord(Record record) {
        return databaseRepository.addRecordEntry(record.getID(),
                record.getArtistName(),
                record.getAlbumName(),
                record.getYear(),
                record.getCountry(),
                record.getCatNo(),
                record.getThumbUrl(),
                record.isOwned(),
                record.getPurchasePrice(),
                record.getValue(),
                record.getCondition(), conn);
    }

} // DatabaseRepositoryTest
