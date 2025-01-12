package com.example.hedefse.view.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hedefse.R;
import com.example.hedefse.model.entities.TaskBadgeItem;
import com.example.hedefse.view.adapter.PomodoroAdapter;
import com.example.hedefse.view.adapter.TaskAdapter;
import com.example.hedefse.viewmodel.MainViewModel;
import com.example.hedefse.viewmodel.SuccessViewModel;
import com.example.hedefse.model.entities.PomodoroItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// SuccessFragment.java
public class SuccessFragment extends Fragment {

    private SuccessViewModel successViewModel;
    private RecyclerView pomodoroRecyclerView;
    private RecyclerView taskRecyclerView;
    private PomodoroAdapter pomodoroAdapter;
    private TaskAdapter taskAdapter;
    private TextView totalTasksCompletedTextView;
    private TextView text_view_cycle_count;
    private TextView success_tv;
    private LinearLayout ne_anlama_geliyor;
    private SharedPreferences sharedPreferences;
    private Set<String> currentPomodoroBadges;
    private Set<String> alreadyCongratulatedPomodoroBadges;
    private Set<String> currentTaskBadges;
    private Set<String> alreadyCongratulatedTaskBadges;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_success, container, false);

        pomodoroRecyclerView = rootView.findViewById(R.id.success_rv_pomodoroCycles);
        pomodoroRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pomodoroAdapter = new PomodoroAdapter();
        pomodoroRecyclerView.setAdapter(pomodoroAdapter);

        taskRecyclerView = rootView.findViewById(R.id.success_rv_taskCompletion);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter();
        taskRecyclerView.setAdapter(taskAdapter);

        totalTasksCompletedTextView = rootView.findViewById(R.id.success_tv_totalTasksCompleted);
        text_view_cycle_count = rootView.findViewById(R.id.success_tv_cycleCount);

        ne_anlama_geliyor = rootView.findViewById(R.id.success_linear_whatDoesItMean);
        ne_anlama_geliyor.setOnClickListener(v -> showMedalsDialog());

        sharedPreferences = requireContext().getSharedPreferences("BadgePrefs", Context.MODE_PRIVATE);
        currentPomodoroBadges = sharedPreferences.getStringSet("stored_current_pomodoro_badges", new HashSet<>());
        alreadyCongratulatedPomodoroBadges = sharedPreferences.getStringSet("stored_congratulated_pomodoro_badges", new HashSet<>());
        currentTaskBadges = sharedPreferences.getStringSet("stored_current_task_badges", new HashSet<>());
        alreadyCongratulatedTaskBadges = sharedPreferences.getStringSet("stored_congratulated_task_badges", new HashSet<>());

        setupObservers(rootView);

        return rootView;
    }

    private void setupObservers(View rootView) {
        LinearLayout linearLayout = rootView.findViewById(R.id.success_linear_successHeader);
        success_tv = rootView.findViewById(R.id.success_tv_successTitle);
        TextView badgesTextView = rootView.findViewById(R.id.success_tv_badgesTitle);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getLinearLayoutColorLiveData().observe(getViewLifecycleOwner(), colorResId -> {
            if (colorResId != null) {
                linearLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), colorResId));
                updateTextViewColors(success_tv, badgesTextView, colorResId);
            }
        });

        successViewModel = new ViewModelProvider(requireActivity()).get(SuccessViewModel.class);

        successViewModel.getPomodoroItemsLiveData().observe(getViewLifecycleOwner(), pomodoroItems -> {
            pomodoroAdapter.setItems(pomodoroItems);
            updatePomodoroBadges(pomodoroItems);
        });

        successViewModel.getTaskBadgeItemsLiveData().observe(getViewLifecycleOwner(), taskBadgeItems -> {
            taskAdapter.setItems(taskBadgeItems);
            updateTaskBadges(taskBadgeItems);
        });

        successViewModel.getTotalCycles().observe(getViewLifecycleOwner(), totalCycles -> {
            if (totalCycles != null) {
                text_view_cycle_count.setText("Pomodoro Döngü Sayısı: " + totalCycles);
                successViewModel.fetchPomodoroBadges(totalCycles);
            }
        });

        successViewModel.getCompletedTodoCount().observe(getViewLifecycleOwner(), completedCount -> {
            if (completedCount != null) {
                totalTasksCompletedTextView.setText("Tamamlanan Görev Sayısı: " + completedCount);
                successViewModel.fetchTaskBadges(completedCount);
            }
        });

        successViewModel.getNewBadgeLiveData().observe(getViewLifecycleOwner(), newBadgeMessage -> {
            if (newBadgeMessage != null) {
                showNewBadgeDialog(newBadgeMessage);
            }
        });

        successViewModel.fetchPomodoroBadges(successViewModel.getTotalCycles().getValue() != null ? successViewModel.getTotalCycles().getValue() : 0);
        successViewModel.fetchTaskBadges(successViewModel.getCompletedTodoCount().getValue() != null ? successViewModel.getCompletedTodoCount().getValue() : 0);
    }

    private void updatePomodoroBadges(List<PomodoroItem> pomodoroItems) {
        Set<String> newBadges = new HashSet<>();
        for (PomodoroItem item : pomodoroItems) {
            newBadges.add(item.getBadge());
        }

        for (String badge : newBadges) {
            if (!currentPomodoroBadges.contains(badge) && !alreadyCongratulatedPomodoroBadges.contains(badge)) {
                showNewBadgeDialog("Tebrikler! Yeni bir rozet kazandınız: " + badge);
                alreadyCongratulatedPomodoroBadges.add(badge);
                sharedPreferences.edit().putStringSet("stored_congratulated_pomodoro_badges", alreadyCongratulatedPomodoroBadges).apply();
            }
        }

        currentPomodoroBadges = newBadges;
        sharedPreferences.edit().putStringSet("stored_current_pomodoro_badges", currentPomodoroBadges).apply();
    }

    private void updateTaskBadges(List<TaskBadgeItem> taskBadgeItems) {
        Set<String> newBadges = new HashSet<>();
        for (TaskBadgeItem item : taskBadgeItems) {
            newBadges.add(item.getBadge());
        }

        for (String badge : newBadges) {
            if (!currentTaskBadges.contains(badge) && !alreadyCongratulatedTaskBadges.contains(badge)) {
                showNewBadgeDialog("Tebrikler! Yeni bir rozet kazandınız: " + badge);
                alreadyCongratulatedTaskBadges.add(badge);
                sharedPreferences.edit().putStringSet("stored_congratulated_task_badges", alreadyCongratulatedTaskBadges).apply();
            }
        }

        currentTaskBadges = newBadges;
        sharedPreferences.edit().putStringSet("stored_current_task_badges", currentTaskBadges).apply();
    }

    private void updateTextViewColors(TextView successTv, TextView badgesTextView, int colorResId) {
        Context context = requireContext();
        int textColor = ContextCompat.getColor(context, R.color.black);
        int backgroundColor = ContextCompat.getColor(context, colorResId);

        if (colorResId == R.color.theme_linear_blue) {
            textColor = ContextCompat.getColor(context, R.color.theme_tv_blue);
        } else if (colorResId == R.color.theme_linear_green) {
            textColor = ContextCompat.getColor(context, R.color.theme_tv_green);
        } else if (colorResId == R.color.theme_linear_red) {
            textColor = ContextCompat.getColor(context, R.color.theme_tv_red);
        } else if (colorResId == R.color.theme_linear_yellow) {
            textColor = ContextCompat.getColor(context, R.color.theme_tv_yellow);
        } else if (colorResId == R.color.theme_linear_purple) {
            textColor = ContextCompat.getColor(context, R.color.theme_tv_purple);
        } else if (colorResId == R.color.theme_linear_orange) {
            textColor = ContextCompat.getColor(context, R.color.theme_tv_orange);
        } else if (colorResId == R.color.theme_linear_pink) {
            textColor = ContextCompat.getColor(context, R.color.theme_tv_pink);
        } else if (colorResId == R.color.theme_linear_gray) {
            textColor = ContextCompat.getColor(context, R.color.theme_tv_gray);
        }

        successTv.setTextColor(textColor);
        badgesTextView.setBackgroundColor(backgroundColor);
    }

    private void showMedalsDialog() {
        MedalsDialogFragment medalsDialogFragment = new MedalsDialogFragment();
        medalsDialogFragment.show(getParentFragmentManager(), "medals_dialog");
    }

    private void showNewBadgeDialog(String newBadgeMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Tebrikler!");
        builder.setMessage(newBadgeMessage);
        builder.setPositiveButton("Tamam", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
