package com.example.hedefse.model;

import com.example.hedefse.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BadgeLogic {
    // Pomodoro döngüleri için madalya koşulları
    public static List<String> getPomodoroBadges(int cycleCount) {
        List<String> badges = new ArrayList<>();
        if (cycleCount >= 2000) {
            badges.add("Şampiyon");
        }
        if (cycleCount >= 1500) {
            badges.add("Elmas");
        }
        if (cycleCount >= 300) {
            badges.add("Altın");
        }
        if (cycleCount >= 150) {
            badges.add("Gümüş");
        }
        if (cycleCount >= 75) {
            badges.add("Bronz");
        }
        return badges;
    }

    // Görev tamamlama için madalya koşulları
    public static List<String> getTaskBadges(int completedTasks, Set<String> awardedBadges) {
        List<String> badges = new ArrayList<>();
        if (completedTasks >= 500 && !awardedBadges.contains("Şampiyon")) {
            badges.add("Şampiyon");
            awardedBadges.add("Şampiyon");
        }
        if (completedTasks >= 300 && !awardedBadges.contains("Elmas")) {
            badges.add("Elmas");
            awardedBadges.add("Elmas");
        }
        if (completedTasks >= 150 && !awardedBadges.contains("Altın")) {
            badges.add("Altın");
            awardedBadges.add("Altın");
        }
        if (completedTasks >= 50 && !awardedBadges.contains("Gümüş")) {
            badges.add("Gümüş");
            awardedBadges.add("Gümüş");
        }
        if (completedTasks >= 30 && !awardedBadges.contains("Bronz")) {
            badges.add("Bronz");
            awardedBadges.add("Bronz");
        }
        return badges;
    }
}

