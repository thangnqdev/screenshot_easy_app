package com.example.screenshot_app.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.screenshot_app.MainActivity;
import com.example.screenshot_app.R;
import com.example.screenshot_app.model.Project;
import com.example.screenshot_app.view.repository.ProjectRepository;
import com.example.screenshot_app.view.adapter.ProjectAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListProjectActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    private ProjectAdapter adapter;
    public List<Project> projectList = new ArrayList<>();
    public ProjectRepository projectRepository;
    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_project);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProjectAdapter(projectList, project -> {
            Intent intent = new Intent(ListProjectActivity.this, MainActivity.class);
            intent.putExtra("PROJECT_ID", project.getId());
            intent.putExtra("PROJECT_TITLE", project.getTitle());
            Log.e("onCreate: ", project.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        projectRepository = new ProjectRepository();
        loadProjects();
        swipeRefreshLayout.setOnRefreshListener(this::loadProjects);
    }

    private void loadProjects() {
        swipeRefreshLayout.setRefreshing(true);
        projectRepository.fetchProjects(new ProjectRepository.ProjectCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Project> projects) {
                projectList.clear();
                projectList.addAll(projects);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("GraphQL", errorMessage);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
