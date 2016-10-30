package com.github.ematiyuk.endlesslistview.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsersBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "usersBase.db";

    public UsersBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + UsersDbSchema.UsersTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                UsersDbSchema.UsersTable.Cols.FIRST_NAME + ", " +
                UsersDbSchema.UsersTable.Cols.LAST_NAME +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
