package com.example.hedefse.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.hedefse.model.daos.NotificationDao;
import com.example.hedefse.model.entities.NotificationItem;
import com.example.hedefse.model.entities.TodoItem;
import com.example.hedefse.model.daos.TodoDao;
import com.example.hedefse.model.database.TodoDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class TasksRepository {
    private final TodoDao todoDao;
    private final NotificationDao notificationDao;
    private LiveData<List<TodoItem>> allTodoItems;
    private LiveData<List<TodoItem>> completedTodos;
    private LiveData<List<TodoItem>> notCompletedTodos;
    private LiveData<List<TodoItem>> expiredNotCompletedTodos;
    private ExecutorService executorService;

    public TasksRepository(Application application) {
        TodoDatabase database = TodoDatabase.getInstance(application);
        todoDao = database.todoDao();
        notificationDao = database.notificationDao();
        executorService = Executors.newFixedThreadPool(3);
        allTodoItems = todoDao.getAllTodoItems();
        completedTodos = todoDao.getCompletedTodos();
        notCompletedTodos = todoDao.getNotCompletedTodos();
        expiredNotCompletedTodos = todoDao.getExpiredNotCompletedTodos();
    }

    public LiveData<List<TodoItem>> getExpiredNotCompletedTodos() {
        return expiredNotCompletedTodos;
    }

    public void insert(TodoItem todoItem) {
        executorService.execute(() -> todoDao.insert(todoItem));
    }

    public void update(TodoItem todoItem) {
        executorService.execute(() -> todoDao.update(todoItem));
    }

    public void delete(TodoItem todoItem) {
        executorService.execute(() -> todoDao.delete(todoItem));
    }

    public void deleteNotification(NotificationItem notificationItem) {
        executorService.execute(() -> notificationDao.delete(notificationItem));
    }

    public void insertNotification(NotificationItem notificationItem) {
        executorService.execute(() -> notificationDao.insert(notificationItem));
    }

    public LiveData<List<NotificationItem>> getNotificationsForTodoItem(int todoId) {
        return notificationDao.getNotificationsByTodoId(todoId);
    }

    public LiveData<TodoItem> getTodoItemById(int id) {
        return todoDao.getTodoItemById(id);
    }

    public LiveData<List<TodoItem>> getAllTodoItems() {
        return allTodoItems;
    }

    public LiveData<List<TodoItem>> getCompletedTodos() {
        return completedTodos;
    }

    public LiveData<List<TodoItem>> getNotCompletedTodos() {
        return notCompletedTodos;
    }

    public void deleteTodoTask(TodoItem todoItem) {
        executorService.execute(() -> todoDao.delete(todoItem));
    }

    public void markTasksAsExpired() {
        executorService.execute(() -> {
            long currentTime = System.currentTimeMillis();
            todoDao.markTasksAsExpired(currentTime);
        });
    }
}
