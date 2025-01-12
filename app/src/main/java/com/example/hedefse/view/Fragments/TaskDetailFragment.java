package com.example.hedefse.view.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hedefse.R;
import com.example.hedefse.Repository.TasksRepository;
import com.example.hedefse.model.entities.NotificationItem;
import com.example.hedefse.model.entities.TodoItem;
import com.example.hedefse.view.adapter.NotificationAdapter;
import com.example.hedefse.viewmodel.MainViewModel;
import com.example.hedefse.viewmodel.TasksViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskDetailFragment extends Fragment {

    private TasksViewModel tasksViewModel;
    private MainViewModel mainViewModel;
    private TextView textViewTitle, textViewDescription, textViewCreatedDate, textViewStartDate, textViewEndDate;
    private EditText editTextTitle, editTextDescription, editTextStartDate, editTextEndDate;
    private Button btnDeleteTask, btnEdit, btnSave, btnDeleteNotification;
    private boolean isEditing = false;
    private NotificationAdapter notificationAdapter;
    // Bu alanı ekleyin
    private TasksRepository tasksRepository;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);

        // UI bileşenlerini ayarla
        setupUI(view);

        // ViewModel'leri başlat
        setupViewModels();

        // Observer'ları ayarla
        setupObservers();

        // Listener'ları ayarla
        setupListeners(view);

        return view;
    }

    private void setupUI(View view) {
        textViewTitle = view.findViewById(R.id.taskDet_tv_taskTitle);
        textViewDescription = view.findViewById(R.id.taskDet_tv_taskDescription);
        textViewCreatedDate = view.findViewById(R.id.taskDet_tv_creationDate);
        textViewStartDate = view.findViewById(R.id.taskDet_tv_startDate);
        textViewEndDate = view.findViewById(R.id.taskDet_tv_endDate);
        editTextTitle = view.findViewById(R.id.taskDet_et_taskTitle);
        editTextDescription = view.findViewById(R.id.taskDet_et_taskDescription);
        editTextStartDate = view.findViewById(R.id.taskDet_et_startDate);
        editTextEndDate = view.findViewById(R.id.taskDet_et_endDate);
        btnDeleteTask = view.findViewById(R.id.taskDet_btn_delete);
        btnEdit = view.findViewById(R.id.taskDet_btn_edit);
        btnSave = view.findViewById(R.id.taskDet_btn_save);
        btnDeleteNotification = view.findViewById(R.id.taskDet_btn_notDel);

        RecyclerView recyclerView = view.findViewById(R.id.taskDet_rv_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Bu satırı güncelleyin
        notificationAdapter = new NotificationAdapter(tasksRepository);
        recyclerView.setAdapter(notificationAdapter);
    }


    private void setupViewModels() {
        tasksViewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Bu satırı ekleyin
        tasksRepository = new TasksRepository(requireActivity().getApplication());
    }
    private void setupObservers() {
        // Seçilen TodoItem gözlemle
        tasksViewModel.getSelectedTodoItem().observe(getViewLifecycleOwner(), todoItem -> {
            if (todoItem != null) {
                updateUI(todoItem);
            }
        });

        // Bildirimleri gözlemle
        tasksViewModel.getSelectedTodoItem().observe(getViewLifecycleOwner(), todoItem -> {
            if (todoItem != null) {
                tasksViewModel.getNotificationsForTodoItem(todoItem.getId()).observe(getViewLifecycleOwner(), notifications -> {
                    notificationAdapter.setNotifications(notifications);
                });
            }
        });

        // LinearLayout'un arka plan rengini gözlemle
        mainViewModel.getLinearLayoutColorLiveData().observe(getViewLifecycleOwner(), colorResId -> {
            LinearLayout linearLayout = getView().findViewById(R.id.taskDet_linear_taskHeader);
            if (colorResId != null && linearLayout != null) {
                linearLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), colorResId));
            }
        });
    }

    private void setupListeners(View view) {
        btnEdit.setOnClickListener(v -> {
            if (!isEditing) {
                editTextStartDate.setText(textViewStartDate.getText());
                editTextEndDate.setText(textViewEndDate.getText());
            }
            toggleEditing(!isEditing);
        });

        btnSave.setOnClickListener(v -> saveChanges());

        btnDeleteTask.setOnClickListener(v -> {
            TodoItem selectedTodo = tasksViewModel.getSelectedTodoItem().getValue();
            if (selectedTodo != null) {
                tasksViewModel.deleteTodoItem(selectedTodo);
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnDeleteNotification.setOnClickListener(v -> deleteSelectedNotification());

        editTextStartDate.setOnClickListener(v -> showDateTimePickerDialog(editTextStartDate));
        editTextEndDate.setOnClickListener(v -> showDateTimePickerDialog(editTextEndDate));
    }

    private void updateUI(TodoItem todoItem) {
        textViewTitle.setText(todoItem.getTitle());
        textViewDescription.setText(todoItem.getDescription());
        editTextTitle.setText(todoItem.getTitle());
        editTextDescription.setText(todoItem.getDescription());
        textViewCreatedDate.setText(formatDate(todoItem.getCreatedAt()));
        textViewStartDate.setText(formatDate(todoItem.getStartDate()));
        textViewEndDate.setText(formatDate(todoItem.getEndDate()));
    }

    private void saveChanges() {
        TodoItem selectedTodo = tasksViewModel.getSelectedTodoItem().getValue();
        if (selectedTodo != null) {
            String updatedTitle = editTextTitle.getText().toString();
            String updatedDescription = editTextDescription.getText().toString();
            long updatedStartDate = convertDateTimeToMillis(editTextStartDate.getText().toString());
            long updatedEndDate = convertDateTimeToMillis(editTextEndDate.getText().toString());

            // Geçmiş tarih kontrolü, mevcut dakika dahil
            long currentTime = System.currentTimeMillis();
            // Mevcut zamana 1 dakikayı ekliyoruz, böylece mevcut dakika seçilebilir.
            if (updatedStartDate < currentTime - 60000) {
                Toast.makeText(getContext(), "Başlangıç tarihi geçmiş zamanda olamaz.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bitiş tarihinden sonra olma kontrolü
            if (updatedStartDate > updatedEndDate) {
                Toast.makeText(getContext(), "Başlangıç tarihi, bitiş tarihinden sonra olamaz.", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedTodo.setTitle(updatedTitle);
            selectedTodo.setDescription(updatedDescription);
            selectedTodo.setStartDate(updatedStartDate);
            selectedTodo.setEndDate(updatedEndDate);
            tasksViewModel.update(selectedTodo); // Veriyi güncelle

            // TextView'ları güncelle
            textViewTitle.setText(updatedTitle);
            textViewDescription.setText(updatedDescription);
            textViewStartDate.setText(formatDate(updatedStartDate));
            textViewEndDate.setText(formatDate(updatedEndDate));

            toggleEditing(false);
        }
    }


    private void deleteSelectedNotification() {
        NotificationItem selectedNotification = notificationAdapter.getSelectedNotification();
        if (selectedNotification != null) {
            tasksViewModel.deleteNotification(selectedNotification);
            notificationAdapter.removeSelectedNotification();
            notificationAdapter.notifyDataSetChanged();
        }
    }


    private void toggleEditing(boolean editing) {
        isEditing = editing;

        // Görünürlüğü ayarla
        textViewTitle.setVisibility(editing ? View.GONE : View.VISIBLE);
        textViewDescription.setVisibility(editing ? View.GONE : View.VISIBLE);
        textViewStartDate.setVisibility(editing ? View.GONE : View.VISIBLE);
        textViewEndDate.setVisibility(editing ? View.GONE : View.VISIBLE);
        editTextTitle.setVisibility(editing ? View.VISIBLE : View.GONE);
        editTextDescription.setVisibility(editing ? View.VISIBLE : View.GONE);
        editTextStartDate.setVisibility(editing ? View.VISIBLE : View.GONE);
        editTextEndDate.setVisibility(editing ? View.VISIBLE : View.GONE);
        btnEdit.setVisibility(editing ? View.GONE : View.VISIBLE);
        btnSave.setVisibility(editing ? View.VISIBLE : View.GONE);
    }

    private void showDateTimePickerDialog(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, (month1 + 1), year1);
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

    private String formatDate(long timestamp) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(timestamp));
    }

    private long convertDateTimeToMillis(String dateTime) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date date = format.parse(dateTime);
            if (date != null) {
                return date.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}