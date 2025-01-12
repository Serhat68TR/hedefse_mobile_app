package com.example.hedefse.model.entities;

public class TaskBadgeItem {
    private String badge;
    private String date;

    public TaskBadgeItem(String badge, String date) {
        this.badge = badge;
        this.date = date;
    }

    public String getBadge() {
        return badge;
    }

    public String getDate() {
        return date;
    }
}
