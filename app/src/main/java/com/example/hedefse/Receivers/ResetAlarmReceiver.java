package com.example.hedefse.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class ResetAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("daily_cycle_count", 0); // Günlük döngü sayısını sıfırla
        editor.putInt("last_reset_day", Calendar.getInstance().get(Calendar.DAY_OF_YEAR)); // Sıfırlama gününü güncelle
        editor.apply();
    }
}
