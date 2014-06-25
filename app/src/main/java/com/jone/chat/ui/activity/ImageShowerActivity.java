package com.jone.chat.ui.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.jone.chat.Constant;
import com.jone.chat.R;
import com.jone.chat.util.PhotoUtils;
import com.jone.chat.util.VolleyUtil;


public class ImageShowerActivity extends Activity {
    private String imagePath;
    private boolean isNetworkPhoto;
    private ImageView large_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_shower);
        large_image = (ImageView) findViewById(R.id.large_image);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            imagePath = bundle.getString(Constant.IMAGE_PATH_KEY);
            isNetworkPhoto = bundle.getBoolean("isNetworkPhoto", false);
            if(imagePath != null){
                System.out.println("imagePath: " + imagePath + ", isNetworkPhoto: " + isNetworkPhoto);
                if(isNetworkPhoto){
                    VolleyUtil.loadImageByVolley(ImageShowerActivity.this, large_image, "http://" + imagePath);
                }else {
                    large_image.setImageBitmap(PhotoUtils.getBitmapFromFile(imagePath));
                }
            }
        }

        Button btnCLose = (Button) findViewById(R.id.btnCLose);
        btnCLose.setOnClickListener(new View.OnClickListener() { // 点击返回
            public void onClick(View paramView) {
                System.out.println("点击了关闭按钮");
                finish();
            }
        });
    }
}
