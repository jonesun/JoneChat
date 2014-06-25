package com.jone.chat.ui.activity;

import android.app.ActionBar;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.jone.chat.Constant;
import com.jone.chat.R;
import com.jone.chat.application.App;
import com.jone.chat.bean.ChatMessage;
import com.jone.chat.bean.User;
import com.jone.chat.enums.MessageType;
import com.jone.chat.util.DownloadManagerUtil;
import com.jone.chat.util.PhotoUtils;
import com.jone.chat.util.SystemUtil;
import com.jone.chat.util.VolleyUtil;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.File;
import java.io.IOException;

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

    private MediaRecorder mediaRecorder;
    private boolean isRecording;
    private String outputPath;

    private MediaPlayer mediaPlayer;

    public static boolean isIsAlive() {
        return isAlive;
    }

    public static User getCharUser() {
        return charUser;
    }

    private static final int refresh_voice_ui_what = 0001;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case refresh_voice_ui_what:
                    ImageButton imageButton = (ImageButton) msg.obj;
                    imageButton.setEnabled(true);
                    imageButton.setImageResource(R.drawable.ic_menu_play_clip);
                    break;
            }
        }
    };

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
            if(charUser != null){
                txtChatUserName.setText(getCharUser().getUserName());
                txtChatUserIP.setText(getCharUser().getIp());
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
                checkAddMore.setChecked(false);
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
                    SystemUtil.hideKeyBoard(ChatRoomActivity.this);
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

        btnSpeaking.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按住事件发生后执行代码的区域
                        System.out.println("按住,开始录音");
                        startRecorder();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动事件发生后执行代码的区域
                        break;
                    case MotionEvent.ACTION_UP:
                        //松开事件发生后执行代码的区域
                        System.out.println("松开,发送语音文件");
                        stopRecorder();
                        addChatView(new ChatMessage("我", MessageType.VOICE, outputPath));
                        //发送给对方
                        try {
                            App.getInstance().getCoreService().sendVoice(charUser, outputPath);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void startRecorder(){
        try{
            outputPath = App.getVOICE_PATH() + File.separator + "to" +charUser.getUserName() +System.currentTimeMillis() + ".amr";
            System.out.println(outputPath);
            File file = new File(outputPath);
            mediaRecorder = new MediaRecorder();
            //设置音频录入源
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置录制音频的输出格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            //设置音频的编码格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //设置录制音频文件输出文件路径
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mediaRecorder, int i, int i2) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    isRecording = false;
                }
            });

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            Toast.makeText(ChatRoomActivity.this, "开始录音", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void stopRecorder(){
        if(isRecording){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        }
    }

    private void startPlay(String filePath){
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            System.out.println("prepare() failed"  + e.getMessage());
        }
    }

    private void stopPlay(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
                imageView.setTag(chatMessage.getContent());
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ChatRoomActivity.this, ImageShowerActivity.class);
                        intent.putExtra(Constant.IMAGE_PATH_KEY, view.getTag().toString());
                        startActivity(intent);
                    }
                });
                chatView.addView(imageView, new ViewGroup.LayoutParams(100, 100));
            }else {
                NetworkImageView imageView = new NetworkImageView(ChatRoomActivity.this);
                VolleyUtil.showImageByNetworkImageView(ChatRoomActivity.this, imageView, "http://" + chatMessage.getContent());
                imageView.setTag(chatMessage.getContent());
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ChatRoomActivity.this, ImageShowerActivity.class);
                        intent.putExtra(Constant.IMAGE_PATH_KEY, view.getTag().toString());
                        intent.putExtra("isNetworkPhoto", true);
                        startActivity(intent);
                    }
                });
                chatView.addView(imageView, new ViewGroup.LayoutParams(100, 100));
            }

        }else if(chatMessage.getMessageType().toString().equals(MessageType.VOICE.toString())){
            //显示语音按钮点击播放
            final ImageButton imageButton = new ImageButton(ChatRoomActivity.this);
            imageButton.setImageResource(R.drawable.ic_menu_play_clip);
            if(chatMessage.getFromUserName().equals("我")){
                imageButton.setTag(chatMessage.getContent());
            }else {
                imageButton.setImageResource(R.drawable.ic_popup_sync_1);
                imageButton.setEnabled(false);
                String url = "http://" + chatMessage.getContent();
                String saveName = "from" + chatMessage.getFromUserName() + System.currentTimeMillis() + url.substring(url.lastIndexOf("."));
                System.out.println("saveName" + saveName);
                imageButton.setTag(App.getVOICE_PATH() + saveName);
                DownloadManagerUtil.download(ChatRoomActivity.this,
                        url,
                        App.getVOICE_PATH(),
                        saveName,
                        new DownloadManagerUtil.DownloadCompleteListener() {
                            @Override
                            public void onComplete(DownloadManager downloadManager, long completeDownloadId) {
                                Message message = new Message();
                                message.what = refresh_voice_ui_what;
                                message.obj = imageButton;
                                handler.sendMessage(message);
                            }
                        }
                );
                System.out.println(chatMessage.getFromUserName() + "发来语音: " + chatMessage.getContent());
            }
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startPlay(view.getTag().toString());
                }
            });
            chatView.addView(imageButton);

        }else {
            //显示文字
            EmojiconTextView textView = new EmojiconTextView(ChatRoomActivity.this);
            textView.setText(chatMessage.getContent());
            chatView.addView(textView);
        }

        chatGroupView.addView(chatView);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(4, 4, 4, 4);
        layoutChatList.addView(chatGroupView, layoutParams);
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
                        if(fromUser != null && fromUser.getIp().equals(getCharUser().getIp())){
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
        stopRecorder();
        stopPlay();
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
