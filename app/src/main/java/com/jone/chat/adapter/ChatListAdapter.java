package com.jone.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jone.chat.R;

import java.util.List;

/**
 * Created by jone on 2014/6/20.
 */
public class ChatListAdapter extends ArrayAdapter<String> {
    private LayoutInflater mInflater;
    public ChatListAdapter(Context context, List<String> objects) {
        super(context, R.layout.item_chat_list, objects);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_chat_list, null);
            holder = new ViewHolder();
            holder.txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtMsg.setText(getItem(position));
        return convertView;
    }

    static class ViewHolder{
        TextView txtMsg;
    }
}
