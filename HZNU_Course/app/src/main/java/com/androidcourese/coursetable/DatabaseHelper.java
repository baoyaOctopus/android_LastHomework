package com.androidcourese.coursetable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    private static final int MAIN_COURSES = 1;

    public final String CREATE_COURSES = "create table courses(" +
            "id integer primary key autoincrement," +
            "course_name text," +
            "teacher text," +
            "class_room text," +
            "day integer," +
            "class_start integer," +
            "class_end integer)";
    public final String CREATE_CREDITS = "create table credits(" +
            "id integer primary key autoincrement," +
            "credits_token integer," +
            "credits_untoken integer," +
            "class_first integer," +
            "class_second integer," +
            "class_third integer," +
            "class_end integer)";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COURSES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

