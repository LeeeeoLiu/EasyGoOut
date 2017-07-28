package com.leeeeo.easygoout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.GridView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Leeeeo on 2017/7/28.
 */

public class SupplyQueryResultActivity extends AppCompatActivity {
    private GridView resultGridView;
    private TextView resultArea;
    private TextView resultTime;
    private String cityName;
    private String provinceName;
    private String keyName;
    private String selectedTime;
    private String BaiduApi = "http://api.map.baidu.com/staticimage/v2?ak=vWrDxVn5HRNPGS9rBYDC6alGoG7qOD9E&mcode=3C:71:B4:DA:7B:8D:2C:5A:29:32:FC:C6:FB:AD:F3:CE:AA:8E:66:6E;com.leeeeo.easygoout&zoom=14&width=300&height=400&center=";
    private String imageAddress;
    private final int SUCCESS = 1;
    private final int FAILURE = 0;
    private final int ERRORCODE = 2;
    private TextView clickArea;
    private int carSupplyLsit[];
    private int peopleSupplyList[];
    private TextView resultCar;
    private TextView resultPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.supply_query_result);

        init();
    }


    public void init() {
        resultGridView = (GridView) findViewById(R.id.supply_query_result_grid_view);
        resultArea = (TextView) findViewById(R.id.supply_query_result_area);
        resultTime = (TextView) findViewById(R.id.supply_query_result_time);
        clickArea = (TextView) findViewById(R.id.supply_query_click_area);
        resultCar=(TextView)findViewById(R.id.supply_query_result_car);
        resultPeople=(TextView)findViewById(R.id.supply_query_result_people);


        Bundle bundle = this.getIntent().getExtras();
        provinceName = bundle.getString("province_name");
        cityName = bundle.getString("city_name");
        keyName = bundle.getString("key_name");
        selectedTime = bundle.getString("selected_time");


        carSupplyLsit = produceRandom();
        peopleSupplyList = produceRandom();
        resultArea.setText(provinceName + " " + cityName + " " + keyName);
        resultTime.setText(selectedTime);

        try {
            imageAddress = BaiduApi + URLEncoder.encode(cityName + keyName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //生成动态数组，并且转入数据
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 20; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("ItemImage", R.drawable.icon);//添加图像资源的ID
            map.put("ItemText", "NO." + String.valueOf(i));//按序号做ItemText
            lstImageItem.add(map);
        }
        //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
        SimpleAdapter saImageItems = new SimpleAdapter(this, //没什么解释
                lstImageItem,//数据来源
                R.layout.grid_view_item,//night_item的XML实现

                //动态数组与ImageItem对应的子项
                new String[]{"ItemBtn"},

                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{R.id.grid_item_btn});

        //添加并且显示
        resultGridView.setAdapter(saImageItems);


        new Thread() {
            public void run() {
                int code;
                try {
                    URL url = new URL(imageAddress);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");//使用GET方法获取
                    conn.setConnectTimeout(5000);
                    code = conn.getResponseCode();
                    System.out.println(url);
                    System.out.println(code);
                    if (code == 200) {
                        /**
                         * 如果获取的code为200，则证明数据获取是正确的。
                         */
                        Bitmap bitmap = null;
                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                        /**
                         * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                         */
                        Message msg = new Message();
                        msg.obj = bitmap;
                        msg.what = SUCCESS;
                        picHandler.sendMessage(msg);

                    } else {

                        Message msg = new Message();
                        msg.what = ERRORCODE;
                        picHandler.sendMessage(msg);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    /**
                     * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，来告诉使用者，数据获取是失败。
                     */
                    Message msg = new Message();
                    msg.what = FAILURE;
                    picHandler.sendMessage(msg);
                }
            }

            ;
        }.start();
        //添加消息处理

        resultGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String col = null;
                int raw;
                raw = i / 4 + 1;
                switch (i % 4) {
                    case 0:
                        col = "A";
                        break;
                    case 1:
                        col = "B";
                        break;
                    case 2:
                        col = "C";
                        break;
                    case 3:
                        col = "D";
                        break;
                }

                clickArea.setText(col + raw);
                resultCar.setText(carSupplyLsit[i]+"辆");
                resultPeople.setText(peopleSupplyList[i]+"人");

            }
        });
    }

    private Handler picHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    /**
                     * 获取信息成功后，对该信息进行JSON解析，得到所需要的信息，然后在textView上展示出来。
                     */
                    JSONObject jsonObject = null;
                    System.out.println(msg.obj.toString());
                    Bitmap bitmap = (Bitmap) msg.obj;
                    resultGridView.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    Toast.makeText(SupplyQueryResultActivity.this, "获取数据成功", Toast.LENGTH_SHORT)
                            .show();
                    break;

                case FAILURE:
                    Toast.makeText(SupplyQueryResultActivity.this, "获取数据失败", Toast.LENGTH_SHORT)
                            .show();
                    break;

                case ERRORCODE:
                    Toast.makeText(SupplyQueryResultActivity.this, "获取的CODE码不为200！",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

        ;
    };


    //在一定范围内生成随机数.
    //比如此处要求在[0 - n)内生成随机数.
    //注意:包含0不包含n

    private int[] produceRandom() {
        int randomList[] = new int[20];
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            randomList[i]=random.nextInt(20);
//            System.out.println("random.nextInt()="+random.nextInt(20));
        }
//        System.out.println("/////以上为testRandom2()的测试///////");

        return randomList;
    }

}
