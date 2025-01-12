package com.example.hedefse.view.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hedefse.R;
import com.example.hedefse.databinding.FragmentPomodoroBinding;
import com.example.hedefse.viewmodel.PomodoroViewModel;

import java.util.Locale;
public class PomodoroFragment extends Fragment {
    private FragmentPomodoroBinding binding;
    private PomodoroViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPomodoroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(PomodoroViewModel.class);

        // Alarmı kur
        viewModel.setupDailyResetAlarm(requireContext());

        binding.pomodoroBtnStartPause.setOnClickListener(v -> {
            if (viewModel.isRunning()) {
                viewModel.pausePomodoro();
                binding.pomodoroBtnStartPause.setText("Start");
            } else {
                viewModel.startPomodoro();
                binding.pomodoroBtnStartPause.setText("Pause");
            }
        });

        binding.pomodoroBtnReset.setOnClickListener(v -> {
            viewModel.resetPomodoro();
            binding.pomodoroBtnStartPause.setText("Start");
        });

        binding.pomodoroBtnSkipCycle.setOnClickListener(v -> {
            if (viewModel.isRunning()) {
                viewModel.pausePomodoro();
                binding.pomodoroBtnStartPause.setText("Start");
            }
            viewModel.skipCycle();
        });

        viewModel.getTimeLeft().observe(getViewLifecycleOwner(), time -> {
            if (time != null) {
                binding.pomodoroTvTimer.setText(formatTime(time));
            } else {
                binding.pomodoroTvTimer.setText("00:00"); // Varsayılan değer
            }
        });


        // Günlük döngü sayısını gözlemle ve TextView'e yazdır
        viewModel.getDailyCycleCount().observe(getViewLifecycleOwner(), dailyCycleCount -> {
            if (dailyCycleCount != null) {
                binding.pomodoroTvCycleCount.setText("Günlük Döngü Sayısı: " + dailyCycleCount);
            } else {
                binding.pomodoroTvCycleCount.setText("Günlük Döngü Sayısı: 0"); // Varsayılan değer
            }
        });
    }

    private String formatTime(long milliseconds) {
        int minutes = (int) (milliseconds / 1000) / 60;
        int seconds = (int) (milliseconds / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
