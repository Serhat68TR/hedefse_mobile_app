package com.example.hedefse.model.entities;

public class SettingItem {
    private String title;       // Ayar adı
    private String description; // Ayar açıklaması

    // Constructor
    public SettingItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getter ve Setter metodları
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

