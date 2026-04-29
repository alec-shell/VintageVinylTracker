package unit;

import org.example.DTO.SearchResult;
import org.example.Service.ParseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ParseServiceTest {
    ArrayList<String> testJson = new ArrayList<>();

    @BeforeEach
    void setUp() throws IOException {
        Path path = Paths.get("src/test/java/unit/testJson");
        testJson.addAll(Files.readAllLines(path));
    }

    @Test
    public void parseSearchResults_validJsonString() {
        int[] expectedParsedCounts = new int[] {50, 49, 48, 50, 50};
        ArrayList<SearchResult> results = new ArrayList<>();
        for (int i = 0; i < expectedParsedCounts.length; i++) {
            results.add(ParseService.parseSearchResults(testJson.get(i)));
        }
        for (int i = 0; i < expectedParsedCounts.length; i++) {
            SearchResult result = results.get(i);
            assertEquals(expectedParsedCounts[i], result.getRecords().size());
            assertTrue(result.hasNextPage());
        }
    }

    @Test
    public void parseSearchResults_invalidJsonString() {
        String json = "Invalid test data";

        SearchResult result = ParseService.parseSearchResults(json);
        assertEquals(0, result.getRecords().size());
        assertFalse(result.hasNextPage());
    }

    @Test
    public void parseSearchResults_emptyJsonString() {
        SearchResult result = ParseService.parseSearchResults("");
        assertEquals(0, result.getRecords().size());
        assertFalse(result.hasNextPage());
    }

    @Test
    public void parseSearchResults_nullJsonString() {
        SearchResult result = ParseService.parseSearchResults(null);
        assertEquals(0, result.getRecords().size());
        assertFalse(result.hasNextPage());
    }

    @Test
    public void parseSearchResults_invalidRecordsJsonString() {
        // last line has invalid title and id entries
        int[] expectedParsedCounts = new int[] {50, 49, 48, 50, 50, 47};
        ArrayList<SearchResult> results = new ArrayList<>();
        for (String json : testJson) {
            results.add(ParseService.parseSearchResults(json));
        }
        for (int i = 0; i < results.size(); i++) {
            SearchResult result = results.get(i);
            assertEquals(expectedParsedCounts[i], result.getRecords().size());
            assertTrue(result.hasNextPage());
        }
    }

}
