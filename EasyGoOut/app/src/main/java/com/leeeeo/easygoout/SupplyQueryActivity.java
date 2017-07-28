package com.leeeeo.easygoout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lljjcoder.citypickerview.widget.CityPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Leeeeo on 2017/7/28.
 */

public class SupplyQueryActivity extends AppCompatActivity {

    private TextView queryArea;
    private String cityName = "沈阳市";
    private String provinceName = "辽宁省";
    private TextView weather;
    private String keyName = "和平区";
    private final int SUCCESS = 1;
    private final int FAILURE = 0;
    private final int ERRORCODE = 2;
    private String time;
    private String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    private TextView currentTime;
    private Button btnSearch;
    private Spinner selectedFutureTime;
    private String selectedTime = "00：00～00：30";


    private String weatherApi = "https://api.seniverse.com/v3/weather/now.json?key=qnr3not5ghiemnec&location=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.supply_query_future);
        init();
    }


    private Handler weatherHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    /**
                     * 获取信息成功后，对该信息进行JSON解析，得到所需要的信息，然后在textView上展示出来。
                     */
                    JSONObject jsonObject = null;
                    try {
                        System.out.println(msg.obj.toString());
                        jsonObject = new JSONObject(msg.obj.toString());
//                        System.out.println(jsonObject.toString());
                        JSONArray results = jsonObject.getJSONArray("results");
                        JSONObject now = results.getJSONObject(0).getJSONObject("now");

                        System.out.println(now.toString());

//                        JSONObject now = results.getJSONObject("now");
                        String nowWeather = now.getString("text");
                        weather.setText(nowWeather);
                    } catch (JSONException e) {
                        Toast.makeText(SupplyQueryActivity.this, "获取数据失败" + e.getCause(), Toast.LENGTH_SHORT)
                                .show();
                        e.printStackTrace();
                    }
//                    Toast.makeText(TexiSpotQueryActivity.this, "获取数据成功", Toast.LENGTH_SHORT)
//                            .show();
                    break;

                case FAILURE:
                    Toast.makeText(SupplyQueryActivity.this, "获取数据失败", Toast.LENGTH_SHORT)
                            .show();
                    break;

                case ERRORCODE:
                    Toast.makeText(SupplyQueryActivity.this, "获取的CODE码不为200！",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public void init() {
        queryArea = (TextView) findViewById(R.id.supply_query_future_area);
        weather = (TextView) findViewById(R.id.supply_future_weather);
        currentTime = (TextView) findViewById(R.id.supply_future_time);
        btnSearch = (Button) findViewById(R.id.btn_supply_future_search);
        selectedFutureTime = (Spinner) findViewById(R.id.spinner_supply_future);


        handler.post(updateThread);


        selectedFutureTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedTime = selectedFutureTime.getItemAtPosition(i).toString();
//                selectedTime = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SupplyQueryActivity.this, SupplyQueryResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("province_name", provinceName);
                bundle.putString("city_name", cityName);
                bundle.putString("key_name", keyName);
                bundle.putString("selected_time", selectedTime);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        String address = null;
        try {
            address = weatherApi + URLEncoder.encode("沈阳", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("Weather", address);


        final String finalAddress = address;
        new Thread() {
            public void run() {
                int code;
                try {
                    URL url = new URL(finalAddress);
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
                        InputStream is = conn.getInputStream();
                        String result = HttpUtils.readMyInputStream(is);

                        /**
                         * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                         */
                        Message msg = new Message();
                        msg.obj = result;
                        msg.what = SUCCESS;
                        weatherHandler.sendMessage(msg);

                    } else {

                        Message msg = new Message();
                        msg.what = ERRORCODE;
                        weatherHandler.sendMessage(msg);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    /**
                     * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，来告诉使用者，数据获取是失败。
                     */
                    Message msg = new Message();
                    msg.what = FAILURE;
                    weatherHandler.sendMessage(msg);
                }
            }

            ;
        }.start();


        queryArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CityPicker cityPicker = new CityPicker.Builder(SupplyQueryActivity.this).textSize(20)
                        .titleTextColor("#000000")
                        .backgroundPop(0xa0000000)
                        .province("辽宁省")
                        .city("沈阳市")
                        .district("和平区")
                        .textColor(Color.parseColor("#000000"))
                        .provinceCyclic(true)
                        .cityCyclic(false)
                        .districtCyclic(false)
                        .visibleItemsCount(7)
                        .itemPadding(10)
                        .build();

                cityPicker.show();
                cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
                    @Override
                    public void onSelected(String... citySelected) {
                        queryArea.setText(citySelected[0] + " " + citySelected[1] + " " + citySelected[2]);
                        provinceName = citySelected[0];
                        cityName = citySelected[1];
                        keyName = citySelected[2];
//                        poiSearchCityProcess(cityName, keyName);


                        String address = null;
                        try {
                            address = weatherApi + URLEncoder.encode(cityName, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.d("Weather", address);
                        final String finalAddress = address;
                        new Thread() {
                            public void run() {
                                int code;
                                try {
                                    URL url = new URL(finalAddress);
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
                                        InputStream is = conn.getInputStream();
                                        String result = HttpUtils.readMyInputStream(is);

                                        /**
                                         * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                                         */
                                        Message msg = new Message();
                                        msg.obj = result;
                                        msg.what = SUCCESS;
                                        weatherHandler.sendMessage(msg);

                                    } else {

                                        Message msg = new Message();
                                        msg.what = ERRORCODE;
                                        weatherHandler.sendMessage(msg);
                                    }
                                } catch (Exception e) {

                                    e.printStackTrace();
                                    /**
                                     * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，来告诉使用者，数据获取是失败。
                                     */
                                    Message msg = new Message();
                                    msg.what = FAILURE;
                                    weatherHandler.sendMessage(msg);
                                }
                            }

                            ;
                        }.start();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SupplyQueryActivity.this, "已取消", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    Handler handler = new Handler();//创建Handler
    Runnable updateThread = new Runnable() {
        public void run() {
            handler.postDelayed(updateThread, 1000);
            SimpleDateFormat dateFormatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
            time = dateFormatter.format(Calendar.getInstance().getTime());//获取当前时间

            currentTime.setText(time);
        }
    };
}
