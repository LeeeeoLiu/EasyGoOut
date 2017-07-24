package com.leeeeo.easygoout;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.newsmssdk.BmobSMS;
import cn.bmob.newsmssdk.listener.RequestSMSCodeListener;


public class MainActivity extends AppCompatActivity {

    private EditText user_name;
    private EditText user_password;
    private Button btn_login;
    private TextView btn_register;
    private TextView btn_forget_password;
    Context context;


    String userName;
    String userPwd;

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

        BmobSMS.initialize(context, "19287f613fa7556cf8a5693760c1ea92");
        AVOSCloud.initialize(this,"IakY4fesWp0RPmzUA9tP1f8z-gzGzoHsz","NXUzlQGYgjuzTyhhNiuF2tMu");

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = user_name.getText().toString();
                userPwd = user_password.getText().toString();
                if (userName.equals("admin") && userPwd.equals("admin")) {
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_forget_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "忘记密码", Toast.LENGTH_SHORT).show();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "注册", Toast.LENGTH_SHORT).show();
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                final String sendTime = sdf.format(date);
                int round = (int) Math.round(Math.random() * (999999 - 100000) + 100000);
                final String verifycode = String.valueOf(round);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                View registerLayout = getLayoutInflater().inflate(R.layout.register_layout, null);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(registerLayout);
                final AlertDialog dialog = alertDialogBuilder.show();

                Button btnRegister = (Button) registerLayout.findViewById(R.id.btn_send_register);
                Button btnSendCode = (Button)registerLayout.findViewById(R.id.btn_send_code);
                EditText registerName=(EditText)registerLayout.findViewById(R.id.register_name);
                EditText registerPwd=(EditText)registerLayout.findViewById(R.id.register_password);
                final EditText registerPhone=(EditText)registerLayout.findViewById(R.id.register_phone);
                final EditText registerCode=(EditText)registerLayout.findViewById(R.id.register_code);

//                BmobQuery<User> bmobQuery = new BmobQuery<User>();
//                bmobQuery.addWhereEqualTo("name",registerName.getText().toString());

                btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (verifycode.equals(registerCode.getText().toString()))
                        {
                            Toast.makeText(MainActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    }
                });

                btnSendCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BmobSMS.requestSMS(MainActivity.this,registerPhone.getText().toString(), "欢迎使用出行易,您的验证码为" + verifycode + "，请及时验证！", sendTime, new RequestSMSCodeListener() {
                            @Override
                            public void done(Integer integer, cn.bmob.newsmssdk.exception.BmobException ex) {
                                if (ex == null) {//验证码发送成功
                                    Toast.makeText(MainActivity.this, "验证码发送成功：" , Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "发送失败：errorCode = " + ex.getErrorCode() + ",errorMsg = " + ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
