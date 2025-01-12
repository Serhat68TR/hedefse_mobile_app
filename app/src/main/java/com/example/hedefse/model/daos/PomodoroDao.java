package com.example.hedefse.model.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hedefse.model.entities.PomodoroCycle;

import java.util.List;

@Dao
public interface PomodoroDao {
    @Insert
    void insert(PomodoroCycle pomodoroCycle);

    @Update
    void update(PomodoroCycle pomodoroCycle);

    @Delete
    void delete(PomodoroCycle pomodoroCycle);

    @Query("SELECT * FROM pomodoro_cycles ORDER BY date DESC")
    LiveData<List<PomodoroCycle>> getAllCycles();

    @Query("SELECT * FROM pomodoro_cycles WHERE date = :date LIMIT 1")
    PomodoroCycle getCycleForDate(long date);
}
