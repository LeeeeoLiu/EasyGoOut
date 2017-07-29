package com.leeeeo.easygoout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

/**
 * Created by Leeeeo on 2017/7/24.
 */

public class NavigateActivity extends AppCompatActivity {

    private TextView textUserName;
    private TextView textUserType;
    private ImageView userHead;
    private ImageButton btnTexiSpotSuggest;
    private ImageButton btnSupplyQuery;
    private ImageButton btnHotspotQuery;
    private ImageButton btnTexiHistory;
    private ImageButton btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {

        textUserName = (TextView) findViewById(R.id.text_user_name);
        textUserType = (TextView) findViewById(R.id.text_user_type);
        btnTexiSpotSuggest = (ImageButton) findViewById(R.id.btn_texi_spot_suggest);
        btnSupplyQuery = (ImageButton) findViewById(R.id.btn_supply_query);
        btnHotspotQuery = (ImageButton) findViewById(R.id.btn_hotspot_query);
        btnTexiHistory = (ImageButton) findViewById(R.id.btn_texi_history);
        btnSetting = (ImageButton) findViewById(R.id.btn_setting);
        userHead = (ImageView) findViewById(R.id.img_user_head);

        Bundle bundle = this.getIntent().getExtras();
        SharedPreferences cache = getSharedPreferences("user_cache", Context.MODE_PRIVATE);
//        cache.edit().putString("name", "小张").putInt("age", 11).commit();
        String tmpUserName = cache.getString("user_name", null);
        String tmpUserType = cache.getString("user_type", null);
        String tmpUserHead = cache.getString("user_head", null);

        textUserName.setText(tmpUserName);
        textUserType.setText(tmpUserType);

        File file = null;
        if (tmpUserHead != null && !tmpUserHead.equals("null")) {
            file = new File(tmpUserHead);
        }

        if (file != null && file.exists()) {

            Bitmap bm = BitmapFactory.decodeFile(tmpUserHead);
            Log.d("saved", "file exists");
            //                    userHead.setImageDrawable();
            userHead.setImageBitmap(bm);
            //                    userName.setText(cache.getString("user_head", ));
            //                    userType.setText(cache.getString("user_type", null));

        }

        btnTexiSpotSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavigateActivity.this, TexiSpotQueryActivity.class);
                startActivity(intent);
            }
        });

        btnSupplyQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavigateActivity.this, SupplyQueryActivity.class);
                startActivity(intent);
            }
        });

        btnHotspotQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(NavigateActivity.this, HotSpotQueryActivity.class);
//                startActivity(intent);
                Toast.makeText(NavigateActivity.this, "该功能尚未开发", Toast.LENGTH_SHORT).show();
            }
        });

        btnTexiHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavigateActivity.this, TexiHistoryActivity.class);
                startActivity(intent);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavigateActivity.this, UserCenterActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("user_name", textUserName.getText().toString());
                bundle1.putString("user_type", textUserType.getText().toString());
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

    }
}
