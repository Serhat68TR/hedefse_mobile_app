package com.example.hedefse.view.Fragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hedefse.Receivers.NotificationReceiver;
import com.example.hedefse.Repository.NotificationSettingsRepository;
import com.example.hedefse.R;
import com.example.hedefse.Repository.TasksRepository;
import com.example.hedefse.model.entities.TodoItem;
import com.example.hedefse.model.entities.NotificationItem;
import com.example.hedefse.view.adapter.NotificationAdapter;
import com.example.hedefse.viewmodel.TasksViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
public class NewTaskFragment extends Fragment {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private Button buttonSave;
    private List<Long> notificationTimes = new ArrayList<>();
    private TasksViewModel tasksViewModel;
    private NotificationManager notificationManager;
    private TasksRepository tasksRepository;
    private NotificationAdapter adapter;
    private NotificationSettingsRepository notificationSettingsRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_task, container, false);

        editTextTitle = view.findViewById(R.id.newTask_et_taskTitle);
        editTextDescription = view.findViewById(R.id.newTask_et_taskDescription);
        editTextStartDate = view.findViewById(R.id.newTask_et_startDate);
        editTextEndDate = view.findViewById(R.id.newTask_et_endDate);
        buttonSave = view.findViewById(R.id.newTask_btn_saveTask);
        tasksViewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);
        tasksRepository = new TasksRepository(requireActivity().getApplication());
        adapter = new NotificationAdapter(tasksRepository);
        RecyclerView recyclerView = view.findViewById(R.id.newTask_rv_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        notificationManager = getSystemService(requireContext(), NotificationManager.class);
        notificationSettingsRepository = new NotificationSettingsRepository(requireContext());

        editTextStartDate.setOnClickListener(v -> showDateTimePickerDialog(editTextStartDate));
        editTextEndDate.setOnClickListener(v -> showDateTimePickerDialog(editTextEndDate));

        Button buttonSelectTime = view.findViewById(R.id.newTask_btn_addNotification);
        buttonSelectTime.setOnClickListener(v -> showDateTimePicker());

        buttonSave.setOnClickListener(v -> saveTask());

        createNotificationChannel(); // Bildirim kanalı oluşturma


        return view;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = "Task Reminder Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("task_reminder_channel", name, importance);
            channel.setDescription(description);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showDateTimePickerDialog(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, (month1 + 1), year1);
                    // Saat seçimi için TimePickerDialog'u gösterelim
                    showTimePickerDialog(editText, selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog(EditText editText, String selectedDate) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String selectedTime = String.format("%s %02d:%02d", selectedDate, hourOfDay, minuteOfHour);
                    editText.setText(selectedTime);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);

                    TimePickerDialog timePicker = new TimePickerDialog(
                            requireContext(),
                            (timeView, hourOfDay, minute) -> {
                                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDate.set(Calendar.MINUTE, minute);
                                selectedDate.set(Calendar.SECOND, 0);

                                long selectedTime = selectedDate.getTimeInMillis();
                                notificationTimes.add(selectedTime);

                                // Yeni bir NotificationItem oluştur
                                NotificationItem newNotification = new NotificationItem(
                                        "Zamanlı Görev Bildirimi", // Bildirim mesajı
                                        0, // Geçici olarak taskId 0
                                        selectedTime
                                );

                                // RecyclerView'ı güncelle
                                adapter.addNotification(newNotification);
                                Toast.makeText(requireContext(), "Zaman seçildi.", Toast.LENGTH_SHORT).show();
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePicker.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        long startDate = convertDateTimeToMillis(editTextStartDate.getText().toString());
        long endDate = convertDateTimeToMillis(editTextEndDate.getText().toString());

        // Geçmiş tarih kontrolü, mevcut dakika dahil
        long currentTime = System.currentTimeMillis();
        // Mevcut zamana 1 dakikayı ekliyoruz, böylece mevcut dakika seçilebilir.
        if (startDate < currentTime - 60000) {
            Toast.makeText(getContext(), "Başlangıç tarihi geçmiş zamanda olamaz.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bitiş tarihinden sonra olma kontrolü
        if (startDate > endDate) {
            Toast.makeText(getContext(), "Başlangıç tarihi, bitiş tarihinden sonra olamaz.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!title.isEmpty() && !description.isEmpty()) {
            int taskId = UUID.randomUUID().hashCode(); // Yeni bir taskId oluşturuyoruz
            long dueDate = endDate; // Bitiş tarihini dueDate olarak ayarlıyoruz
            TodoItem newItem = new TodoItem(taskId, title, description, false, System.currentTimeMillis(), startDate, endDate, dueDate);
            tasksViewModel.insert(newItem); // Yeni görevi ekliyoruz

            // Bildirimler yalnızca bu yeni görevle ilişkilendirilmiş olmalı
            for (Long time : notificationTimes) {
                NotificationItem notification = new NotificationItem("Zamanlı Görev Bildirimi", taskId, time);
                tasksViewModel.insertNotification(notification); // Yeni bildirimi kaydediyoruz

                // Bildirimi zamanla
                if (notificationSettingsRepository.areNotificationsEnabled()) {
                    scheduleNotification(notification, title, description);
                    // Süresi dolmuş görevleri kontrol et
                    scheduleExpirationNotification(newItem);
                }
            }

            // RecyclerView'ı güncelle
            adapter.clearNotifications(); // Önceki görevlerin bildirimlerini temizle
            tasksViewModel.getNotificationsForTodoItem(taskId).observe(getViewLifecycleOwner(), notifications -> {
                adapter.setNotifications(notifications);
                adapter.notifyDataSetChanged();
            });

            requireActivity().getSupportFragmentManager().popBackStack(); // Fragment'ı geri al

        } else {
            Toast.makeText(getContext(), "Başlık ve açıklama boş olamaz.", Toast.LENGTH_SHORT).show();
        }
    }




    private long convertDateTimeToMillis(String dateTime) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date date = format.parse(dateTime);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }



    private void scheduleExpirationNotification(TodoItem todoItem) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 ve üzeri
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
        }

        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        intent.putExtra("title", "Görev Süresi Doldu");
        intent.putExtra("description", todoItem.getTitle() + " görevinin süresi doldu.");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                todoItem.getId(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, todoItem.getEndDate(), pendingIntent);
        }
        Toast.makeText(getContext(), "Bildirim hazirlandi.", Toast.LENGTH_SHORT).show();
    }



    private void scheduleNotification(NotificationItem notification, String title, String description) {
        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.putExtra("timeMillis", notification.getTimestamp());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                notification.getNotificationId(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notification.getTimestamp(), pendingIntent);
        }
        Toast.makeText(getContext(), "Bildirim zamanlandı.", Toast.LENGTH_SHORT).show();
    }

}