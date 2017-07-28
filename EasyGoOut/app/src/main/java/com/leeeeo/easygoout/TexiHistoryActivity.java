package com.leeeeo.easygoout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Leeeeo on 2017/7/28.
 */

public class TexiHistoryActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView selectedDateRecordNum;
    private ListView texiHistoryList;
    private Button btnCleanAll;
    private List<HashMap<String, Object>> mData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.texi_history);
        init();
    }


    public void init() {
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        selectedDateRecordNum = (TextView) findViewById(R.id.seleced_record_num);
        texiHistoryList = (ListView) findViewById(R.id.text_history_list);
        btnCleanAll = (Button) findViewById(R.id.btn_cleanall);
        int recordNum;
        Random random = new Random();
        recordNum = random.nextInt(10);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String dateString = sdf.format(date);
        selectedDateRecordNum.setText(dateString + "共" + recordNum + "条查询记录");


        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map;
//                poiTitle.setText("起点附近共有" + result.getAllPoi().size() + "个推荐打车点");
        System.out.println("onGetPoiResult function is running");
        for (int j = 0; j < recordNum; j++) {
            map = new HashMap<>();
            map.put("texi_record_date", dateString + String.format("%02d", random.nextInt(24)) + ":" + String.format("%02d", random.nextInt(60)));
            map.put("btn_location", R.drawable.ic_location_on_black_24dp);
            map.put("btn_share", R.drawable.ic_share_black_24dp);
            map.put("item_head", R.drawable.ic_pets_black_24dp);
            list.add(map);
        }
        mData = list;

        MyAdapter adapter = new MyAdapter(getApplicationContext());//创建一个适配器
        texiHistoryList.setAdapter(adapter);//为ListView控件绑定适配器


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Random random = new Random();
                int recordNum = random.nextInt(10);
                i1++;
                selectedDateRecordNum.setText(i + "年" + i1 + "月" + i2 + "日" + "共" + recordNum + "条查询记录");


                ArrayList<HashMap<String, Object>> list = new ArrayList<>();
                HashMap<String, Object> map;
//                poiTitle.setText("起点附近共有" + result.getAllPoi().size() + "个推荐打车点");
                System.out.println("onGetPoiResult function is running");
                for (int j = 0; j < recordNum; j++) {
                    map = new HashMap<>();
                    map.put("texi_record_date", i + "年" + i1 + "月" + i2 + "日" + String.format("%02d", random.nextInt(24)) + ":" + String.format("%02d", random.nextInt(60)));
                    map.put("btn_location", R.drawable.ic_location_on_black_24dp);
                    map.put("btn_share", R.drawable.ic_share_black_24dp);
                    map.put("item_head", R.drawable.ic_pets_black_24dp);
//                    map.put("poi_address", p.address);
//                    map.put("poi_head", R.drawable.setting);
//                    map.put("poi_tail", R.drawable.ic_chevron_right_black_24dp);
                    list.add(map);
                }
                mData = list;

                MyAdapter adapter = new MyAdapter(getApplicationContext());//创建一个适配器
                texiHistoryList.setAdapter(adapter);//为ListView控件绑定适配器
            }
        });


        btnCleanAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String old = selectedDateRecordNum.getText().toString();
                String [] temp = null;
                temp=old.split("共");
                selectedDateRecordNum.setText(temp[0]+"共0条查询记录");
                mData.clear();
                MyAdapter adapter = new MyAdapter(getApplicationContext());//创建一个适配器
                texiHistoryList.setAdapter(adapter);//为ListView控件绑定适配器
            }
        });
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
            convertView = mInflater.inflate(R.layout.list_item_texi_history, null);//根据布局文件实例化view
            TextView texiRecordDate = (TextView) convertView.findViewById(R.id.texi_record_date);//找某个控件
            texiRecordDate.setText(mData.get(position).get("texi_record_date").toString());//给该控件设置数据(数据从集合类中来)
            ImageView btnLocation = (ImageView) convertView.findViewById(R.id.btn_location);
            btnLocation.setBackgroundResource((Integer) mData.get(position).get("btn_location"));
            ImageView btnShare = (ImageView) convertView.findViewById(R.id.btn_share);
            btnShare.setBackgroundResource((Integer) mData.get(position).get("btn_share"));
            ImageView itemHead = (ImageView) convertView.findViewById(R.id.item_head);
            itemHead.setBackgroundResource((Integer) mData.get(position).get("item_head"));
//            TextView poiAddress = (TextView) convertView.findViewById(R.id.poi_address);//找某个控件
//            poiAddress.setText(mData.get(position).get("poi_address").toString());//给该控件设置数据(数据从集合类中来)

            return convertView;
        }
    }
}
