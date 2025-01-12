package com.example.hedefse.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hedefse.R;
import com.example.hedefse.model.entities.TaskBadgeItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskBadgeItem> items = new ArrayList<>();
    private final Map<String, Integer> badgeImageMap;

    public TaskAdapter() {
        badgeImageMap = new HashMap<>();
        badgeImageMap.put("Şampiyon", R.drawable.award);
        badgeImageMap.put("Elmas", R.drawable.quality);
        badgeImageMap.put("Altın", R.drawable.gold_medal);
        badgeImageMap.put("Gümüş", R.drawable.silvermedal);
        badgeImageMap.put("Bronz", R.drawable.bronzemedal);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task_badge, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskBadgeItem item = items.get(position);
        if (item != null) {
            holder.textViewBadge.setText(item.getBadge());
            holder.textViewDate.setText(item.getDate());

            Integer imageResId = badgeImageMap.get(item.getBadge());
            if (imageResId != null) {
                holder.imageViewBadge.setImageResource(imageResId);
            } else {
                holder.imageViewBadge.setImageResource(R.drawable.pomodoro);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<TaskBadgeItem> items) {
        this.items = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewBadge;
        private final TextView textViewDate;
        private final ImageView imageViewBadge;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBadge = itemView.findViewById(R.id.itemTask_tv_badge);
            textViewDate = itemView.findViewById(R.id.itemTask_tv_date);
            imageViewBadge = itemView.findViewById(R.id.itemTask_iv_badge);
        }
    }
}
