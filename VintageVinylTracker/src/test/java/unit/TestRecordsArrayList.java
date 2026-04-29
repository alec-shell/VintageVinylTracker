package unit; /**
 * unit.TestRecordsArrayList.java: Build and return an ArrayList<Record> for testing purposes.
 */

import org.example.DTO.Record;

import java.util.ArrayList;

public class TestRecordsArrayList {
    
    public static ArrayList<Record> buildTestRecords() {
        ArrayList<Record> testRecords = new ArrayList<>();

        testRecords.add(new Record(1, "Pink Floyd", "The Dark Side of the Moon", "1973",
                "UK", "SHVL 804", "https://example.com/dsotm.jpg", true,
                30.0, 50.0, "Near Mint (NM or M-)"));
        testRecords.add(new Record(2, "The Beatles", "Abbey Road", "1969",
                "UK", "PCS 7088", "https://example.com/abbey.jpg", true,
                25.0, 45.0, "Very Good Plus (VG+)"));
        testRecords.add(new Record(3, "Fleetwood Mac", "Rumours", "1977", "USA",
                "BSK 3010", "https://example.com/rumours.jpg", true, 15.0,
                35.0, "Very Good (VG)"));
        testRecords.add(new Record(4, "Led Zeppelin", "Led Zeppelin IV", "1971",
                "USA", "SD 7208", "https://example.com/lz4.jpg", true,
                20.0, 40.0, "Good Plus (G+)"));
        testRecords.add(new Record(5, "Michael Jackson", "Thriller", "1982",
                "USA", "QE 38112", "https://example.com/thriller.jpg", true,
                10.0, 25.0, "Near Mint (NM or M-)"));
        testRecords.add(new Record(6, "David Bowie", "The Rise and Fall of Ziggy Stardust",
                "1972", "UK", "SF 8287", "https://example.com/ziggy.jpg", true,
                40.0, 80.0, "Mint (M)"));
        testRecords.add(new Record(7, "Radiohead", "OK Computer", "1997", "UK",
                "NODATA 02", "https://example.com/okc.jpg", true, 35.0, 60.0,
                "Near Mint (NM or M-)"));
        testRecords.add(new Record(8, "Nirvana", "Nevermind", "1991", "USA",
                "DGC-24425", "https://example.com/nevermind.jpg", true, 50.0,
                120.0, "Very Good Plus (VG+)"));
        testRecords.add(new Record(9, "Miles Davis", "Kind of Blue", "1959",
                "USA", "CS 8163", "https://example.com/kblue.jpg", true,
                45.0, 95.0, "Near Mint (NM or M-)"));
        testRecords.add(new Record(10, "The Clash", "London Calling", "1979",
                "UK", "CLASH 3", "https://example.com/london.jpg", true,
                20.0, 55.0, "Very Good (VG)"));
        
        return testRecords;
    } // buildTestRecords()

}
