package com.jone.chat.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jone.chat.Constant;
import com.jone.chat.R;
import com.jone.chat.adapter.ChatListAdapter;
import com.jone.chat.application.App;
import com.jone.chat.bean.ChatMessage;
import com.jone.chat.bean.User;
import com.jone.chat.enums.MessageType;
import com.jone.chat.util.PhotoUtils;
import com.jone.chat.util.SystemUtil;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends FragmentActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener{
    private static boolean isAlive;

    private static User charUser = null;
    private ImageButton imBtnChatUserHead;
    private TextView txtChatUserName;
    private TextView txtChatUserIP;
    private Button btnClose;
    private ListView chat_list;
    private ChatListAdapter adapter;
    private EditText editInput;
    private Button btnSend;
    private CheckBox checkEmoji;
    private LinearLayout layoutCandidate;

    private CheckBox checkPhoto;

    private List<ChatMessage> msgList = new ArrayList<>();

    private BroadcastReceiver broadcastReceiver;

    public static boolean isIsAlive() {
        return isAlive;
    }

    public static User getCharUser() {
        return charUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAlive = true;
        setContentView(R.layout.activity_chat_room);
        if(getIntent() != null){
            charUser = getIntent().getParcelableExtra("fromUser");
            ChatMessage receiveMsg = (ChatMessage) getIntent().getSerializableExtra("receiveMsg");
            if(receiveMsg != null){
                msgList.add(receiveMsg);
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
        checkPhoto = (CheckBox) findViewById(R.id.checkPhoto);
        checkEmoji = (CheckBox) findViewById(R.id.checkEmoji);
        editInput = (EditText) findViewById(R.id.editInput);
        btnSend = (Button) findViewById(R.id.btnSend);
        layoutCandidate = (LinearLayout) findViewById(R.id.layoutCandidate);

        if(getCharUser() != null){
            txtChatUserName.setText(getCharUser().getUserName());
            txtChatUserIP.setText(getCharUser().getIp());
        }

        editInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmojiLayout(false);
                checkEmoji.setChecked(false);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatRoomActivity.this, SelectPicPopupWindowActivity.class);
                startActivityForResult(intent, PhotoUtils.GET_PHOTO_CODE);
            }
        });

        checkEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmojiLayout(checkEmoji.isChecked());
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editInput.getText().length() > 0){
                    String msg = editInput.getText().toString();
                    System.out.println("ssss: " + msg);
                    if(getCharUser() != null){
                        try {
                            App.getInstance().getCoreService().send(getCharUser(), msg);
                            msgList.add(new ChatMessage("我", msg));
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

    private void showEmojiLayout(boolean isShow){
        if(isShow){
            SystemUtil.hideKeyBoard(ChatRoomActivity.this);
            layoutCandidate.setVisibility(View.VISIBLE);
        }else {
            layoutCandidate.setVisibility(View.GONE);
        }
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
                        ChatMessage receiveMsg = (ChatMessage) intent.getSerializableExtra("receiveMsg");
                        if(fromUser != null && fromUser.getIp().equals(getCharUser().getIp())){ //发给自己的再更新列表
                            SystemUtil.vibrate(context);
                            msgList.add(receiveMsg);
                            adapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PhotoUtils.GET_PHOTO_CODE: //获取图片
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    if(bundle != null){
                        String imagePath = bundle.getString(Constant.IMAGE_PATH_KEY);
                        if(imagePath != null){
                            //todo 得到了图片的位置
                            try {
                                msgList.add(new ChatMessage("我", MessageType.PHOTO, imagePath));
                                adapter.notifyDataSetChanged();
                                App.getInstance().getCoreService().sendPhoto(charUser, imagePath);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        isAlive = false;
        charUser = null;
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

    @Override
    public void onEmojiconBackspaceClicked(View view) {
        EmojiconsFragment.backspace(editInput);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(editInput, emojicon);
    }
}
