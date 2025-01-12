package com.example.hedefse.model.entities;

public class PomodoroItem {
    private String date;
    private Integer cycleCount;
    private String badge;

    public PomodoroItem(String date, Integer cycleCount, String badge) {
        this.date = date;
        this.cycleCount = cycleCount;
        this.badge = badge;
    }

    public String getDate() {
        return date;
    }

    public Integer getCycleCount() {
        return cycleCount;
    }

    public String getBadge() {
        return badge;
    }
}

