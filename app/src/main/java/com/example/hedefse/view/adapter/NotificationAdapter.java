package com.example.hedefse.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hedefse.R;
import com.example.hedefse.Repository.TasksRepository;
import com.example.hedefse.model.entities.NotificationItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {


    private final List<NotificationItem> notificationList = new ArrayList<>();
    private NotificationItem selectedNotification = null;
    private final TasksRepository tasksRepository;

    public NotificationAdapter(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    // Yeni bildirimleri set etmek için kullanılır
    public void setNotifications(List<NotificationItem> notifications) {
        if (notifications != null) {
            notificationList.clear(); // Önceki bildirimleri temizle
            notificationList.addAll(notifications);
            notifyDataSetChanged();
        }
    }

    // Bildirim eklemek için kullanılır
    public void addNotification(NotificationItem notification) {
        if (notification != null) {
            notificationList.add(notification);
            notifyItemInserted(notificationList.size() - 1);
        }
    }

    // Bildirimleri temizlemek için kullanılır
    public void clearNotifications() {
        notificationList.clear();
        notifyDataSetChanged();
    }

    // Seçili bildirimi almak için kullanılır
    public NotificationItem getSelectedNotification() {
        return selectedNotification;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false); // Özelleştirilmiş layout'u burada kullan
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notification = notificationList.get(position);

        if (notification != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            try {
                String formattedTime = sdf.format(new Date(notification.getTimestamp()));
                holder.titleTextView.setText("Bildirim " + (position + 1));
                holder.timeTextView.setText(formattedTime);
            } catch (Exception e) {
                e.printStackTrace();
                holder.timeTextView.setText("Geçersiz Zaman");
            }

            // Seçili bildirimi gösteren animasyon
            holder.itemView.setSelected(selectedNotification == notification);

            holder.itemView.setOnClickListener(view -> {
                if (selectedNotification == notification) {
                    selectedNotification = null;
                } else {
                    selectedNotification = notification;
                }
                notifyDataSetChanged(); // RecyclerView'ı yeniden düzenlemek için
            });
        }
    }

    public void removeSelectedNotification() {
        if (selectedNotification != null) {
            int position = notificationList.indexOf(selectedNotification);
            notificationList.remove(selectedNotification);
            notifyItemRemoved(position);
            selectedNotification = null;
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView timeTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notifi_tv_notificationTitle);
            timeTextView = itemView.findViewById(R.id.notifi_tv_notificationTime);
        }
    }
}
