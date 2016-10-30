package com.github.ematiyuk.endlesslistview;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.ematiyuk.endlesslistview.db.UsersBaseHelper;
import com.github.ematiyuk.endlesslistview.db.UsersCursorWrapper;
import com.github.ematiyuk.endlesslistview.db.UsersDbSchema.UsersTable;

import java.util.ArrayList;

public class UserStorage {
    private static UserStorage sUserStorage;
    private Context mAppContext;
    private SQLiteDatabase mDatabase;

    private UserStorage(Context appContext) {
        this.mAppContext = appContext;
        mDatabase = new UsersBaseHelper(mAppContext).getWritableDatabase();
    }

    public static UserStorage get(Context c) {
        if (sUserStorage == null) {
            sUserStorage = new UserStorage(c.getApplicationContext());
        }
        return sUserStorage;
    }

    public void fillUsersTable() {
        if (getTotalUsersCount() < 1) { // if "users" table is empty
            // fill "users" table with dummy data
            for (int i = 0; i < 1000; i++) {
                String firstName = "first_name" + i;
                String lastName = "last_name" + i;

                ContentValues values = new ContentValues();
                values.put(UsersTable.Cols.FIRST_NAME, firstName);
                values.put(UsersTable.Cols.LAST_NAME, lastName);
                mDatabase.insert(UsersTable.NAME, null, values);
            }
        }
    }

    public int getTotalUsersCount() {
        String count = "SELECT count(*) FROM " + UsersTable.NAME;
        Cursor cursor = mDatabase.rawQuery(count, null);
        try {
            cursor.moveToFirst();
            return cursor.getInt(0);
        } finally {
            cursor.close();
        }
    }

    /**
     * Retrieves specified range of users from db.
     *
     * @param begin denotes begin index
     * @param offset items count starting from begin index
     * @return a list of users fetched from db
     */
    public ArrayList<User> getUsersRange(int begin, int offset) {
        final String selectRangeQuery = "SELECT * FROM " + UsersTable.NAME
                + " ORDER BY _id LIMIT " + begin + ", " + offset;

        // an alternative
//        String selectRangeQuery = "SELECT * FROM " + UsersTable.NAME
//                + " WHERE _id > " + begin
//                + " ORDER BY _id LIMIT " + offset;
        UsersCursorWrapper cursor = new UsersCursorWrapper(mDatabase.rawQuery(selectRangeQuery, null));
        ArrayList<User> users = new ArrayList<>();

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                users.add(cursor.getUser());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return users;
    }

    public void destroySelf() {
        mDatabase.close();
        sUserStorage = null;
    }
}
