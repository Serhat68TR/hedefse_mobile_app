package com.example.hedefse.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.hedefse.model.BadgeLogic;
import com.example.hedefse.model.daos.TodoDao;
import com.example.hedefse.model.entities.PomodoroCycle;
import com.example.hedefse.model.daos.PomodoroDao;
import com.example.hedefse.model.entities.PomodoroItem;
import com.example.hedefse.model.entities.Success;
import com.example.hedefse.model.database.TodoDatabase;
import com.example.hedefse.model.entities.TaskBadgeItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

// SuccessViewModel.java
public class SuccessViewModel extends AndroidViewModel {
    private final MutableLiveData<Success> successLiveData;
    private final PomodoroDao pomodoroDao;
    private final TodoDao todoDao;
    private final LiveData<Integer> completedTodoCount;
    private final LiveData<Integer> totalCycles;
    private final MutableLiveData<List<PomodoroItem>> pomodoroItemsLiveData;
    private final MutableLiveData<List<TaskBadgeItem>> taskBadgeItemsLiveData;
    private final MutableLiveData<String> newBadgeLiveData;
    private final SharedPreferences sharedPreferences;

    public SuccessViewModel(@NonNull Application application) {
        super(application);
        TodoDatabase database = TodoDatabase.getInstance(application);
        pomodoroDao = database.pomodoroDao();
        todoDao = database.todoDao();
        successLiveData = new MutableLiveData<>(new Success());
        completedTodoCount = todoDao.getCompletedTodoCount();
        totalCycles = Transformations.map(pomodoroDao.getAllCycles(), this::aggregateTotalCycles);
        pomodoroItemsLiveData = new MutableLiveData<>(new ArrayList<>());
        taskBadgeItemsLiveData = new MutableLiveData<>(new ArrayList<>());
        newBadgeLiveData = new MutableLiveData<>();
        sharedPreferences = application.getSharedPreferences("BadgePrefs", Context.MODE_PRIVATE);

        totalCycles.observeForever(this::fetchPomodoroBadges);
        completedTodoCount.observeForever(this::fetchTaskBadges);
    }

    private Integer aggregateTotalCycles(List<PomodoroCycle> cycles) {
        int totalCycles = 0;
        if (cycles != null) {
            for (PomodoroCycle cycle : cycles) {
                if (cycle.getWorkDuration() == 25 * 60 * 1000 && !cycle.isBreakTime()) {
                    totalCycles++;
                }
            }
        }
        return totalCycles;
    }
    public void fetchPomodoroBadges(Integer totalCycles) {
        Log.d("SuccessViewModel", "fetchPomodoroBadges called with totalCycles: " + totalCycles);
        if (totalCycles != null) {
            List<PomodoroItem> pomodoroItems = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            List<String> badges = BadgeLogic.getPomodoroBadges(totalCycles);
            Set<String> storedBadges = sharedPreferences.getStringSet("stored_pomodoro_badges", new HashSet<>());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean newBadgeFound = false;

            for (String badge : badges) {
                String badgeKey = "pomodoro_badge_" + badge;
                String badgeDate = sharedPreferences.getString(badgeKey, currentDate);

                if (!storedBadges.contains(badge)) {
                    newBadgeLiveData.setValue("Tebrikler! Yeni bir rozet kazandınız: " + badge);
                    Log.d("SuccessViewModel", "Yeni rozet bulundu: " + badge);
                    storedBadges.add(badge);
                    editor.putStringSet("stored_pomodoro_badges", storedBadges);
                    editor.putString(badgeKey, currentDate); // Rozet alındığı tarihi sakla
                    newBadgeFound = true;
                }
                pomodoroItems.add(new PomodoroItem(badgeDate, totalCycles, badge));
            }

            if (!newBadgeFound) {
                Log.d("SuccessViewModel", "Yeni rozet bulunamadı");
                newBadgeLiveData.setValue(null);
            }

            Log.d("SuccessViewModel", "Rozetler güncellendi: " + pomodoroItems.size());
            pomodoroItemsLiveData.setValue(pomodoroItems);

            editor.apply();
        }
    }

    public void fetchTaskBadges(Integer completedTasks) {
        Log.d("SuccessViewModel", "fetchTaskBadges called with completedTasks: " + completedTasks);
        if (completedTasks != null) {
            List<TaskBadgeItem> taskBadgeItems = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            List<String> badges = BadgeLogic.getTaskBadges(completedTasks, new HashSet<>(successLiveData.getValue().getBadges()));
            Set<String> storedBadges = sharedPreferences.getStringSet("stored_task_badges", new HashSet<>());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean newBadgeFound = false;

            for (String badge : badges) {
                String badgeKey = "task_badge_" + badge;
                String badgeDate = sharedPreferences.getString(badgeKey, currentDate);

                if (!storedBadges.contains(badge)) {
                    newBadgeLiveData.setValue("Tebrikler! Yeni bir rozet kazandınız: " + badge);
                    Log.d("SuccessViewModel", "Yeni rozet bulundu: " + badge);
                    storedBadges.add(badge);
                    editor.putStringSet("stored_task_badges", storedBadges);
                    editor.putString(badgeKey, currentDate); // Rozet alındığı tarihi sakla
                    newBadgeFound = true;
                }
                taskBadgeItems.add(new TaskBadgeItem(badge, badgeDate));
            }

            if (!newBadgeFound) {
                Log.d("SuccessViewModel", "Yeni rozet bulunamadı");
                newBadgeLiveData.setValue(null);
            }

            Log.d("SuccessViewModel", "Rozetler güncellendi: " + taskBadgeItems.size());
            taskBadgeItemsLiveData.setValue(taskBadgeItems);

            editor.apply();
        }
    }


    public LiveData<List<PomodoroItem>> getPomodoroItemsLiveData() {
        return pomodoroItemsLiveData;
    }

    public LiveData<List<TaskBadgeItem>> getTaskBadgeItemsLiveData() {
        return taskBadgeItemsLiveData;
    }

    public LiveData<Integer> getCompletedTodoCount() {
        return completedTodoCount;
    }

    public LiveData<Integer> getTotalCycles() {
        return totalCycles;
    }

    public LiveData<String> getNewBadgeLiveData() {
        return newBadgeLiveData;
    }

        public void completeTask() {
            Success success = successLiveData.getValue();
            if (success != null) {
                success.setTotalTasksCompleted(success.getTotalTasksCompleted() + 1);
                successLiveData.setValue(success);
                fetchTaskBadges(success.getTotalTasksCompleted()); // Bu satırın güncellenen görev sayısını kontrol ettiğinden emin olun
            }
        }

        public void completeTaskUndo() {
            Success success = successLiveData.getValue();
            if (success != null) {
                success.setTotalTasksCompleted(success.getTotalTasksCompleted() - 1);
                successLiveData.setValue(success);
                fetchTaskBadges(success.getTotalTasksCompleted()); // Bu satırın güncellenen görev sayısını kontrol ettiğinden emin olun
            }
        }
}
