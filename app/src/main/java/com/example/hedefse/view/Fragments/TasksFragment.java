package com.example.hedefse.view.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hedefse.R;
import com.example.hedefse.view.adapter.TasksAdapter;
import com.example.hedefse.viewmodel.MainViewModel;
import com.example.hedefse.viewmodel.SuccessViewModel;
import com.example.hedefse.viewmodel.TasksViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
public class TasksFragment extends Fragment {

    private TasksAdapter adapter;
    private TasksViewModel tasksViewModel;
    private MainViewModel mainViewModel;
    private SuccessViewModel successViewModel;
    private Handler handler;
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        setupViewModels();
        setupRecyclerView(view);
        setupObservers(view);
        setupListeners(view);
        setupAutoRefresh();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.tasks_rv_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new TasksAdapter(tasksViewModel, mainViewModel, successViewModel);
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModels() {
        ViewModelProvider.Factory factory = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication());
        tasksViewModel = new ViewModelProvider(requireActivity(), factory).get(TasksViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity(), factory).get(MainViewModel.class);
        successViewModel = new ViewModelProvider(requireActivity(), factory).get(SuccessViewModel.class);
    }

    private void setupObservers(View view) {
        TextView tasksTitleTextView = view.findViewById(R.id.tasks_tv_tasksTitle);
        LinearLayout linearLayout = view.findViewById(R.id.tasks_linear_tasksHeader);

        mainViewModel.getLinearLayoutColorLiveData().observe(getViewLifecycleOwner(), colorResId -> {
            if (colorResId != null) {
                linearLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), colorResId));
            }
        });

        tasksViewModel.getNotCompletedTodos().observe(getViewLifecycleOwner(), todoItems -> {
            adapter.setTodoList(todoItems);
            tasksTitleTextView.setText("Bekleyen Görevler");
        });
    }

    private void setupListeners(View view) {
        view.findViewById(R.id.tasks_fab_addTask).setOnClickListener(v -> navigateToNewTaskFragment());

        view.findViewById(R.id.tasks_btn_showPending).setOnClickListener(v -> showPendingTasks(view));

        view.findViewById(R.id.tasks_btn_showCompleted).setOnClickListener(v -> showCompletedTasks(view));

        view.findViewById(R.id.tasks_btn_showExpired).setOnClickListener(v -> {
            markTasksAsExpired();
            navigateToExpiredTasksFragment();
        });

        addAnimationToButton(view.findViewById(R.id.tasks_btn_showCompleted));
        addAnimationToButton(view.findViewById(R.id.tasks_btn_showPending));
        addAnimationToButton(view.findViewById(R.id.tasks_btn_showExpired));
    }

    private void setupAutoRefresh() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                tasksViewModel.markTasksAsExpired();
                handler.postDelayed(this, 10000); // 10 saniye aralıklarla kontrol et
            }
        };
        handler.post(runnable);
    }

    private void markTasksAsExpired() {
        tasksViewModel.markTasksAsExpired();
    }

    private void navigateToNewTaskFragment() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, new NewTaskFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToExpiredTasksFragment() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, new ExpiredTasksFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showPendingTasks(View view) {
        tasksViewModel.getNotCompletedTodos().observe(getViewLifecycleOwner(), todoItems -> {
            adapter.setTodoList(todoItems);
            updateTasksTitle(view, "Bekleyen Görevler");
        });
    }

    private void showCompletedTasks(View view) {
        tasksViewModel.getCompletedTodos().observe(getViewLifecycleOwner(), todoItems -> {
            adapter.setTodoList(todoItems);
            updateTasksTitle(view, "Tamamlanmış Görevler");
        });
    }

    private void updateTasksTitle(View view, String title) {
        TextView tasksTitleTextView = view.findViewById(R.id.tasks_tv_tasksTitle);
        tasksTitleTextView.setText(title);
    }

    private void addAnimationToButton(View button) {
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_down));
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_up));
            }
            return false;
        });
    }
}


