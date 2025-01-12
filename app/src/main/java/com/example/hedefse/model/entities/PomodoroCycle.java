package com.example.hedefse.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "pomodoro_cycles")
public class PomodoroCycle {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int cycleCount;
    private long workDuration;
    private long breakDuration;
    private boolean isBreakTime;
    private long date; // Eklendi: tarih bilgisi

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCycleCount() { return cycleCount; }
    public void setCycleCount(int cycleCount) { this.cycleCount = cycleCount; }

    public long getWorkDuration() { return workDuration; }
    public void setWorkDuration(long workDuration) { this.workDuration = workDuration; }

    public long getBreakDuration() { return breakDuration; }
    public void setBreakDuration(long breakDuration) { this.breakDuration = breakDuration; }

    public boolean isBreakTime() { return isBreakTime; }
    public void setBreakTime(boolean breakTime) { isBreakTime = breakTime; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public static String getFormattedDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // dd/MM/yyyy formatı
        return sdf.format(new Date(millis));
    }
}


