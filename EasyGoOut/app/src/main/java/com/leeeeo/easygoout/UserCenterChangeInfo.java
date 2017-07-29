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
import android.widget.Spinner;
import android.widget.TextView;
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

public class UserCenterChangeInfo extends AppCompatActivity {

    private TextView userName;
    private Spinner userType;
    private Button btnSave;
    private Button btnCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_center_change_info);
        init();
    }

    private void init() {
        userName = (TextView) findViewById(R.id.edit_user_name);
        userType = (Spinner) findViewById(R.id.spinner_user_type);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        SharedPreferences cache = getSharedPreferences("user_cache", Context.MODE_PRIVATE);
//        cache.edit().putString("name", "小张").putInt("age", 11).commit();
        final String tmpUserName = cache.getString("user_name", null);
        String tmpUserType = cache.getString("user_type", null);
        userName.setText(tmpUserName);
        if (tmpUserType.equals("乘客"))
            userType.setSelection(0);
        else
            userType.setSelection(1);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tmpUserTypeNew;
                if (userType.getSelectedItemPosition() == 1) {
                    tmpUserTypeNew = "司机";
                } else
                    tmpUserTypeNew = "乘客";

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
                                    todo.put("user_type", tmpUserTypeNew);
                                    // 保存到云端
                                    todo.saveInBackground();
                                    Toast.makeText(UserCenterChangeInfo.this, "修改成功!", Toast.LENGTH_SHORT).show();
                                    SharedPreferences cache = getSharedPreferences("user_cache", Context.MODE_PRIVATE);
                                    cache.edit().putString("user_name", userName.getText().toString()).commit();
                                    cache.edit().putString("user_type", tmpUserTypeNew).commit();
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
