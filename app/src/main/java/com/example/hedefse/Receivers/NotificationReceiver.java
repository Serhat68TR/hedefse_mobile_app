package com.example.hedefse.Receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.hedefse.R;
import com.example.hedefse.Repository.NotificationSettingsRepository;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "task_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");

        NotificationSettingsRepository repository = new NotificationSettingsRepository(context);
        boolean isSoundEnabled = repository.isSoundEnabled();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Task Reminder Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            if (!isSoundEnabled) {
                channel.setSound(null, null); // Ses kapalıysa sesi devre dışı bırak
            }
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.hedefse);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.hedefse) // Küçük ikonu koruyun ancak küçük ikon olarak gösterilecektir
                .setLargeIcon(largeIcon) // Tam renkli ikon olarak kullanın
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0, 500, 500, 500})
                .setLights(Color.BLUE, 3000, 3000) // Bildirim ışık rengini ve süresini ayarla
                .setAutoCancel(true);

        if (!isSoundEnabled) {
            builder.setSound(null); // Ses kapalıysa sesi devre dışı bırak
        }

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(title.hashCode(), notification);
        }
    }
}
