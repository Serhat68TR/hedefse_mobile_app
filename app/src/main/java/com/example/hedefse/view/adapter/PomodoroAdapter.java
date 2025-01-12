package com.example.hedefse.view.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hedefse.R;
import com.example.hedefse.model.entities.PomodoroItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PomodoroAdapter extends RecyclerView.Adapter<PomodoroAdapter.PomodoroViewHolder> {

    private List<PomodoroItem> items = new ArrayList<>();
    private final Map<String, Integer> badgeImageMap;

    public PomodoroAdapter() {
        // Rozet isimlerine karşılık gelen resimleri tanımlayın
        badgeImageMap = new HashMap<>();
        badgeImageMap.put("Şampiyon", R.drawable.award);
        badgeImageMap.put("Elmas", R.drawable.quality);
        badgeImageMap.put("Altın", R.drawable.gold_medal);
        badgeImageMap.put("Gümüş", R.drawable.silvermedal);
        badgeImageMap.put("Bronz", R.drawable.bronzemedal);
    }

    @NonNull
    @Override
    public PomodoroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pomodoro_badge, parent, false);
        return new PomodoroViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PomodoroViewHolder holder, int position) {
        PomodoroItem item = items.get(position);
        if (item != null) {
            holder.textViewDate.setText(item.getDate()); // Ödül alındığı tarih
            holder.textViewBadge.setText(item.getBadge()); // Rozet adı

            // Rozet resmini ayarla
            Integer imageResId = badgeImageMap.get(item.getBadge());
            if (imageResId != null) {
                holder.imageViewBadge.setImageResource(imageResId);
            } else {
                Log.e("PomodoroAdapter", "Rozet resmi bulunamadı: " + item.getBadge());
                holder.imageViewBadge.setImageResource(R.drawable.pomodoro); // Varsayılan bir resim
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<PomodoroItem> items) {
        this.items = new ArrayList<>(items); // Immutable bir liste kullanarak atomik olarak güncelle
        notifyDataSetChanged();
    }
    class PomodoroViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDate;
        private final TextView textViewBadge;
        private final ImageView imageViewBadge;

        public PomodoroViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.itemPomodoro_tv_date);
            textViewBadge = itemView.findViewById(R.id.itemPomodoro_tv_badge);
            imageViewBadge = itemView.findViewById(R.id.itemPomodoro_iv_badge);
        }
    }
}
