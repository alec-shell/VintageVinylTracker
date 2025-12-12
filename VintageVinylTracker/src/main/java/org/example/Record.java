/**
 * Record.java: Record class.
 */

package org.example;


public class Record {
    private int id;
    private String bandName;
    private String albumName;
    private String year;
    private String country;
    private String condition;
    private boolean isMaster;
    private boolean isOwned;


    public Record(int id, String bandName, String albumName, String year, String country, String condition, boolean isMaster, boolean isOwned) {
        this.id = id;
        this.bandName = bandName;
        this.albumName = albumName;
        this.year = year;
        this.country = country;
        this.condition = condition;
        this.isMaster = isMaster;
        this.isOwned = isOwned;
    } // constructor


    @Override
    public String toString() {
        return "Album: %s Band: %s Year: %s Country: %s Owned: %b Condition: %s Master: %b ID: %d"
                .formatted(albumName, bandName, year, country, isOwned, condition, isMaster, id);
    } // toString()


    public String[] returnValues() {
        return new String[] {
                albumName,
                bandName,
                year,
                country,
                Boolean.toString(isOwned),
                condition,
                Boolean.toString(isMaster),
                Integer.toString(id)
        };
    } // returnValues()

} // Record class
