package com.jone.chat.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jone.chat.R;
import com.jone.chat.bean.ChatMessage;
import com.jone.chat.enums.MessageType;
import com.jone.chat.util.PhotoUtils;

import java.util.List;

/**
 * Created by jone on 2014/6/20.
 */
public class ChatListAdapter extends ArrayAdapter<ChatMessage> {
    private LayoutInflater mInflater;
    public ChatListAdapter(Context context, List<ChatMessage> objects) {
        super(context, R.layout.item_chat_list, objects);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_chat_list, null);
            holder = new ViewHolder();
            holder.layoutMsg = (LinearLayout) convertView.findViewById(R.id.layoutMsg);
            holder.imMsg = (ImageView) convertView.findViewById(R.id.imMsg);
            holder.txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ChatMessage chatMessage = getItem(position);
        if(chatMessage.getFromUserName().equals("我")){ //后续优化判断条件
            holder.layoutMsg.setBackgroundResource(R.drawable.bg_message_from_me_list_item);
            ((LinearLayout)convertView).setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        }else {
            holder.layoutMsg.setBackgroundResource(R.drawable.bg_message_list_item);
            ((LinearLayout)convertView).setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }
        if(chatMessage.getMessageType().toString().equals(MessageType.PHOTO.toString())){
            holder.imMsg.setVisibility(View.VISIBLE);
            holder.imMsg.setImageBitmap(PhotoUtils.getImageThumbnail(chatMessage.getContent(), 100, 100));
        }else {
            holder.txtMsg.setVisibility(View.VISIBLE);
            holder.txtMsg.setText(chatMessage.getContent());
        }
        return convertView;
    }

    static class ViewHolder{
        private LinearLayout layoutMsg;
        ImageView imMsg;
        TextView txtMsg;
    }
}
