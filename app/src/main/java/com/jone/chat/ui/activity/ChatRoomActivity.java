package com.jone.chat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.jone.chat.Constant;
import com.jone.chat.R;
import com.jone.chat.application.App;
import com.jone.chat.bean.ChatMessage;
import com.jone.chat.bean.User;
import com.jone.chat.enums.MessageType;
import com.jone.chat.util.PhotoUtils;
import com.jone.chat.util.SystemUtil;
import com.jone.chat.util.VolleyUtil;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class ChatRoomActivity extends FragmentActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener{
    private static boolean isAlive;

    private static User charUser = null;
    private ImageButton imBtnChatUserHead;
    private TextView txtChatUserName;
    private TextView txtChatUserIP;
    private Button btnClose;
    private LinearLayout layoutChatList;


    private CheckBox checkType;
    private Button btnSpeaking;
    private EditText editInput;
    private CheckBox checkEmoji;
    private Button btnSend;
    private CheckBox checkAddMore;
    private LinearLayout layoutCandidate;

    private ImageButton imBtnPhoto;
    private RelativeLayout layoutAddMoreCandidate;

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
        initViews();
        if(getIntent() != null){
            charUser = getIntent().getParcelableExtra("fromUser");
            ChatMessage receiveMsg = (ChatMessage) getIntent().getSerializableExtra("receiveMsg");
            if(receiveMsg != null){
                addChatView(receiveMsg);
            }
        }
        bindBroadcast();
    }

    private void initViews(){
        imBtnChatUserHead = (ImageButton) findViewById(R.id.imBtnChatUserHead);
        txtChatUserName = (TextView) findViewById(R.id.txtChatUserName);
        txtChatUserIP = (TextView) findViewById(R.id.txtChatUserIP);
        btnClose = (Button) findViewById(R.id.btnClose);
        layoutChatList = (LinearLayout) findViewById(R.id.layoutChatList);

        checkType = (CheckBox) findViewById(R.id.checkType);
        btnSpeaking = (Button) findViewById(R.id.btnSpeaking);
        editInput = (EditText) findViewById(R.id.editInput);
        checkEmoji = (CheckBox) findViewById(R.id.checkEmoji);
        btnSend = (Button) findViewById(R.id.btnSend);
        checkAddMore = (CheckBox) findViewById(R.id.checkAddMore);
        layoutCandidate = (LinearLayout) findViewById(R.id.layoutCandidate);

        imBtnPhoto = (ImageButton) findViewById(R.id.imBtnPhoto);
        layoutAddMoreCandidate = (RelativeLayout) findViewById(R.id.layoutAddMoreCandidate);

        if(getCharUser() != null){
            txtChatUserName.setText(getCharUser().getUserName());
            txtChatUserIP.setText(getCharUser().getIp());
        }
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkType.isChecked()){
                    checkEmoji.setChecked(false);
                    checkAddMore.setChecked(false);
                    btnSpeaking.setVisibility(View.VISIBLE);
                    editInput.setVisibility(View.GONE);
                    checkEmoji.setVisibility(View.GONE);
                    checkAddMore.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.GONE);
                }else {
                    btnSpeaking.setVisibility(View.GONE);
                    editInput.setVisibility(View.VISIBLE);
                    checkEmoji.setVisibility(View.VISIBLE);
                    if(editInput.getText().length() > 0){
                        checkAddMore.setVisibility(View.GONE);
                        btnSend.setVisibility(View.VISIBLE);
                    }else {
                        checkAddMore.setVisibility(View.VISIBLE);
                        btnSend.setVisibility(View.GONE);
                    }
                }
            }
        });

        checkEmoji.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showEmojiLayout(b);
                if(b){
                    if(checkAddMore.isChecked()){
                        checkAddMore.setChecked(false);
                    }
                }
            }
        });

        editInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmoji.setChecked(false);
            }
        });
        editInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() > 0){
                    btnSend.setVisibility(View.VISIBLE);
                    checkAddMore.setVisibility(View.GONE);
                }else {
                    btnSend.setVisibility(View.GONE);
                    checkAddMore.setVisibility(View.VISIBLE);
                }
            }
        });

        checkAddMore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    layoutAddMoreCandidate.setVisibility(View.VISIBLE);
                    if(checkEmoji.isChecked()){
                        checkEmoji.setChecked(false);
                    }
                }else {
                    layoutAddMoreCandidate.setVisibility(View.GONE);
                }
            }
        });



        imBtnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatRoomActivity.this, SelectPicPopupWindowActivity.class);
                startActivityForResult(intent, PhotoUtils.GET_PHOTO_CODE);
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
                            addChatView(new ChatMessage("我", msg));
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

    private void addChatView(ChatMessage chatMessage){
        LinearLayout chatGroupView = new LinearLayout(ChatRoomActivity.this);
        LinearLayout chatView = new LinearLayout(ChatRoomActivity.this);
        chatView.setMinimumWidth(45);
        chatView.setMinimumHeight(45);
        chatView.setPadding(8, 8, 8, 8);
        chatView.setGravity(Gravity.CENTER);
        if(chatMessage.getFromUserName().equals("我")){ //后续优化判断条件
            chatView.setBackgroundResource(R.drawable.bg_message_from_me_list_item);
            chatGroupView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        }else {
            chatView.setBackgroundResource(R.drawable.bg_message_list_item);
            chatGroupView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }
        if(chatMessage.getMessageType().toString().equals(MessageType.PHOTO.toString())){
            //显示图片
            if(chatMessage.getFromUserName().equals("我")){
                ImageView imageView = new ImageView(ChatRoomActivity.this);
                imageView.setImageBitmap(PhotoUtils.getImageThumbnail(chatMessage.getContent(), 100, 100));
                chatView.addView(imageView);
            }else {
                NetworkImageView imageView = new NetworkImageView(ChatRoomActivity.this);
                VolleyUtil.showImageByNetworkImageView(ChatRoomActivity.this, imageView, "http://" + chatMessage.getContent());
                chatView.addView(imageView, new ViewGroup.LayoutParams(100, 100));
            }

        }else {
            //显示文字
            EmojiconTextView textView = new EmojiconTextView(ChatRoomActivity.this);
            textView.setText(chatMessage.getContent());
            chatView.addView(textView);
        }
        chatGroupView.addView(chatView);
        layoutChatList.addView(chatGroupView);
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
                            addChatView(receiveMsg);
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
                                addChatView(new ChatMessage("我", MessageType.PHOTO, imagePath));
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
