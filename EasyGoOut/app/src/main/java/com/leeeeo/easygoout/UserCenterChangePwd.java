package com.leeeeo.easygoout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.GetCallback;

/**
 * Created by Leeeeo on 2017/7/29.
 */

public class UserCenterChangePwd extends AppCompatActivity {
    private EditText oldPwd;
    private EditText newPwd;
    private EditText checkPwd;
    private Button btnSave;
    private Button btnCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_center_change_password);
        init();
    }

    private void init() {
        oldPwd = (EditText) findViewById(R.id.old_password);
        newPwd = (EditText) findViewById(R.id.new_password);
        checkPwd = (EditText) findViewById(R.id.check_password);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnCancel = (Button) findViewById(R.id.btn_cancel);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences cache = getSharedPreferences("user_cache", Context.MODE_PRIVATE);
//                    userPwd = user_password.getText().toString();
                final AVQuery<AVObject> query = new AVQuery<>("user_info");
                query.whereEqualTo("user_name", cache.getString("user_name", null));
                query.whereEqualTo("user_type", cache.getString("user_type", null));
                query.countInBackground(new CountCallback() {
                    @Override
                    public void done(int i, AVException e) {
                        if (i > 0) {
                            query.getFirstInBackground(new GetCallback<AVObject>() {
                                @Override
                                public void done(AVObject avObject, AVException e) {
                                    // 第一参数是 className,第二个参数是 objectId
                                    AVObject todo = AVObject.createWithoutData("user_info", avObject.getObjectId());

                                    // 修改 content
                                    todo.put("user_password", newPwd.getText().toString());
                                    // 保存到云端
                                    todo.saveInBackground();
                                    Toast.makeText(UserCenterChangePwd.this, "修改成功!", Toast.LENGTH_SHORT).show();

                                    Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
