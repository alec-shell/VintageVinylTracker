/**
 * Record.java: Record class.
 */

package org.example;


public class Record {
    private int id;
    private String artistName;
    private String albumName;
    private String year;
    private String country;
    private String catNo;
    private String thumbUrl;
    private boolean isOwned;
    private Double purchasePrice;

    public Record(int id,
                     String bandName,
                     String albumName,
                     String year,
                     String country,
                     String catNo,
                     String thumbUrl,
                     boolean isOwned,
                  Double purchasePrice) {
        this.id = id;
        this.artistName = bandName;
        this.albumName = albumName;
        this.year = year;
        this.country = country;
        this.catNo = catNo;
        this.thumbUrl = thumbUrl;
        this.isOwned = isOwned;
        this.purchasePrice = purchasePrice;
    } // constructor

    // getters
    public final int getID() {
        return id;
    } // getID()

    public final String getArtistName() {
        return artistName;
    } // getArtistName()

    public final String getAlbumName() {
        return albumName;
    } // getAlbumName()

    public final String getYear() {
        return year;
    } // getYear()

    public final String getCountry() {
        return country;
    } // getCountry()

    public final String getCatNo() {
        return catNo;
    } // getCatNo()

    public final String getThumbUrl() {
        return thumbUrl;
    } // getThumbUrl()

    public final boolean isOwned() {
        return isOwned;
    } // isOwned()

    public final Double getPurchasePrice() {
        return purchasePrice;
    } // getPurchasePrice()

    // setters
    public final void setID(int id) {
        this.id = id;
    } // setID()

    public final void setArtistName(String artistName) {
        this.artistName = artistName;
    } // setArtistName()

    public final void setAlbumName(String albumName) {
        this.albumName = albumName;
    } // setAlbumName()

    public final void setYear(String year) {
        this.year = year;
    } // setYear()

    public final void setCountry(String country) {
        this.country = country;
    } // setCountry()

    public final void setCatNo(String catNo) {
        this.catNo = catNo;
    } // setCatNo()

    public final void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    } // setThumbUrl()

    public final void setIsOwned(boolean isOwned) {
        this.isOwned = isOwned;
    } // setIsOwned()

    public final void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    } // setPurchasePrice()

} // Record class
