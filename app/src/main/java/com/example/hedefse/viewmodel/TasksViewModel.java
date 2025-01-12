package com.example.hedefse.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hedefse.Repository.TasksRepository;
import com.example.hedefse.model.entities.NotificationItem;
import com.example.hedefse.model.entities.TodoItem;

import java.util.List;
public class TasksViewModel extends AndroidViewModel {
    private final TasksRepository repository;
    private LiveData<List<TodoItem>> allTodos;
    private LiveData<List<TodoItem>> completedTodos;
    private LiveData<List<TodoItem>> notCompletedTodos;
    private LiveData<List<TodoItem>> expiredNotCompletedTodos; // Yeni LiveData
    private MutableLiveData<TodoItem> selectedTodoItem = new MutableLiveData<>();

    public TasksViewModel(@NonNull Application application) {
        super(application);
        repository = new TasksRepository(application);
        allTodos = repository.getAllTodoItems();
        completedTodos = repository.getCompletedTodos();
        notCompletedTodos = repository.getNotCompletedTodos();
        expiredNotCompletedTodos = repository.getExpiredNotCompletedTodos(); // Süresi dolmuş görevler
    }

    public LiveData<List<TodoItem>> getExpiredNotCompletedTodos() {
        return expiredNotCompletedTodos;
    }

    public void deleteTodoTask(TodoItem todoItem) {
        repository.delete(todoItem);
    }

    public void deleteNotification(NotificationItem notificationItem) {
        repository.deleteNotification(notificationItem);
    }

    public LiveData<List<NotificationItem>> getNotificationsForTodoItem(int todoId) {
        return repository.getNotificationsForTodoItem(todoId);
    }

    public void insertNotification(NotificationItem notificationItem) {
        repository.insertNotification(notificationItem);
    }

    public void setSelectedTodoItem(TodoItem todoItem) {
        selectedTodoItem.setValue(todoItem);
    }

    public LiveData<TodoItem> getSelectedTodoItem() {
        return selectedTodoItem;
    }

    public LiveData<List<TodoItem>> getAllTodos() {
        return allTodos;
    }

    public LiveData<List<TodoItem>> getCompletedTodos() {
        return completedTodos;
    }

    public LiveData<List<TodoItem>> getNotCompletedTodos() {
        return notCompletedTodos;
    }

    public void insert(TodoItem item) {
        repository.insert(item);
    }

    public void update(TodoItem item) {
        repository.update(item);
    }

    public void delete(TodoItem item) {
        repository.delete(item);
    }

    public void deleteTodoItem(TodoItem item) {
        repository.deleteTodoTask(item);
    }

    public void markTasksAsExpired() {
        repository.markTasksAsExpired();
        // Süresi dolmuş görevlerin listesini güncelle
        expiredNotCompletedTodos = repository.getExpiredNotCompletedTodos();
        notCompletedTodos = repository.getNotCompletedTodos();
    }

    public void updateExpiredTasks() {
        repository.markTasksAsExpired(); // Süresi dolmuş görevleri işaretle
        // Süresi dolmuş görevlerin listesini güncelle
        expiredNotCompletedTodos = repository.getExpiredNotCompletedTodos();
        notCompletedTodos = repository.getNotCompletedTodos();
    }
}
