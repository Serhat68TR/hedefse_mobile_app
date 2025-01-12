package com.example.hedefse.model.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hedefse.model.entities.NotificationItem;

import java.util.List;

@Dao
public interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NotificationItem item);

    @Update
    void update(NotificationItem item);

    @Delete
    void delete(NotificationItem item);

    @Query("SELECT * FROM notification_table WHERE todoId = :todoId")
    LiveData<List<NotificationItem>> getNotificationsByTodoId(int todoId);
}
