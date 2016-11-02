package com.github.ematiyuk.endlesslistview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<User> mAdapter;
    private ListView mListView;

    private ProgressDialog mProgressDialog;

    private static final int DEFAULT_MAX_ELEMENTS_IN_MEMORY = 50;
    private int mMaxElementsInMemory = DEFAULT_MAX_ELEMENTS_IN_MEMORY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int offset) {
                // triggered only when new data needs to be appended to the list
                return loadNextData(offset);
            }
        });

        new AsynchDatabaseFiller().execute();
    }

    @Override
    protected void onDestroy() {
        UserStorage.get(this).destroySelf();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionEditCachedElements:
                showEditCachedElementsDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditCachedElementsDialog() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View numPickerView = inflater.inflate(R.layout.cached_elements_number_picker, null);

        final TextView elementsTextView = (TextView) numPickerView.findViewById(R.id.alertDialogElementsText);
        final NumberPicker numberPicker = (NumberPicker) numPickerView.findViewById(R.id.cachedElementsNumberPicker);

        numberPicker.setMaxValue(1000);
        numberPicker.setMinValue(mMaxElementsInMemory);
        numberPicker.setValue(mMaxElementsInMemory);

        // disable soft keyboard autoshowing
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS);

        // set respective plural string on NumberPicker value changing
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker nmPicker, int oldVal, int newVal) {
                elementsTextView.setText(getElementsPluralString(newVal));
            }
        });

        // set respective plural string (depends on mMaxElementsInMemory value)
        // before AlertDialog showing
        elementsTextView.setText(getElementsPluralString(mMaxElementsInMemory));

        // create and config AlertDialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.change_cached_elements_title_string)
                .setView(numPickerView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mMaxElementsInMemory = numberPicker.getValue();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }

    private String getElementsPluralString(int quantity) {
        return getResources().getQuantityString(R.plurals.dialog_elements_plural, quantity);
    }

    /**
     * Appends fetched data into the adapter.
     *
     * @param offset means begin index
     * @return <code>true</code> if data has been loaded, otherwise - <code>false</code>
     */
    public boolean loadNextData(int offset) {
        ArrayList<User> users = UserStorage.get(this).getUsersRange(offset,
                mMaxElementsInMemory - mAdapter.getCount());
        if (users == null || users.isEmpty()) return false; // more data isn't being loaded

        mAdapter.addAll(users); // append previously fetched data into the adapter
        mAdapter.notifyDataSetChanged();

        return true; // more data is actually being loaded
    }

    private class AsynchDatabaseFiller extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage(getString(R.string.fill_db_progress_msg));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            UserStorage.get(MainActivity.this).fillUsersTable();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // fetch the first set of items
            ArrayList<User> users = UserStorage.get(MainActivity.this)
                    .getUsersRange(0, mMaxElementsInMemory);

            mAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item, users);
            mListView.setAdapter(mAdapter);

            mProgressDialog.dismiss();
        }
    }
}
