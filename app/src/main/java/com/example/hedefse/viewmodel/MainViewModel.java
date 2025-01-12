package com.example.hedefse.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hedefse.R;
import com.example.hedefse.Repository.ThemeRepository;
import com.example.hedefse.model.daos.ColorSettingDao;
import com.example.hedefse.model.database.TodoDatabase;
import com.example.hedefse.model.entities.ColorSetting;

import androidx.lifecycle.Transformations;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<Integer> backgroundColorLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> linearLayoutColorLiveData = new MutableLiveData<>();
    private ColorSettingDao colorSettingDao;
    private ThemeRepository themeRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        TodoDatabase db = TodoDatabase.getInstance(application);
        colorSettingDao = db.colorSettingDao();
        themeRepository = new ThemeRepository(application);
        loadColorSettings();
    }

    public LiveData<Integer> getBackgroundColorLiveData() {
        return Transformations.distinctUntilChanged(backgroundColorLiveData);
    }

    public LiveData<Integer> getLinearLayoutColorLiveData() {
        return Transformations.distinctUntilChanged(linearLayoutColorLiveData);
    }

    public void setBackgroundColor(int colorResId) {
        backgroundColorLiveData.setValue(colorResId);
        themeRepository.setBackgroundColor(colorResId);
        updateLinearLayoutColor(colorResId);
        saveColorSetting();
    }

    public void setLinearLayoutColor(int colorResId) {
        linearLayoutColorLiveData.setValue(colorResId);
        themeRepository.setLinearLayoutColor(colorResId);
        saveColorSetting();
    }

    private void updateLinearLayoutColor(int colorResId) {
        if (colorResId == R.color.theme_blue) {
            linearLayoutColorLiveData.postValue(R.color.theme_linear_blue);
        } else if (colorResId == R.color.theme_pink) {
            linearLayoutColorLiveData.postValue(R.color.theme_linear_pink);
        } else if (colorResId == R.color.theme_orange) {
            linearLayoutColorLiveData.postValue(R.color.theme_linear_orange);
        } else if (colorResId == R.color.theme_green) {
            linearLayoutColorLiveData.postValue(R.color.theme_linear_green);
        } else if (colorResId == R.color.theme_gray) {
            linearLayoutColorLiveData.postValue(R.color.theme_linear_gray);
        } else if (colorResId == R.color.theme_red) {
            linearLayoutColorLiveData.postValue(R.color.theme_linear_red);
        } else if (colorResId == R.color.default_background) {
            linearLayoutColorLiveData.postValue(R.color.theme_linear_blue);
        } else {
            linearLayoutColorLiveData.postValue(R.color.yellow);
        }
    }

    private void saveColorSetting() {
        if (backgroundColorLiveData.getValue() != null && linearLayoutColorLiveData.getValue() != null) {
            ColorSetting colorSetting = new ColorSetting(1, backgroundColorLiveData.getValue(), linearLayoutColorLiveData.getValue());
            new Thread(() -> colorSettingDao.insertColorSetting(colorSetting)).start();
        }
    }

    private void loadColorSettings() {
        new Thread(() -> {
            ColorSetting colorSetting = colorSettingDao.getColorSetting(1);
            if (colorSetting != null) {
                backgroundColorLiveData.postValue(colorSetting.getBackgroundColor());
                linearLayoutColorLiveData.postValue(colorSetting.getLinearLayoutColor());
            } else {
                int defaultBackgroundColor = themeRepository.getBackgroundColor();
                int defaultLinearLayoutColor = themeRepository.getLinearLayoutColor();
                backgroundColorLiveData.postValue(defaultBackgroundColor);
                linearLayoutColorLiveData.postValue(defaultLinearLayoutColor);
            }
        }).start();
    }
}

