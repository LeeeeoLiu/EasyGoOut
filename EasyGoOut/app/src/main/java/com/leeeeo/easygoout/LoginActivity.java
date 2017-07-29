package com.leeeeo.easygoout;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mapapi.SDKInitializer;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.newsmssdk.BmobSMS;
import cn.bmob.newsmssdk.listener.RequestSMSCodeListener;


public class LoginActivity extends AppCompatActivity {

    private EditText user_name;
    private EditText user_password;
    private Button btn_login;
    private TextView btn_register;
    private TextView btn_forget_password;
    Context context;


    String userName;
    String userPwd;


//    private static final String LTAG = BMapApiDemoMain.class.getSimpleName();

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Toast.makeText(LoginActivity.this, "key 验证出错! 错误码 :" + intent.getIntExtra
                        (SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
                        + " ; 请在 AndroidManifest.xml 文件中检查 key 设置", Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                Toast.makeText(LoginActivity.this, "key 验证成功! 功能可以正常使用", Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(LoginActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private SDKReceiver mReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        init();
    }

    private void init() {
        user_name = (EditText) findViewById(R.id.text_user_name);
        user_password = (EditText) findViewById(R.id.text_user_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_forget_password = (TextView) findViewById(R.id.btn_forget_password);
        btn_register = (TextView) findViewById(R.id.btn_register);

        context = getApplicationContext();


        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);

        SDKInitializer.initialize(context);
        BmobSMS.initialize(context, "19287f613fa7556cf8a5693760c1ea92");
        AVOSCloud.initialize(this, "IakY4fesWp0RPmzUA9tP1f8z-gzGzoHsz", "NXUzlQGYgjuzTyhhNiuF2tMu");

        final SharedPreferences cache = getSharedPreferences("user_cache", Context.MODE_PRIVATE);
//        cache.edit().putString("name", "小张").putInt("age", 11).commit();
        String tmpUserName = cache.getString("user_name", null);
//        cache.getString()

        if (tmpUserName != null && !tmpUserName.equals("")) {
            Toast.makeText(LoginActivity.this, "欢迎回来," + tmpUserName, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, NavigateActivity.class);
            startActivity(intent);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = user_name.getText().toString();
                userPwd = user_password.getText().toString();
                final AVQuery<AVObject> query = new AVQuery<>("user_info");
                query.whereEqualTo("user_name", userName);
                query.whereEqualTo("user_password", userPwd);
                query.countInBackground(new CountCallback() {
                    @Override
                    public void done(int i, AVException e) {
                        if (i > 0) {
                            query.getFirstInBackground(new GetCallback<AVObject>() {
                                @Override
                                public void done(AVObject avObject, AVException e) {
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, NavigateActivity.class);
                                    Log.d("lll", avObject.getString("user_type"));

                                    SharedPreferences cache = getSharedPreferences("user_cache", Context.MODE_PRIVATE);
                                    cache.edit().putString("user_name", userName).commit();
                                    cache.edit().putString("user_type", avObject.getString("user_type")).commit();
                                    cache.edit().putString("user_head", avObject.getString("user_head")).commit();
                                    startActivity(intent);
                                }
                            });
                        } else
                            Toast.makeText(LoginActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });

        btn_forget_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "忘记密码", Toast.LENGTH_SHORT).show();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Toast.makeText(LoginActivity.this, "注册", Toast.LENGTH_SHORT).show();
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                final String sendTime = sdf.format(date);
                int round = (int) Math.round(Math.random() * (999999 - 100000) + 100000);
                final String verifycode = String.valueOf(round);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);

                View registerLayout = getLayoutInflater().inflate(R.layout.register_layout, null);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(registerLayout);
                final AlertDialog dialog = alertDialogBuilder.show();

                final Button btnRegister = (Button) registerLayout.findViewById(R.id.btn_send_register);
                final Button btnSendCode = (Button) registerLayout.findViewById(R.id.btn_send_code);
                final EditText registerName = (EditText) registerLayout.findViewById(R.id.register_name);
                final EditText registerPwd = (EditText) registerLayout.findViewById(R.id.register_password);
                final EditText registerPhone = (EditText) registerLayout.findViewById(R.id.register_phone);
                final EditText registerCode = (EditText) registerLayout.findViewById(R.id.register_code);

                btnSendCode.setEnabled(false);
                btnRegister.setEnabled(false);


                registerName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (!registerName.getText().toString().equals("")) {
                            AVQuery<AVObject> query = new AVQuery<>("user_info");
                            query.whereEqualTo("user_name", registerName.getText().toString());
                            query.countInBackground(new CountCallback() {
                                @Override
                                public void done(int i, AVException e) {
                                    if (i > 0) {
                                        Toast.makeText(LoginActivity.this, "该用户已存在", Toast.LENGTH_SHORT).show();
                                        btnSendCode.setEnabled(false);
                                    } else
                                        btnSendCode.setEnabled(true);

                                }
                            });
                        }
                    }
                });

                registerPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (!registerPhone.getText().toString().equals("")) {
                            AVQuery<AVObject> query = new AVQuery<>("user_info");
                            query.whereEqualTo("user_phone", registerPhone.getText().toString());
                            query.countInBackground(new CountCallback() {
                                @Override
                                public void done(int i, AVException e) {
                                    if (i > 0) {
                                        Toast.makeText(LoginActivity.this, "该电话已被注册已存在", Toast.LENGTH_SHORT).show();
                                        btnRegister.setEnabled(false);
                                    } else
                                        btnRegister.setEnabled(true);

                                }
                            });
                        }
                    }
                });

                btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (verifycode.equals(registerCode.getText().toString())) {
                            AVObject todo = new AVObject("user_info");
                            todo.put("user_name", registerName.getText().toString());
                            todo.put("user_password", registerPwd.getText().toString());
                            todo.put("user_phone", registerPhone.getText().toString());// 只要添加这一行代码，服务端就会自动添加这个字段
                            todo.put("user_type", "乘客");
                            todo.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        // 存储成功
                                        Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                        SharedPreferences cache = getSharedPreferences("user_cache", Context.MODE_PRIVATE);
                                        cache.edit().putString("user_name", registerName.getText().toString()).commit();
                                        cache.edit().putString("user_type", "乘客").commit();
                                        dialog.dismiss();
                                    } else {
                                        // 失败的话，请检查网络环境以及 SDK 配置是否正确
                                        Toast.makeText(LoginActivity.this, "注册失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });

                btnSendCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BmobSMS.requestSMS(LoginActivity.this, registerPhone.getText().toString(), "欢迎使用出行易,您的验证码为" + verifycode + "，请及时验证！", sendTime, new RequestSMSCodeListener() {
                            @Override
                            public void done(Integer integer, cn.bmob.newsmssdk.exception.BmobException ex) {
                                if (ex == null) {//验证码发送成功
                                    Toast.makeText(LoginActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "发送失败：errorCode = " + ex.getErrorCode() + ",errorMsg = " + ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
