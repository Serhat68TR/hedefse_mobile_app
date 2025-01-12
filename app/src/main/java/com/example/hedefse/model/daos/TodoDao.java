package com.example.hedefse.model.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hedefse.model.entities.TodoItem;

import java.util.List;
@Dao
public interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TodoItem item);

    @Update
    void update(TodoItem item);

    @Delete
    void delete(TodoItem item);

    @Query("SELECT * FROM todo_items ORDER BY id DESC")
    LiveData<List<TodoItem>> getAllTodos();

    @Query("SELECT * FROM todo_items WHERE id = :id")
    LiveData<TodoItem> getTodoItemById(int id);

    @Query("SELECT * FROM todo_items ORDER BY createdAt DESC")
    LiveData<List<TodoItem>> getAllTodoItems();

    @Query("SELECT * FROM todo_items WHERE isCompleted = 1")
    LiveData<List<TodoItem>> getCompletedTodos();

    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 1")
    LiveData<Integer> getCompletedTodoCount();

    @Query("SELECT * FROM todo_items WHERE isCompleted = 0 AND isExpired = 0")
    LiveData<List<TodoItem>> getNotCompletedTodos();

    @Query("SELECT * FROM todo_items WHERE isExpired = 1 AND isCompleted = 0")
    LiveData<List<TodoItem>> getExpiredNotCompletedTodos(); // Süresi dolmuş görevleri getiren metod

    @Query("UPDATE todo_items SET isExpired = 1 WHERE dueDate < :currentTime AND isCompleted = 0")
    void markTasksAsExpired(long currentTime); // Süresi dolmuş görevleri işaretleyen metod
}
