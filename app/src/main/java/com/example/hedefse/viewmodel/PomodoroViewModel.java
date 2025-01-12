package com.example.hedefse.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.hedefse.Receivers.ResetAlarmReceiver;
import com.example.hedefse.model.entities.PomodoroCycle;
import com.example.hedefse.model.daos.PomodoroDao;
import com.example.hedefse.model.database.TodoDatabase;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class PomodoroViewModel extends AndroidViewModel {
    private final MutableLiveData<Long> timeLeft = new MutableLiveData<>();
    private final MutableLiveData<Integer> cycleCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> dailyCycleCount = new MutableLiveData<>(0);
    private final LiveData<Map<String, Integer>> dailyCycles;
    private CountDownTimer timer;
    private static final long POMODORO_DURATION = 25 * 60 * 1000; // 25 dakika
    private static final long SHORT_BREAK_DURATION = 5 * 60 * 1000; // 5 dakika
    private static final long LONG_BREAK_DURATION = 30 * 60 * 1000; // 30 dakika
    private long timeRemaining = POMODORO_DURATION;
    private boolean isRunning = false;
    private boolean isBreakTime = false;
    private PomodoroDao pomodoroDao;
    private SharedPreferences sharedPreferences;

    public PomodoroViewModel(@NonNull Application application) {
        super(application);
        TodoDatabase database = TodoDatabase.getInstance(application);
        pomodoroDao = database.pomodoroDao();
        sharedPreferences = application.getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);
        loadCycleCount();
        setupDailyResetAlarm(application);

        // Veritabanından döngü sayısını çek
        dailyCycles = Transformations.map(pomodoroDao.getAllCycles(), this::aggregateDailyCycles);

        // Günlük döngü sayısını sıfırlayıp gözle
        resetDailyCycleCountIfNecessary();
    }

    public LiveData<Long> getTimeLeft() {
        return timeLeft;
    }

    public LiveData<Integer> getCycleCount() {
        return Transformations.map(dailyCycles, cycles -> cycles.values().stream().mapToInt(Integer::intValue).sum());
    }

    public LiveData<Integer> getDailyCycleCount() {
        return dailyCycleCount;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void startPomodoro() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(timeRemaining, 1000) {
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                timeLeft.setValue(millisUntilFinished);
            }
            public void onFinish() {
                if (isBreakTime) {
                    timeRemaining = POMODORO_DURATION;
                    isBreakTime = false;
                } else {
                    cycleCount.setValue(cycleCount.getValue() + 1);
                    dailyCycleCount.setValue(dailyCycleCount.getValue() + 1);
                    saveCycleCount(cycleCount.getValue(), dailyCycleCount.getValue());
                    if (cycleCount.getValue() % 4 == 0) {
                        timeRemaining = LONG_BREAK_DURATION;
                    } else {
                        timeRemaining = SHORT_BREAK_DURATION;
                    }
                    isBreakTime = true;
                }
                startPomodoro(); // Yeni döngüyü başlat
            }
        }.start();
        isRunning = true;
    }

    public void pausePomodoro() {
        if (timer != null) {
            timer.cancel();
        }
        isRunning = false;
    }

    public void resetPomodoro() {
        if (timer != null) {
            timer.cancel();
        }
        timeRemaining = isBreakTime ? SHORT_BREAK_DURATION : POMODORO_DURATION;
        timeLeft.setValue(timeRemaining);
        isRunning = false;
    }

    public void skipCycle() {
        if (timer != null) {
            timer.cancel();
        }
        if (!isBreakTime) {
            cycleCount.setValue(cycleCount.getValue() + 1);
            dailyCycleCount.setValue(dailyCycleCount.getValue() + 1);
            saveCycleCount(cycleCount.getValue(), dailyCycleCount.getValue());
            timeRemaining = (cycleCount.getValue() % 4 == 3) ? LONG_BREAK_DURATION : SHORT_BREAK_DURATION;
        } else {
            timeRemaining = POMODORO_DURATION;
        }
        isBreakTime = !isBreakTime;
        timeLeft.setValue(timeRemaining);
        isRunning = false;
    }

    public void setupDailyResetAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ResetAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void saveCycleCount(int currentCycleCount, int currentDailyCycleCount) {
        sharedPreferences.edit().putInt("cycle_count", currentCycleCount).apply();
        sharedPreferences.edit().putInt("daily_cycle_count", currentDailyCycleCount).apply();
        PomodoroCycle cycle = new PomodoroCycle();
        cycle.setCycleCount(currentCycleCount);
        cycle.setWorkDuration(POMODORO_DURATION);
        cycle.setBreakDuration(isBreakTime ? (currentCycleCount % 4 == 0 ? LONG_BREAK_DURATION : SHORT_BREAK_DURATION) : SHORT_BREAK_DURATION);
        cycle.setBreakTime(isBreakTime);
        cycle.setDate(System.currentTimeMillis());
        new InsertCycleAsyncTask(pomodoroDao).execute(cycle);
    }

    private void loadCycleCount() {
        int lastCycleCount = sharedPreferences.getInt("cycle_count", 0);
        int lastDailyCycleCount = sharedPreferences.getInt("daily_cycle_count", 0);
        cycleCount.setValue(lastCycleCount);
        dailyCycleCount.setValue(lastDailyCycleCount);
    }

    private void resetDailyCycleCountIfNecessary() {
        int lastResetDay = sharedPreferences.getInt("last_reset_day", -1);
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);

        if (lastResetDay != currentDay) {
            dailyCycleCount.setValue(0);
            sharedPreferences.edit().putInt("daily_cycle_count", 0).apply();
            sharedPreferences.edit().putInt("last_reset_day", currentDay).apply();
        }
    }

    private static class InsertCycleAsyncTask extends AsyncTask<PomodoroCycle, Void, Void> {
        private PomodoroDao pomodoroDao;

        private InsertCycleAsyncTask(PomodoroDao pomodoroDao) {
            this.pomodoroDao = pomodoroDao;
        }

        @Override
        protected Void doInBackground(PomodoroCycle... cycles) {
            pomodoroDao.insert(cycles[0]);
            return null;
        }
    }

    private Map<String, Integer> aggregateDailyCycles(List<PomodoroCycle> cycles) {
        Map<String, Integer> dailyCyclesMap = new HashMap<>();
        for (PomodoroCycle cycle : cycles) {
            String date = PomodoroCycle.getFormattedDate(cycle.getDate());
            if (cycle.getWorkDuration() == POMODORO_DURATION && !cycle.isBreakTime()) { // Sadece çalışma döngülerini say
                dailyCyclesMap.put(date, dailyCyclesMap.getOrDefault(date, 0) + 1);
            }
        }
        return dailyCyclesMap;
    }
}
