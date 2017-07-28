package com.leeeeo.easygoout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.SuggestionSearch;
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
import java.util.Date;
import java.util.List;

/**
 * Created by Leeeeo on 2017/7/25.
 */

public class TexiSpotQueryActivity extends AppCompatActivity implements OnGetPoiSearchResultListener {

    private EditText startPosition;
    private TextView weather;
    private String cityName;
    private String keyName;
    private Button btnSearchPoi;
    private Spinner timeSelect;
    private TextView currentTime;
    private TextView futureTime;
    //BMapManager 对象管理地图、定位、搜索功能
    private BMapManager mBMapManager;
    private MapView mapView = null;                     //地图主控件
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private BaiduMap mBaiduMap = null;
    private List<String> suggest;
    int searchType = 0;  // 搜索的类型，在显示时区分
    private int loadIndex = 0;
    LatLng center = new LatLng(39.92235, 116.380338);
    int radius = 100;
    LatLng southwest = new LatLng(39.92235, 116.380338);
    LatLng northeast = new LatLng(39.947246, 116.414977);
    LatLngBounds searchbound = new LatLngBounds.Builder().include(southwest).include(northeast).build();
    private static final int SET = 1;

    private String time;
    private String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    private final int SUCCESS = 1;
    private final int FAILURE = 0;
    private final int ERRORCODE = 2;


    private String weatherApi = "https://api.seniverse.com/v3/weather/now.json?key=qnr3not5ghiemnec&location=";


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
                        JSONObject now=results.getJSONObject(0).getJSONObject("now");

                        System.out.println(now.toString());

//                        JSONObject now = results.getJSONObject("now");
                        String nowWeather = now.getString("text");
                        weather.setText(nowWeather);
                    } catch (JSONException e) {
                        Toast.makeText(TexiSpotQueryActivity.this, "获取数据失败"+e.getCause(), Toast.LENGTH_SHORT)
                                .show();
                        e.printStackTrace();
                    }
//                    Toast.makeText(TexiSpotQueryActivity.this, "获取数据成功", Toast.LENGTH_SHORT)
//                            .show();
                    break;

                case FAILURE:
                    Toast.makeText(TexiSpotQueryActivity.this, "获取数据失败", Toast.LENGTH_SHORT)
                            .show();
                    break;

                case ERRORCODE:
                    Toast.makeText(TexiSpotQueryActivity.this, "获取的CODE码不为200！",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.texi_spot_query);

        init();
    }

    private void init() {
        startPosition = (EditText) findViewById(R.id.text_start_pos);
        weather = (TextView) findViewById(R.id.supply_future_weather);
        btnSearchPoi = (Button) findViewById(R.id.btn_search_poi);
        timeSelect = (Spinner) findViewById(R.id.spinner_hour);
        futureTime = (TextView) findViewById(R.id.text_future_time);
        currentTime = (TextView) findViewById(R.id.supply_future_time);

        handler.post(updateThread);

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.mapView2))).getBaiduMap();


        startPosition.setEnabled(true);
        startPosition.setClickable(true);



        String address = null;
        try {
            address = weatherApi + URLEncoder.encode("沈阳市","UTF-8");
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




        btnSearchPoi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!startPosition.getText().toString().equals("")) {
                    Intent intent = new Intent(TexiSpotQueryActivity.this, TexiSpotQueryResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("city_name", cityName);
                    bundle.putString("key_name", keyName);
                    intent.putExtras(bundle);
                    Log.d("test", "点击跳转到打车推荐");
                    startActivity(intent);
                }
            }
        });
        startPosition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final CityPicker cityPicker = new CityPicker.Builder(TexiSpotQueryActivity.this).textSize(20)
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
                        startPosition.setText(citySelected[0] + " " + citySelected[1] + " " + citySelected[2]);
                        cityName = citySelected[1];
                        keyName = citySelected[2];
                        poiSearchCityProcess(cityName, keyName);


                        String address = null;
                        try {
                            address = weatherApi + URLEncoder.encode(cityName,"UTF-8");
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
                        Toast.makeText(TexiSpotQueryActivity.this, "已取消", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        timeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("spinner", "selected " + i);
                Date date = new Date();
                Date newDate2 = new Date(date.getTime() + (long) i * 30 * 60 * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String dateString = sdf.format(newDate2);
                futureTime.setText("小时后,即：" + dateString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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


    private void poiSearchCityProcess(String citystr, String keystr) {

        searchType = 1;
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(citystr).keyword(keystr).pageNum(loadIndex));
    }

    /**
     * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
     *
     * @param result
     */
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(TexiSpotQueryActivity.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new TexiSpotQueryActivity.MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
            switch (searchType) {
                case 2:
                    showNearbyArea(center, radius);
                    break;
                case 3:
                    showBound(searchbound);
                    break;
                default:
                    break;
            }
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(TexiSpotQueryActivity.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
     *
     * @param result
     */
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(TexiSpotQueryActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(TexiSpotQueryActivity.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }

    /**
     * 对周边检索的范围进行绘制
     *
     * @param center
     * @param radius
     */
    public void showNearbyArea(LatLng center, int radius) {
        BitmapDescriptor centerBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_geo);
        MarkerOptions ooMarker = new MarkerOptions().position(center).icon(centerBitmap);
        mBaiduMap.addOverlay(ooMarker);

        OverlayOptions ooCircle = new CircleOptions().fillColor(0xCCCCCC00)
                .center(center).stroke(new Stroke(5, 0xFFFF00FF))
                .radius(radius);
        mBaiduMap.addOverlay(ooCircle);
    }

    /**
     * 对区域检索的范围进行绘制
     *
     * @param bounds
     */
    public void showBound(LatLngBounds bounds) {
        BitmapDescriptor bdGround = BitmapDescriptorFactory
                .fromResource(R.drawable.ground_overlay);

        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds).image(bdGround).transparency(0.8f);
        mBaiduMap.addOverlay(ooGround);

        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(bounds.getCenter());
        mBaiduMap.setMapStatus(u);

        bdGround.recycle();
    }

}
