package com.example.hedefse.view.adapter;

import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hedefse.R;
import com.example.hedefse.view.Fragments.TaskDetailFragment;
import com.example.hedefse.model.entities.TodoItem;
import com.example.hedefse.viewmodel.MainViewModel;
import com.example.hedefse.viewmodel.SuccessViewModel;
import com.example.hedefse.viewmodel.TasksViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TodoViewHolder> {

    private List<TodoItem> todoList = new ArrayList<>();
    private final TasksViewModel tasksViewModel;
    private final MainViewModel mainViewModel;
    private final SuccessViewModel successViewModel;

    public TasksAdapter(TasksViewModel tasksViewModel, MainViewModel mainViewModel, SuccessViewModel successViewModel) {
        this.tasksViewModel = tasksViewModel;
        this.mainViewModel = mainViewModel;
        this.successViewModel = successViewModel;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tasks, parent, false);
        return new TodoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoItem currentItem = todoList.get(position);

        holder.textViewTitle.setText(currentItem.getTitle());

        String startDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(currentItem.getStartDate()));
        String endDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(currentItem.getEndDate()));
        holder.textViewStartDate.setText(startDate);
        holder.textViewEndDate.setText(endDate);

        holder.checkBoxCompleted.setOnCheckedChangeListener(null);
        holder.checkBoxCompleted.setChecked(currentItem.isCompleted());

        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckboxChecked(holder, currentItem, isChecked));

        holder.itemView.setOnClickListener(v -> navigateToTaskDetailFragment(holder, currentItem));

        observeBackgroundColor(holder);
    }

    private void onCheckboxChecked(TodoViewHolder holder, TodoItem currentItem, boolean isChecked) {
        currentItem.setCompleted(isChecked);
        tasksViewModel.update(currentItem);
        if (isChecked) {
            successViewModel.completeTask(); // Görev tamamlandığında başarıyı güncelle
        } else {
            successViewModel.completeTaskUndo(); // Görev işareti kaldırıldığında tamamlanma sayısını azalt
        }
    }

    private void navigateToTaskDetailFragment(TodoViewHolder holder, TodoItem currentItem) {
        tasksViewModel.setSelectedTodoItem(currentItem);
        FragmentTransaction transaction = ((AppCompatActivity) holder.itemView.getContext()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, new TaskDetailFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void observeBackgroundColor(TodoViewHolder holder) {
        mainViewModel.getBackgroundColorLiveData().observe((LifecycleOwner) holder.itemView.getContext(), colorResId -> {
            if (colorResId != null) {
                // Eğer renk maviyse, arka plan rengini ayarlayalım
                GradientDrawable border = new GradientDrawable();
                border.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.theme_adapter_blue)); // İç arka plan rengini ayarlayın
                border.setStroke(2, ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black)); // Kenarlık genişliği ve rengi

                holder.linearLayout.setBackground(border);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void setTodoList(List<TodoItem> todoList) {
        this.todoList.clear();
        if (todoList != null) {
            Log.d("TasksAdapter", "Todo list size: " + todoList.size());
            this.todoList.addAll(todoList);
        } else {
            Log.d("TasksAdapter", "Todo list is null");
        }
        notifyDataSetChanged();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final CheckBox checkBoxCompleted;
        private final TextView textViewStartDate;
        private final TextView textViewEndDate;
        private final LinearLayout linearLayout;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.itemTasks_tv_taskTitle);
            checkBoxCompleted = itemView.findViewById(R.id.itemTasks_cb_completed);
            textViewStartDate = itemView.findViewById(R.id.itemTasks_tv_startDate);
            textViewEndDate = itemView.findViewById(R.id.itemTasks_tv_endDate);
            linearLayout = itemView.findViewById(R.id.itemTasks_cv_cardView);
        }
    }
}
