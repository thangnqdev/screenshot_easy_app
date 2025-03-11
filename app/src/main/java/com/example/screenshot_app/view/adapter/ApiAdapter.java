package com.example.screenshot_app.view.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.screenshot_app.MainActivity;
import com.example.screenshot_app.R;
import com.example.screenshot_app.model.api.ApiToken;
import com.example.screenshot_app.model.database.DbHelper;
import com.example.screenshot_app.controller.client.ApiConfigManager;

import java.util.List;

public class ApiAdapter extends RecyclerView.Adapter<ApiAdapter.ViewHolder> {
    private final List<ApiToken> apiList;
    private final DbHelper dbHelper;
    private final Context context;

    public ApiAdapter(List<ApiToken> apiList, DbHelper dbHelper, Context context) {
        this.apiList = apiList;
        this.dbHelper = dbHelper;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_api, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApiToken apiToken = apiList.get(position);
        holder.textApi.setText(apiToken.getBaseUrl());
        holder.textToken.setText(apiToken.getToken());
        holder.lnItemApi.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext()).setTitle("Xác nhận xóa").setMessage("Bạn có chắc chắn muốn xóa không?").setPositiveButton("Có", (dialog, which) -> {
                // Nếu chọn "Có", xóa API token khỏi database và cập nhật RecyclerView
                boolean isDeleted = dbHelper.deleteApiToken(apiToken.getId());
                if (isDeleted) {
                    apiList.remove(position); // Xóa khỏi danh sách
                    notifyItemRemoved(position); // Cập nhật RecyclerView
                }
            }).setNegativeButton("Không", null).show();
            return true;
        });
        holder.lnItemApi.setOnClickListener(v -> {
            ApiConfigManager.INSTANCE.saveApiConfig(context, holder.textApi.getText().toString(), holder.textToken.getText().toString());
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return apiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textApi, textToken;
        LinearLayout lnItemApi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textApi = itemView.findViewById(R.id.textApi);
            textToken = itemView.findViewById(R.id.textToken);
            lnItemApi = itemView.findViewById(R.id.lnItemApi);
        }

    }
}
