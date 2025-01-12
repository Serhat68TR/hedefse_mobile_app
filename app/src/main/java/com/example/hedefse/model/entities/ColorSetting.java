package com.example.hedefse.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "color_settings")
public class ColorSetting {
    @PrimaryKey
    private int id;
    private int backgroundColor;
    private int linearLayoutColor;

    public ColorSetting(int id, int backgroundColor, int linearLayoutColor) {
        this.id = id;
        this.backgroundColor = backgroundColor;
        this.linearLayoutColor = linearLayoutColor;
    }

    public int getId() {
        return id;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getLinearLayoutColor() {
        return linearLayoutColor;
    }
}
