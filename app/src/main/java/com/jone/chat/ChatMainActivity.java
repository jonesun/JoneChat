package com.jone.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jone.chat.adapter.UserExpandableListAdapter;
import com.jone.chat.application.App;
import com.jone.chat.bean.User;
import com.jone.chat.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;


public class ChatMainActivity extends Activity {
    String localIp;
    private ImageButton imBtnHead;
    private TextView txtLocalName;
    private TextView txtLocalIP;

    private TextView txtOnlineUserCount;
    private Button btnRefresh;

    private ExpandableListView listOnlineUsers;
    private UserExpandableListAdapter adapter;
    private List<String> strGroups;
    private List<List<User>> children;

    private BroadcastReceiver broadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        if(!SystemUtil.isWifiActive(ChatMainActivity.this)){	//若wifi没有打开，提示
            Toast.makeText(this, R.string.no_wifi, Toast.LENGTH_LONG).show();
        }
        localIp = SystemUtil.getLocalIpAddress();
        strGroups = new ArrayList<>();
        strGroups.add("未分组");
        children = new ArrayList<>();
        initViews();
        bindBroadcast();
        App.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshOnlineUsers();
            }
        }, 1000);
    }

    private void initViews(){
        imBtnHead = (ImageButton) findViewById(R.id.imBtnHead);
        txtLocalName = (TextView) findViewById(R.id.txtLocalName);
        txtLocalName.setText("用户(" + localIp + ")");
        txtLocalIP = (TextView) findViewById(R.id.txtLocalIP);
        txtLocalIP.setText(localIp);

        txtOnlineUserCount = (TextView) findViewById(R.id.txtOnlineUserCount);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("刷新列表");
                refreshOnlineUsers();
            }
        });

        listOnlineUsers = (ExpandableListView) findViewById(R.id.listOnlineUsers);
        adapter = new UserExpandableListAdapter(ChatMainActivity.this, strGroups, children);
        listOnlineUsers.setAdapter(adapter);
    }

    private void bindBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK); // 每分钟更新，只能采用代码registerReceiver动态注册方式
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED); // 时间被改变，人为设置时间
        intentFilter.addAction(Constant.BROADCAST_RECEIVE_MSG_ACTION); //收到信息
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("action: " + intent.getAction());
                String action = intent.getAction();
                switch (action){
                    case Intent.ACTION_TIME_TICK:
                    case Intent.ACTION_TIME_CHANGED:
                        System.out.println("通知在线");
                        try {
                            App.getInstance().getCoreService().noticeOnline();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Constant.BROADCAST_RECEIVE_MSG_ACTION:
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void refreshOnlineUsers(){
        //检查在线用户
        children.clear();
        try {
            List<User> onlineUsers = App.getInstance().getCoreService().getOnlineUsers();
            System.out.println("当前在线"+ onlineUsers.size() + "个用户");
            txtOnlineUserCount.setText("当前在线"+ onlineUsers.size() + "个用户");
            children.add(onlineUsers);
            adapter.notifyDataSetChanged();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_main, menu);
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
    protected void onStart() {
        super.onStart();
        System.out.println("ChatMainActivity onStart");
        try {
            App.getInstance().getCoreService().noticeOnline();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("ChatMainActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }
}
