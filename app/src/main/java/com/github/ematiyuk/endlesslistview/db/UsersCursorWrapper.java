package com.github.ematiyuk.endlesslistview.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.github.ematiyuk.endlesslistview.User;

public class UsersCursorWrapper extends CursorWrapper {

    public UsersCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getUser() {
        String firstName = getString(getColumnIndex(UsersDbSchema.UsersTable.Cols.FIRST_NAME));
        String lastName = getString(getColumnIndex(UsersDbSchema.UsersTable.Cols.LAST_NAME));

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);

        return user;
    }
}
