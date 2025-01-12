package com.example.hedefse.model.entities;

import java.util.ArrayList;
import java.util.List;

public class Success {
    private int totalTasksCompleted;
    private int dailyStreak;
    private List<String> badges;

    public Success() {
        this.totalTasksCompleted = 0;
        this.dailyStreak = 0;
        this.badges = new ArrayList<>();
    }

    public int getTotalTasksCompleted() {
        return totalTasksCompleted;
    }

    public void setTotalTasksCompleted(int totalTasksCompleted) {
        this.totalTasksCompleted = totalTasksCompleted;
    }

    public int getDailyStreak() {
        return dailyStreak;
    }

    public void setDailyStreak(int dailyStreak) {
        this.dailyStreak = dailyStreak;
    }

    public List<String> getBadges() {
        return badges;
    }

    public void setBadges(List<String> badges) {
        this.badges = badges;
    }
}
