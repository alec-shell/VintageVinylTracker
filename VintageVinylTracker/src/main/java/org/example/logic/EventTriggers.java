package org.example.logic;

import org.example.gui.StatsUI;

public class EventTriggers {
    private StatsUI statsUI;
    public EventTriggers(StatsUI statsUI) {
        this.statsUI = statsUI;
    } // constructor

    public void updateStatsUI() {
        statsUI.removeAll();
        statsUI.buildPanel();
    } // updateStatsUI()

} // EventTriggers class
