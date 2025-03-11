package com.example.screenshot_app.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.screenshot_app.model.api.ApiToken;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "screenshot_app.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và cột
    private static final String TABLE_API_TOKEN = "api_token";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_BASE_URL = "base_url";
    private static final String COLUMN_TOKEN = "token";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_API_TOKEN + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BASE_URL + " TEXT, "
                + COLUMN_TOKEN + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);

        String INSERT_SAMPLE_DATA = "INSERT INTO " + TABLE_API_TOKEN + " ("
                + COLUMN_BASE_URL + ", "
                + COLUMN_TOKEN + ") VALUES "
                + "('cms.bolttech.space', 'KjzcGYUfUJQdLca7SEx2hpZkXe5STOwj')";
        db.execSQL(INSERT_SAMPLE_DATA);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_API_TOKEN);
        onCreate(db);
    }

    public long addApiToken(ApiToken apiToken) {
        SQLiteDatabase db = null;
        long id;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_BASE_URL, apiToken.getBaseUrl());
            values.put(COLUMN_TOKEN, apiToken.getToken());

            id = db.insertWithOnConflict(TABLE_API_TOKEN, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            if (db != null) db.close();
        }

        return id;
    }


    public List<ApiToken> getAllApiTokens() {
        List<ApiToken> tokenList = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.query(TABLE_API_TOKEN, null, null, null, null, null, null)) {

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String baseUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BASE_URL));
                    String token = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOKEN));

                    tokenList.add(new ApiToken(id, baseUrl, token));
                } while (cursor.moveToNext());
            }
        }

        return tokenList;
    }
    public boolean deleteApiToken(int id) {
        SQLiteDatabase db = null;
        int rowsDeleted;

        try {
            db = this.getWritableDatabase();
            rowsDeleted = db.delete(TABLE_API_TOKEN, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        } finally {
            if (db != null) db.close();
        }

        return rowsDeleted > 0;
    }

}
