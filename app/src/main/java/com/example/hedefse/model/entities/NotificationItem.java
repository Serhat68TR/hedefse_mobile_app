package com.example.hedefse.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification_table")
public class NotificationItem {
    @PrimaryKey(autoGenerate = true)
    private int notificationId;
    private String message;
    private long timestamp;
    private int todoId; // Göreve bağlama

    // Constructor
    public NotificationItem(String message, int todoId, long timestamp) {
        this.message = message;
        this.todoId = todoId;
        this.timestamp = timestamp;
    }

    // Getter ve Setter metodları
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTodoId() {
        return todoId;
    }

    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }
}
