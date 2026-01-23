package org.example;

import javax.swing.*;

public class StatsUI extends JPanel {
    private DiscogsAuthorization discogsAuth;
    private DBAccess dbAccess;

    public StatsUI(DiscogsAuthorization discogsAuth, DBAccess dbAccess) {
        this.discogsAuth = discogsAuth;
        this.dbAccess = dbAccess;
    } // constructor

} // StatsUI class


/*
 * Show collection value total based on previously stored values for DB entries where is_owned == true
 * While running ASYNC op for updating entry values, display updating message.
 * - make API call to Discogs, (60 / min rate limit).
 * - update DB entry with new value.
 * - store pending total value.
 * When ASYNC operation is done, update collection value total.
 * ** Need to track update date, limiting transaction to one per day. Stored in external file. **
 */
