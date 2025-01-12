package com.example.hedefse;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.hedefse.model.database.TodoDatabase;
import com.example.hedefse.view.Fragments.TasksFragment;
import com.example.hedefse.view.Fragments.PomodoroFragment;
import com.example.hedefse.view.Fragments.SettingsFragment;
import com.example.hedefse.view.Fragments.SuccessFragment;
import com.example.hedefse.viewmodel.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    MainViewModel mainViewModel;
    TodoDatabase todoDatabase; // Veritabanı referansı
    BottomNavigationView bottomNavigationView; // BottomNavigationView referansı

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Veritabanını başlat
        todoDatabase = TodoDatabase.getInstance(this);

        mainViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(MainViewModel.class);

        setContentView(R.layout.activity_main);

        // ConstraintLayout'u ve BottomNavigationView'u bul ve ViewModel'deki renk değişikliklerini gözlemle
        FrameLayout mainContainer = findViewById(R.id.main_fragment_container);
        bottomNavigationView = findViewById(R.id.main_bottom_navigation);

        // Uygulama başladığında veritabanındaki renkleri yükleyin
        mainViewModel.getBackgroundColorLiveData().observe(this, colorResId -> {
            if (colorResId != null && mainContainer != null) {
                mainContainer.setBackgroundColor(ContextCompat.getColor(this, colorResId));
            }
        });

        mainViewModel.getLinearLayoutColorLiveData().observe(this, colorResId -> {
            if (colorResId != null && bottomNavigationView != null) {
                bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, colorResId));
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new TasksFragment())
                    .commitNow();
        }

        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_settings) {
                    mainViewModel.getLinearLayoutColorLiveData().observe(this, colorResId -> {
                        if (colorResId != null && bottomNavigationView != null) {
                            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, colorResId));
                        }
                    });
                    loadFragment(new SettingsFragment());
                    return true;
                } else if (id == R.id.menu_tasks) {
                    mainViewModel.getLinearLayoutColorLiveData().observe(this, colorResId -> {
                        if (colorResId != null && bottomNavigationView != null) {
                            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, colorResId));
                        }
                    });
                    loadFragment(new TasksFragment());
                    return true;
                } else if (id == R.id.menu_success) {
                    mainViewModel.getLinearLayoutColorLiveData().observe(this, colorResId -> {
                        if (colorResId != null && bottomNavigationView != null) {
                            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, colorResId));
                        }
                    });
                    loadFragment(new SuccessFragment());
                    return true;
                } else if (id == R.id.menu_pomodoro) {
                    loadFragment(new PomodoroFragment());
                    if (bottomNavigationView != null) {
                        bottomNavigationView.setBackgroundColor(Color.parseColor("#C24545"));
                    }
                    return true;
                } else {
                    return false;
                }
            });
        }

        if (savedInstanceState == null) {
            loadFragment(new TasksFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
