package com.example.hedefse.model.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hedefse.model.entities.ColorSetting;

@Dao
public interface ColorSettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertColorSetting(ColorSetting colorSetting);

    @Update
    void updateColorSetting(ColorSetting colorSetting);

    @Query("SELECT * FROM color_settings WHERE id = :id")
    ColorSetting getColorSetting(int id);
}
