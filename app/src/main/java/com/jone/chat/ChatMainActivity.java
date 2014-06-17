package com.jone.chat;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jone.chat.adapter.UserExpandableListAdapter;
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
    private List<List<User>> childrens;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        if(!SystemUtil.isWifiActive(ChatMainActivity.this)){	//若wifi没有打开，提示
            Toast.makeText(this, R.string.no_wifi, Toast.LENGTH_LONG).show();
        }
        localIp = SystemUtil.getLocalIpAddress();
        strGroups = new ArrayList<String>();
        childrens = new ArrayList<List<User>>();
        initViews();
        refreshOnlineUsers();
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
                refreshOnlineUsers();
            }
        });

        listOnlineUsers = (ExpandableListView) findViewById(R.id.listOnlineUsers);
        adapter = new UserExpandableListAdapter(ChatMainActivity.this, strGroups, childrens);
        listOnlineUsers.setAdapter(adapter);
    }

    private void refreshOnlineUsers(){
        //清空数据
        strGroups.clear();
        childrens.clear();
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
}
