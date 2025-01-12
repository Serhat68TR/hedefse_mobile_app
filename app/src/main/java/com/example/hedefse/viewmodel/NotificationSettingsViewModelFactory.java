package com.example.hedefse.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.hedefse.Repository.NotificationSettingsRepository;

public class NotificationSettingsViewModelFactory implements ViewModelProvider.Factory {
    private final NotificationSettingsRepository repository;

    public NotificationSettingsViewModelFactory(NotificationSettingsRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NotificationSettingsViewModel.class)) {
            return (T) new NotificationSettingsViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
