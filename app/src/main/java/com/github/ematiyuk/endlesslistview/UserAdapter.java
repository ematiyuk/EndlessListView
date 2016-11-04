package com.github.ematiyuk.endlesslistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {
    private ArrayList<User> mUsers;
    private LayoutInflater mTrackInflater;

    public UserAdapter(Context c, ArrayList<User> mUsers) {
        this.mUsers = mUsers;
        this.mTrackInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public User getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // create a ViewHolder reference
        ViewHolder holder;

        // check to see if the reused view is null or not, if is not null then reuse it
        if(convertView == null) {
            convertView = mTrackInflater.inflate(R.layout.list_item, parent, false);

            holder = new ViewHolder();

            // get views and save them in the view holder
            holder.tvUserData = (TextView) convertView.findViewById(R.id.list_item_text_view);

            // save the view holder on the cell view to get it back latter
            convertView.setTag(holder);
        } else {
            // the getTag() returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        User user = getItem(position);

        holder.tvUserData.setText(user.toString());

        return convertView;
    }

    public void addAll(ArrayList<User> usersToAdd) {
        mUsers.addAll(usersToAdd);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView tvUserData;
    }
}
