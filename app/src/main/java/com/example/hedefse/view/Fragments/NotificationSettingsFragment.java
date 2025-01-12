package com.example.hedefse.view.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hedefse.Repository.NotificationSettingsRepository;
import com.example.hedefse.viewmodel.NotificationSettingsViewModel;
import com.example.hedefse.viewmodel.NotificationSettingsViewModelFactory;
import com.example.hedefse.R;

public class NotificationSettingsFragment extends Fragment {

    private Switch switchNotifications;
    private NotificationSettingsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_settings, container, false);

        // Switch'leri tanımla
        switchNotifications = view.findViewById(R.id.notiSet_switch_notifications);

        // ViewModel'i başlat
        NotificationSettingsRepository repository = new NotificationSettingsRepository(requireContext());
        NotificationSettingsViewModelFactory factory = new NotificationSettingsViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(NotificationSettingsViewModel.class);

        // Varsayılan değerleri oku ve switch'leri güncelle
        viewModel.areNotificationsEnabled().observe(getViewLifecycleOwner(), enabled ->
                switchNotifications.setChecked(enabled));


        // Değişiklikleri kaydet
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                viewModel.setNotificationsEnabled(isChecked));


        return view;
    }
}
