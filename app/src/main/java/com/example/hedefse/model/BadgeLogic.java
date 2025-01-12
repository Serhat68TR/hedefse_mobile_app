package com.example.hedefse.model;

import com.example.hedefse.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BadgeLogic {
    // Pomodoro döngüleri için madalya koşulları
    public static List<String> getPomodoroBadges(int cycleCount) {
        List<String> badges = new ArrayList<>();
        if (cycleCount >= 10) {
            badges.add("Şampiyon");
        }
        if (cycleCount >= 8) {
            badges.add("Elmas");
        }
        if (cycleCount >= 7) {
            badges.add("Altın");
        }
        if (cycleCount >= 6) {
            badges.add("Gümüş");
        }
        if (cycleCount >= 5) {
            badges.add("Bronz");
        }
        return badges;
    }

    // Görev tamamlama için madalya koşulları
    public static List<String> getTaskBadges(int completedTasks, Set<String> awardedBadges) {
        List<String> badges = new ArrayList<>();
        if (completedTasks >= 5 && !awardedBadges.contains("Şampiyon")) {
            badges.add("Şampiyon");
            awardedBadges.add("Şampiyon");
        }
        if (completedTasks >= 4 && !awardedBadges.contains("Elmas")) {
            badges.add("Elmas");
            awardedBadges.add("Elmas");
        }
        if (completedTasks >= 3 && !awardedBadges.contains("Altın")) {
            badges.add("Altın");
            awardedBadges.add("Altın");
        }
        if (completedTasks >= 2 && !awardedBadges.contains("Gümüş")) {
            badges.add("Gümüş");
            awardedBadges.add("Gümüş");
        }
        if (completedTasks >= 1 && !awardedBadges.contains("Bronz")) {
            badges.add("Bronz");
            awardedBadges.add("Bronz");
        }
        return badges;
    }
}
