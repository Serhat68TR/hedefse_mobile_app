package com.example.hedefse.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hedefse.Repository.ThemeRepository;
import com.example.hedefse.model.database.TodoDatabase;
import com.example.hedefse.model.entities.ColorSetting;
import com.example.hedefse.viewmodel.MainViewModel;
import com.example.hedefse.view.Fragments.NotificationSettingsFragment;
import com.example.hedefse.R;
import com.example.hedefse.model.entities.SettingItem;

import java.util.List;

import android.content.Context;

import java.util.concurrent.Executors;
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {
    private List<SettingItem> settings;
    private MainViewModel mainViewModel;
    private ThemeRepository themeRepository;

    public SettingsAdapter(List<SettingItem> settings, MainViewModel mainViewModel, Context context) {
        this.settings = settings;
        this.mainViewModel = mainViewModel;
        this.themeRepository = new ThemeRepository(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingItem setting = settings.get(position);
        holder.title.setText(setting.getTitle());
        holder.description.setText(setting.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (setting.getTitle().equals("Tema Ayarları")) {
                // Arka Plan Rengi Seçim Diyalog Göster
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_theme_selection, null);
                builder.setView(dialogView);

                RadioGroup radioGroup = dialogView.findViewById(R.id.dialogTS_rg_themeSelection);

                builder.setPositiveButton("Tamam", (dialog, which) -> {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    int selectedColor;

                    if (selectedId == R.id.dialogTS_rb_themeRed) {
                        selectedColor = R.color.theme_red;
                    } else if (selectedId == R.id.dialogTS_rb_themeBlue) {
                        selectedColor = R.color.theme_blue;
                    } else if (selectedId == R.id.dialogTS_rb_themeGreen) {
                        selectedColor = R.color.theme_green;
                    } else if (selectedId == R.id.dialogTS_rb_themeGray) {
                        selectedColor = R.color.theme_gray;
                    } else if (selectedId == R.id.dialog_rb_themePink) {
                        selectedColor = R.color.theme_pink;
                    } else if (selectedId == R.id.dialogTS_rb_themeOrange) {
                        selectedColor = R.color.theme_orange;
                    } else {
                        selectedColor = R.color.default_background;
                    }

                    // Ana rengi ayarla
                    mainViewModel.setBackgroundColor(selectedColor);

                    // LinearLayout rengini ayarla ve kaydet
                    int linearLayoutColor;
                    if (selectedColor == R.color.theme_blue) {
                        linearLayoutColor = R.color.theme_linear_blue;
                    } else if (selectedColor == R.color.theme_pink) {
                        linearLayoutColor = R.color.theme_linear_pink;
                    } else if (selectedColor == R.color.theme_orange) {
                        linearLayoutColor = R.color.theme_linear_orange;
                    } else if (selectedColor == R.color.theme_green) {
                        linearLayoutColor = R.color.theme_linear_green;
                    } else if (selectedColor == R.color.theme_gray) {
                        linearLayoutColor = R.color.theme_linear_gray;
                    } else if (selectedColor == R.color.theme_red) {
                        linearLayoutColor = R.color.theme_linear_red;
                    } else if (selectedColor == R.color.default_background) {
                        linearLayoutColor = R.color.theme_linear_blue;
                    } else {
                        linearLayoutColor = selectedColor;
                    }
                    mainViewModel.setLinearLayoutColor(linearLayoutColor);

                    // Renk ayarlarını kaydet
                    saveColorSetting(v.getContext(), selectedColor, linearLayoutColor);

                   // FragmentActivity activity = (FragmentActivity) v.getContext();
                    //activity.getWindow().getDecorView().setBackgroundColor(activity.getResources().getColor(selectedColor));

                });

                builder.setNegativeButton("İptal", null);
                builder.show();
            }

            if (setting.getTitle().equals("Bildirim")) {
                // NotificationSettingsFragment'e git
                FragmentActivity activity = (FragmentActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new NotificationSettingsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void saveColorSetting(Context context, int backgroundColor, Integer textColor) {
        TodoDatabase db = TodoDatabase.getInstance(context);
        ColorSetting colorSetting = new ColorSetting(1, backgroundColor, textColor != null ? textColor : backgroundColor);

        // Veritabanı işlemlerini arka planda çalıştır
        Executors.newSingleThreadExecutor().execute(() -> db.colorSettingDao().insertColorSetting(colorSetting));
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemSettings_tv_settingsTitle);
            description = itemView.findViewById(R.id.itemSettings_tv_settingsDescription);
        }
    }
}
