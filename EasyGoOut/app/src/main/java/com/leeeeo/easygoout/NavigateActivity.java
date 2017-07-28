package com.leeeeo.easygoout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Leeeeo on 2017/7/24.
 */

public class NavigateActivity extends AppCompatActivity {

    private TextView textUserName;
    private TextView textUserType;
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

        Bundle bundle = this.getIntent().getExtras();
        textUserName.setText(bundle.getString("user_name"));
        textUserType.setText(bundle.getString("user_type"));

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

            }
        });

        btnTexiHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
