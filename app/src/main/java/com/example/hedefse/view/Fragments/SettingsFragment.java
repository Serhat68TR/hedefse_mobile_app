package com.example.hedefse.view.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hedefse.R;
import com.example.hedefse.model.entities.SettingItem;
import com.example.hedefse.view.adapter.SettingsAdapter;
import com.example.hedefse.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
public class SettingsFragment extends Fragment {
    private MainViewModel mainViewModel;
    private SettingsAdapter settingsAdapter;
    private TextView fragmentSettingTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.settings_rv_settings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<SettingItem> settings = new ArrayList<>();
        settings.add(new SettingItem("Tema Ayarları", "Uygulama temasını değiştir"));
        settings.add(new SettingItem("Bildirim", "Bildirimleri etkinleştir yada takat"));
        settings.add(new SettingItem("Uygulama Hakkında", "Bu uygulama Serhat Kaya tarafından yazılmıştır."));

        settingsAdapter = new SettingsAdapter(settings, mainViewModel, requireContext());
        recyclerView.setAdapter(settingsAdapter);

        // LinearLayout'u ve TextView'i bul ve ViewModel'deki renk değişikliklerini gözlemle
        LinearLayout linearLayout = view.findViewById(R.id.settings_linear_settingsHeader);
        fragmentSettingTextView = view.findViewById(R.id.settings_tv_settingsTitle);

        mainViewModel.getLinearLayoutColorLiveData().observe(getViewLifecycleOwner(), colorResId -> {
            if (colorResId != null) {
                // LinearLayout arka plan rengini güncelle
                linearLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), colorResId));
                // TextView metin rengini güncelle
                updateTextViewColor(fragmentSettingTextView, colorResId);
            }
        });

        return view;
    }

    private void updateTextViewColor(TextView textView, int colorResId) {
        int textColor;
        if (colorResId == R.color.theme_linear_blue) {
            textColor = R.color.theme_tv_blue;
        } else if (colorResId == R.color.theme_linear_green) {
            textColor = R.color.theme_tv_green;
        } else if (colorResId == R.color.theme_linear_red) {
            textColor = R.color.theme_tv_red;
        } else if (colorResId == R.color.theme_linear_yellow) {
            textColor = R.color.theme_tv_yellow;
        } else if (colorResId == R.color.theme_linear_purple) {
            textColor = R.color.theme_tv_purple;
        } else if (colorResId == R.color.theme_linear_orange) {
            textColor = R.color.theme_tv_orange;
        } else if (colorResId == R.color.theme_linear_pink) {
            textColor = R.color.theme_tv_pink;
        } else if (colorResId == R.color.theme_linear_gray) {
            textColor = R.color.theme_tv_gray;
        } else {
            textColor = R.color.black;
        }
        textView.setTextColor(ContextCompat.getColor(requireContext(), textColor));
    }
}
