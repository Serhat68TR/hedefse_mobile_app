package com.example.hedefse.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hedefse.Repository.NotificationSettingsRepository;

public class NotificationSettingsViewModel extends ViewModel {
    private final NotificationSettingsRepository repository;
    private final MutableLiveData<Boolean> notificationsEnabled = new MutableLiveData<>();

    public NotificationSettingsViewModel(NotificationSettingsRepository repository) {
        this.repository = repository;
        loadSettings();
    }

    public LiveData<Boolean> areNotificationsEnabled() {
        return notificationsEnabled;
    }


    public void setNotificationsEnabled(boolean enabled) {
        notificationsEnabled.setValue(enabled);
        repository.setNotificationsEnabled(enabled);
    }



    private void loadSettings() {
        notificationsEnabled.setValue(repository.areNotificationsEnabled());
    }
}
