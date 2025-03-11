package com.example.screenshot_app.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.screenshot_app.R;
import com.example.screenshot_app.model.api.ApiToken;
import com.example.screenshot_app.model.database.DbHelper;
import com.example.screenshot_app.view.adapter.ApiAdapter;
import com.example.screenshot_app.view.dialog.ApiConfigDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

public class OptionActivity extends BaseActivity {
    RecyclerView recyclerViewOption;
    FloatingActionButton floatingActionButton;
    ApiAdapter apiAdapter;
    List<ApiToken> apiList;
    DbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        setupToolbar(R.id.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Cài đặt API/TOKEN");
        recyclerViewOption = findViewById(R.id.recyclerViewOption);
        floatingActionButton = findViewById(R.id.floatButtonAdd);
        dbHelper = new DbHelper(this);
        apiList = dbHelper.getAllApiTokens();
        recyclerViewOption.setLayoutManager(new LinearLayoutManager(this));
        apiAdapter = new ApiAdapter(apiList, dbHelper, this);
        recyclerViewOption.setAdapter(apiAdapter);
        floatingActionButton.setOnClickListener(v -> {
            new ApiConfigDialog(this, (baseUrl, token) -> {
                if (!baseUrl.isEmpty() && !token.isEmpty()) {
                    ApiToken apiToken = new ApiToken(0, baseUrl, token);
                    long id = dbHelper.addApiToken(apiToken);
                    if (id != -1) {
                        Log.e("dbInsert", "insert successful" + id);
                        loadApiTokens();

                    }else{
                        Log.e("dbInsert", "insert fail");
                    }
                }else{
                    Log.e("dbInsert", "Base Url hoặc token rỗng");
                }
                return null;
            }).show();
        });
        loadApiTokens();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadApiTokens() {
        // Lấy lại danh sách từ database
        apiList.clear(); // Xóa danh sách cũ
        apiList.addAll(dbHelper.getAllApiTokens());
        apiAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
    }
}