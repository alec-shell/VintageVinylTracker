package org.example.GUI.statsUpdate;

import org.example.GUI.StatsUI;


public class EventTriggers {
    private StatsUI statsUI;

    public void setStatsUI(StatsUI statsUI) {
        this.statsUI = statsUI;
    }

    public void updateStatsUI() {
        if  (statsUI != null) {
            statsUI.removeAll();
            statsUI.buildPanel();
        }
    } // updateStatsUI()

} // EventTriggers class
