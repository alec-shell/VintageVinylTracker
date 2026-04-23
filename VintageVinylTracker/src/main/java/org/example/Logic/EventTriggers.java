package org.example.Logic;

import org.example.GUI.StatsUI;


public class EventTriggers {
    private StatsUI statsUI;

    public EventTriggers() {} // no arg constructor

    public EventTriggers(StatsUI statsUI) {
        this.statsUI = statsUI;
    } // constructor

    public void setStatsUI(StatsUI statsUI) {
        this.statsUI = statsUI;
    }

    public void updateStatsUI() {
        statsUI.removeAll();
        statsUI.buildPanel();
    } // updateStatsUI()

} // EventTriggers class
