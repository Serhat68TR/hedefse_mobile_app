package com.example.hedefse.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "todo_items")
public class TodoItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private boolean isCompleted;
    private long createdAt;
    private long startDate;  // Başlangıç tarihi
    private long endDate;    // Bitiş tarihi
    private long dueDate; // Yeni alan
    // Constructor, getter ve setter metodları
    private boolean isExpired; // Yeni alan

    public TodoItem(int id, String title, String description, boolean isCompleted, long createdAt, long startDate, long endDate, long dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dueDate = dueDate; // Yeni alanı ayarlayın3
        this.isExpired = isExpired; // Yeni alanı ayarlayın
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    // Getter ve Setter metodlarını ekleyin
    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }
    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }
}
