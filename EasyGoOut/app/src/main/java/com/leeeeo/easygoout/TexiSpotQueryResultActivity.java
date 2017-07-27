package com.leeeeo.easygoout;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leeeeo on 2017/7/27.
 */

public class TexiSpotQueryResultActivity extends AppCompatActivity implements OnGetPoiSearchResultListener {

    private String cityName;
    private String keyName;
    private Button poiTitle;
    private PoiSearch mPoiSearch = null;
    int searchType = 0;  // 搜索的类型，在显示时区分
    private int loadIndex = 0;
    private PoiResult poiResult;
    LatLng center = new LatLng(39.92235, 116.380338);
    int radius = 100;
    LatLng southwest = new LatLng(39.92235, 116.380338);
    LatLng northeast = new LatLng(39.947246, 116.414977);
    List<String> poiName;
    List<String> poiAddress;
    LatLngBounds searchbound = new LatLngBounds.Builder().include(southwest).include(northeast).build();

    private List<HashMap<String, Object>> mData;
    private ListView poiListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.texi_spot_suggest_result);
        init();
    }

    private void init() {
        Bundle bundle = this.getIntent().getExtras();
        cityName = bundle.getString("city_name");
        keyName = bundle.getString("key_name");
        poiTitle = (Button) findViewById(R.id.poi_title);
        poiListView = (ListView) findViewById(R.id.poi_result);//实例化ListView
//        ArrayList<HashMap<String,String>>poiTarget;


        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        poiSearchCityProcess(cityName, keyName);


     //   mData = getData();//为刚才的变量赋值
//        MyAdapter adapter = new MyAdapter(this);//创建一个适配器

      //  MyAdapter adapter = new MyAdapter(getApplicationContext());//创建一个适配器

//        System.out.println(mData.size());
//        for (int i=0;i<mData.size();i++)
//        {
//            System.out.println(mData.get(i).get("poi_name").toString());
//        }

      //  poiListView.setAdapter(adapter);//为ListView控件绑定适配器


    }

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
        poiResult = result;
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(TexiSpotQueryResultActivity.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
           // mData.clear();
            ArrayList<HashMap<String, Object>> list = new ArrayList<>();
            HashMap<String, Object> map;
            poiTitle.setText("起点附近共有" + result.getAllPoi().size() + "个推荐打车点");
            System.out.println("onGetPoiResult function is running");
            for (int i = 0; i < poiResult.getAllPoi().size(); i++) {
                PoiInfo p = poiResult.getAllPoi().get(i);
                Log.d("poi", p.name + "||" + p.address);
                map = new HashMap<>();
                map.put("poi_name", p.name);
                map.put("poi_address", p.address);
                map.put("poi_head", R.drawable.setting);
                map.put("poi_tail", R.drawable.ic_chevron_right_black_24dp);
                list.add(map);
            }
            mData = list;
            MyAdapter adapter = new MyAdapter(getApplicationContext());//创建一个适配器
            poiListView.setAdapter(adapter);//为ListView控件绑定适配器
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
            Toast.makeText(TexiSpotQueryResultActivity.this, strInfo, Toast.LENGTH_LONG)
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
            Toast.makeText(TexiSpotQueryResultActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(TexiSpotQueryResultActivity.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
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
     * 自定义适配器
     */
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;// 动态布局映射

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        // 决定ListView有几行可见
        @Override
        public int getCount() {
            return mData.size();// ListView的条目数
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.list_item_texi_spot_suggest, null);//根据布局文件实例化view
            ImageView poiHead = (ImageView) convertView.findViewById(R.id.head_pic);
            poiHead.setBackgroundResource((Integer) mData.get(position).get("poi_head"));
            ImageView poiTail = (ImageView) convertView.findViewById(R.id.tail_pic);
            poiTail.setBackgroundResource((Integer) mData.get(position).get("poi_tail"));
            TextView poiName = (TextView) convertView.findViewById(R.id.poi_name);//找某个控件
            poiName.setText(mData.get(position).get("poi_name").toString());//给该控件设置数据(数据从集合类中来)
            TextView poiAddress = (TextView) convertView.findViewById(R.id.poi_address);//找某个控件
            poiAddress.setText(mData.get(position).get("poi_address").toString());//给该控件设置数据(数据从集合类中来)

            return convertView;
        }
    }


    // 初始化一个List
    private List<HashMap<String, Object>> getData() {
        // 新建一个集合类，用于存放多条数据
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = null;
        System.out.println("getData function is running");
        for (int i = 1; i <= 10; i++) {
            map = new HashMap<String, Object>();
            map = new HashMap<>();
            map.put("poi_name", i);
            map.put("poi_address", i);
            map.put("poi_head", R.drawable.setting);
            map.put("poi_tail", R.drawable.ic_chevron_right_black_24dp);
            list.add(map);
        }

        return list;
    }
}
