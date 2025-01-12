package com.example.hedefse.Repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hedefse.R;
public class ThemeRepository {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_SELECTED_BACKGROUND_COLOR = "selected_background_color";
    private static final String KEY_SELECTED_LINEAR_LAYOUT_COLOR = "selected_linear_layout_color";

    private final SharedPreferences prefs;

    public ThemeRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setBackgroundColor(int colorResId) {
        prefs.edit().putInt(KEY_SELECTED_BACKGROUND_COLOR, colorResId).apply();
    }

    public int getBackgroundColor() {
        return prefs.getInt(KEY_SELECTED_BACKGROUND_COLOR, R.color.white); // Varsayılan renk.
    }

    public void setLinearLayoutColor(int colorResId) {
        prefs.edit().putInt(KEY_SELECTED_LINEAR_LAYOUT_COLOR, colorResId).apply();
    }

    public int getLinearLayoutColor() {
        return prefs.getInt(KEY_SELECTED_LINEAR_LAYOUT_COLOR, R.color.theme_linear_blue); // Varsayılan renk.
    }
}
