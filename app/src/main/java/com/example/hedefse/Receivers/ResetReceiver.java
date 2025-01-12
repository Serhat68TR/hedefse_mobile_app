package com.example.hedefse.Receivers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.hedefse.model.database.TodoDatabase;
import com.example.hedefse.model.entities.PomodoroCycle;
import com.example.hedefse.model.daos.PomodoroDao;

public class ResetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TodoDatabase database = TodoDatabase.getInstance(context.getApplicationContext());
        PomodoroDao pomodoroDao = database.pomodoroDao();
        new ResetCycleAsyncTask(pomodoroDao).execute();
    }

    private static class ResetCycleAsyncTask extends AsyncTask<Void, Void, Void> {
        private PomodoroDao pomodoroDao;

        private ResetCycleAsyncTask(PomodoroDao pomodoroDao) {
            this.pomodoroDao = pomodoroDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            long currentDate = System.currentTimeMillis();
            PomodoroCycle resetCycle = new PomodoroCycle();
            resetCycle.setCycleCount(0);
            resetCycle.setWorkDuration(25 * 60 * 1000);
            resetCycle.setBreakDuration(5 * 60 * 1000);
            resetCycle.setBreakTime(false);
            resetCycle.setDate(currentDate);
            pomodoroDao.insert(resetCycle);
            return null;
        }
    }
}
