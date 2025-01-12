package com.example.hedefse.model.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.hedefse.model.daos.ColorSettingDao;
import com.example.hedefse.model.entities.ColorSetting;
import com.example.hedefse.model.entities.NotificationItem;
import com.example.hedefse.model.entities.PomodoroCycle;
import com.example.hedefse.model.daos.NotificationDao;
import com.example.hedefse.model.daos.PomodoroDao;
import com.example.hedefse.model.entities.TodoItem;
import com.example.hedefse.model.daos.TodoDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {TodoItem.class, NotificationItem.class, PomodoroCycle.class, ColorSetting.class}, version = 1) // Version numarasını artırın
public abstract class TodoDatabase extends RoomDatabase {
    private static TodoDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract TodoDao todoDao();
    public abstract NotificationDao notificationDao();
    public abstract PomodoroDao pomodoroDao();
    public abstract ColorSettingDao colorSettingDao(); // Yeni DAO eklendi

    public static synchronized TodoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            TodoDatabase.class, "todo_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
