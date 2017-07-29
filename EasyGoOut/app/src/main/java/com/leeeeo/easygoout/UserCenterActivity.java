package com.leeeeo.easygoout;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Leeeeo on 2017/7/29.
 */

public class UserCenterActivity extends AppCompatActivity {

    private Button btnChangeHeadPic;
    private Button btnChangeInfo;
    private Button btnChangePwd;
    private Button btnShareApp;
    private Button btnLogout;
    private ImageView userHead;
    private TextView userName;
    private TextView userType;


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_center_index);
        init();
    }

    private void init() {
        btnChangeHeadPic = (Button) findViewById(R.id.btn_change_user_head);
        btnChangeInfo = (Button) findViewById(R.id.btn_change_user_info);
        btnChangePwd = (Button) findViewById(R.id.btn_change_password);
        btnShareApp = (Button) findViewById(R.id.btn_share_app);
        btnLogout = (Button) findViewById(R.id.btn_exit);
        userName = (TextView) findViewById(R.id.text_user_name);
        userType = (TextView) findViewById(R.id.text_user_type);
        userHead = (ImageView) findViewById(R.id.img_user_head);

        final SharedPreferences cache = getSharedPreferences("user_cache", Context.MODE_PRIVATE);
        userName.setText(cache.getString("user_name", null));
        userType.setText(cache.getString("user_type", null));
        String tmpUserHead = cache.getString("user_head", null);


        File file = null;
        if (tmpUserHead != null) {
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


        btnChangeHeadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //调用android本地文件管理器
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1); //1是requestcode
            }
        });

        btnChangeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserCenterActivity.this, UserCenterChangeInfo.class);
                startActivity(intent);
            }
        });

        btnChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserCenterActivity.this, UserCenterChangePwd.class);
                startActivity(intent);
            }
        });

        btnShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserCenterActivity.this, UserCenterShareApp.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences cache = getSharedPreferences("user_cache", Context.MODE_PRIVATE);
                cache.edit().clear().commit();
                Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();
            String[] filePathSplit = fileUri.getPath().split(":");
            final String mFilePath;
            if (filePathSplit.length == 2) {
                //高版本系统内置文件选择器，只能从uri中得到相对路径，形式为：primary：文件相对路径
                String relativeFilePath = null;
                relativeFilePath = filePathSplit[1];
                mFilePath = Environment.getExternalStorageDirectory() + "/" + relativeFilePath;
            } else {
                //低版本系统文件选择器，可以直接从uri中得到绝对路径
                mFilePath = fileUri.getPath();
            }


            String[] folders = mFilePath.split("/");
            String mFileName = folders[folders.length - 1];

            Log.d("saved", "选择了文件:" + mFilePath);
            String fileAbsolutePath = getRealFilePath(getApplicationContext(), fileUri);
//            String s = Environment.getExternalStorageDirectory().getAbsolutePath();
//            Log.d("saved", "environment:" + s);

            if (fileAbsolutePath.equals(null))
                Toast.makeText(UserCenterActivity.this, "请选择相册中的图片", Toast.LENGTH_SHORT);
            else {
                AVFile loadingFile = null;
//                userHead.setBackgroundResource();

//                Bitmap bitmap = BitmapFactory.decodeFile(mFilePath);
////                userHead.setImageBitmap(bitmap);
//                userHead.setImageDrawable(new BitmapDrawable(bitmap));
////                userHead.setBackgroundDrawable(new BitmapDrawable(bitmap));
//

                verifyStoragePermissions(this);

                File file = new File(mFilePath);

                if (file.exists()) {

                    Bitmap bm = BitmapFactory.decodeFile(mFilePath);
                    Log.d("saved", "file exists");
//                    userHead.setImageDrawable();
                    userHead.setImageBitmap(bm);

                    final SharedPreferences cache = getSharedPreferences("user_cache", Context.MODE_PRIVATE);

                    final String userName = cache.getString("user_name",null);
                    String userType=cache.getString("user_type",null);

//                    userPwd = user_password.getText().toString();
                    final AVQuery<AVObject> query = new AVQuery<>("user_info");
                    query.whereEqualTo("user_name", userName);
                    query.whereEqualTo("user_type", userType);
                    query.countInBackground(new CountCallback() {
                        @Override
                        public void done(int i, AVException e) {
                            if (i > 0) {
                                query.getFirstInBackground(new GetCallback<AVObject>() {
                                    @Override
                                    public void done(AVObject avObject, AVException e) {
//                                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(LoginActivity.this, NavigateActivity.class);
//                                        Log.d("lll", avObject.getString("user_type"));
                                        // 第一参数是 className,第二个参数是 objectId
                                        AVObject todo = AVObject.createWithoutData("user_info", avObject.getObjectId());

                                        // 修改 content
                                        todo.put("user_head",mFilePath);
                                        // 保存到云端
                                        todo.saveInBackground();
                                    }
                                });
                            }
                        }
                    });



                    cache.edit().putString("user_head", mFilePath).commit();
//                    userName.setText(cache.getString("user_head", ));
//                    userType.setText(cache.getString("user_type", null));
                    try {
                        loadingFile = AVFile.withFile(mFileName, file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    loadingFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Log.d("saved", "文件上传成功！");
                            } else {
                                Log.d("saved", "文件上传失败! " + e.getMessage() + "|" + e.getCause());
                            }
                        }
                    });


                    Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }


            }
//            Log.d("saved", "fileUri:" +);

        } else {
            Log.d("saved", "没有选择文件");
        }
    }


    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
