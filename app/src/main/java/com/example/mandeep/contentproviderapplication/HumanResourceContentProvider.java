package com.example.mandeep.contentproviderapplication;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Mandeep on 13-06-2016.
 */
public class HumanResourceContentProvider extends ContentProvider {

    private SQLiteDatabase sqLiteDatabase;
    private static final String DATABASE_NAME = "human_resource";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "employee";

    @Override
    public boolean onCreate() {

        Context context = getContext();
        SqliteHelper sqliteHelper = new SqliteHelper(context);
        sqLiteDatabase = sqliteHelper.getWritableDatabase();

        return (sqLiteDatabase != null);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(TABLE_NAME);
        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, null);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        // Add a new employee record

        long row_id = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if (row_id > 0) {
            Uri uri_n = ContentUris.withAppendedId(uri, row_id);
            getContext().getContentResolver().notifyChange(uri_n, null);
            return uri_n;

        }
        throw new SQLException("Unable to add a new employee record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int num_of_rows_deleted = sqLiteDatabase.delete(TABLE_NAME, selection, selectionArgs);

        return num_of_rows_deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int num_of_rows_affected = sqLiteDatabase.update(TABLE_NAME, values, selection, selectionArgs);

        return num_of_rows_affected;
    }

    // Inner helper class that does the actual chores of dealing with the sqlite database
    private static class SqliteHelper extends SQLiteOpenHelper {

        SqliteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String sql = "CREATE TABLE " + TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " age INTEGER NOT NULL);";
            sqLiteDatabase.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
