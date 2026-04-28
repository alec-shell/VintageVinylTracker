package unit;

import org.example.DTO.CollectionStats;
import org.example.DTO.Record;
import org.example.Service.StatsService;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StatsServiceTest {

    @Test
    public void parseOwnedAlbums_populatedRecordsArrayListTest() {
        TestRecordsArrayList testRecordsArrayList = new TestRecordsArrayList();
        ArrayList<Record> testRecords = testRecordsArrayList.buildTestRecords();

        CollectionStats testStats = StatsService.parseOwnedAlbums(testRecords);

        assertEquals(10, testStats.getOwnedCount());
        assertEquals(290, testStats.getTotalInvested());
        assertEquals(605, testStats.getTotalValue());
        assertEquals(8, testStats.getMostValuableRecord().getID());
        assertEquals(5, testStats.getLeastValuableRecord().getID());
        assertEquals(testRecords,  testStats.getOwnedRecords());
        assertFalse(testStats.getIsUpdating());
    }

    @Test
    public void parseOwnedAlbums_emptyRecordsArrayListTest() {
        ArrayList<Record> testRecords = new ArrayList<>();
        CollectionStats testStats = StatsService.parseOwnedAlbums(testRecords);

        assertEquals(0, testStats.getOwnedCount());
        assertEquals(0, testStats.getTotalInvested());
        assertEquals(0, testStats.getTotalValue());
        assertNull(testStats.getMostValuableRecord());
        assertNull(testStats.getLeastValuableRecord());
        assertEquals(testRecords,  testStats.getOwnedRecords());
        assertFalse(testStats.getIsUpdating());
    }
}
