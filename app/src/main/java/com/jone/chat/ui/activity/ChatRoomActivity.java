package com.jone.chat.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jone.chat.ChatMainActivity;
import com.jone.chat.Constant;
import com.jone.chat.R;
import com.jone.chat.adapter.ChatListAdapter;
import com.jone.chat.application.App;
import com.jone.chat.bean.User;
import com.jone.chat.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends Activity {

    private User charUser;
    private ImageButton imBtnChatUserHead;
    private TextView txtChatUserName;
    private TextView txtChatUserIP;
    private Button btnClose;
    private ListView chat_list;
    private ChatListAdapter adapter;
    private EditText editInput;
    private Button btnSend;

    private List<String> msgList = new ArrayList<>();

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        if(getIntent() != null){
            charUser = getIntent().getParcelableExtra("fromUser");
            String receiveMsg = getIntent().getStringExtra("receiveMsg");
            if(receiveMsg != null){
                msgList.add(charUser.getUserName() + "说: " + receiveMsg);
            }
        }
        initViews();
        bindBroadcast();
    }

    private void initViews(){
        imBtnChatUserHead = (ImageButton) findViewById(R.id.imBtnChatUserHead);
        txtChatUserName = (TextView) findViewById(R.id.txtChatUserName);
        txtChatUserIP = (TextView) findViewById(R.id.txtChatUserIP);
        btnClose = (Button) findViewById(R.id.btnClose);
        chat_list = (ListView) findViewById(R.id.chat_list);
        adapter = new ChatListAdapter(this, msgList);
        chat_list.setAdapter(adapter);
        editInput = (EditText) findViewById(R.id.editInput);
        btnSend = (Button) findViewById(R.id.btnSend);

        if(charUser != null){
            txtChatUserName.setText(charUser.getUserName());
            txtChatUserIP.setText(charUser.getIp());
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editInput.getText().length() > 0){
                    String msg = editInput.getText().toString();
                    if(charUser != null){
                        try {
                            App.getInstance().getCoreService().send(charUser, msg);
                            msgList.add("我说: " + msg);
                            adapter.notifyDataSetChanged();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }finally {
                            editInput.getText().clear();
                        }
                    }

                }
            }
        });
    }

    private void bindBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.BROADCAST_RECEIVE_MSG_ACTION); //收到信息
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("action: " + intent.getAction());
                String action = intent.getAction();
                switch (action){
                    case Constant.BROADCAST_RECEIVE_MSG_ACTION:
                        User fromUser = intent.getParcelableExtra("fromUser");
                        String receiveMsg = intent.getStringExtra("receiveMsg");
                        if(fromUser != null && fromUser.getIp().equals(charUser.getIp())){ //发给自己的再更新列表
                            SystemUtil.vibrate(context);
                            msgList.add(fromUser.getUserName() + "说: " + receiveMsg);
                            adapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
