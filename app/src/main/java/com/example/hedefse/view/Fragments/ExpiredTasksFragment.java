package com.example.hedefse.view.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hedefse.R;
import com.example.hedefse.view.adapter.TasksAdapter;
import com.example.hedefse.viewmodel.MainViewModel;
import com.example.hedefse.viewmodel.SuccessViewModel;
import com.example.hedefse.viewmodel.TasksViewModel;

import java.util.ArrayList;
public class ExpiredTasksFragment extends Fragment {
    private TasksViewModel tasksViewModel;
    private TasksAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expired_tasks, container, false);

        setupViewModels();
        setupRecyclerView(view);
        setupObservers();
        setupBackButton(view);

        return view;
    }

    private void setupViewModels() {
        ViewModelProvider.Factory factory = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication());
        tasksViewModel = new ViewModelProvider(requireActivity(), factory).get(TasksViewModel.class);
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.expired_tasks_rv_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new TasksAdapter(tasksViewModel, new MainViewModel(requireActivity().getApplication()), new SuccessViewModel(requireActivity().getApplication()));
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        tasksViewModel.getExpiredNotCompletedTodos().observe(getViewLifecycleOwner(), todoItems -> {
            if (todoItems != null) {
                Log.d("ExpiredTasksFragment", "Expired tasks size: " + todoItems.size());
                adapter.setTodoList(todoItems);
            } else {
                Log.d("ExpiredTasksFragment", "Expired tasks is null");
                adapter.setTodoList(new ArrayList<>());
            }
        });
    }

    private void setupBackButton(View view) {
        Button backButton = view.findViewById(R.id.expired_tasks_btn_back);
        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, new TasksFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}

