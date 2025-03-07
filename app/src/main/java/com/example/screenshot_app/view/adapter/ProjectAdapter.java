package com.example.screenshot_app.view.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.screenshot_app.R;
import com.example.screenshot_app.controller.interfaces.EventItemClickListenerInterface;
import com.example.screenshot_app.model.Project;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    private final List<Project> projectList;
    public EventItemClickListenerInterface listener;


    public ProjectAdapter(List<Project> projectList, EventItemClickListenerInterface listener) {
        this.projectList = projectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.bind(project, listener);
        holder.textTitle.setText("Project: " + project.getTitle());
        holder.textTitle.setTextColor(Color.BLACK);
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textId;
        LinearLayout lnItemProject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textId = itemView.findViewById(R.id.textId);
            lnItemProject = itemView.findViewById(R.id.lnItemProject);
        }

        public void bind(Project project, EventItemClickListenerInterface listener) {
            lnItemProject.setOnClickListener(v -> listener.onItemClick(project));
        }
    }
}
