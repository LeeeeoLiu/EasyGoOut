package com.leeeeo.easygoout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;


/**
 * Created by Leeeeo on 2017/7/29.
 */

public class TexiHistoryLocationActivity extends AppCompatActivity {

    private MapView mapViewLocation;
    BaiduMap mBaidumap = null;
    String startLocation = "东北大学浑南校区";
    String endLocation;
    String startCity = "沈阳市";
    String endCity = "沈阳市";
    Button mBtnPre = null; // 上一个节点
    Button mBtnNext = null; // 下一个节点
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    RouteLine route = null;  //路线
    OverlayManager routeOverlay = null;  //该类提供一个能够显示和管理多个Overlay的基类
    boolean useDefaultIcon = false;  //
    private TextView popupText = null; // 泡泡view

    // 搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.texi_hitory_location);
        init();
    }


    public void init() {
        mapViewLocation = (MapView) findViewById(R.id.texi_history_location);
        mBaidumap = mapViewLocation.getMap();


        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(TexiHistoryLocationActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;
//                    mBtnPre.setVisibility(View.VISIBLE);
//                    mBtnNext.setVisibility(View.VISIBLE);
                    route = drivingRouteResult.getRouteLines().get(0);
                    DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
                    routeOverlay = overlay;
                    mBaidumap.setOnMarkerClickListener(overlay);
                    overlay.setData(drivingRouteResult.getRouteLines().get(0));  //设置路线数据
                    overlay.addToMap(); //将所有overlay添加到地图中
                    overlay.zoomToSpan();//缩放地图
                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });

        Bundle bundle = this.getIntent().getExtras();
        endLocation = bundle.getString("end_loc");


        // 重置浏览节点的路线数据
        mBaidumap.clear();
        // 设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = PlanNode.withCityNameAndPlaceName(startCity, startLocation);
        Toast.makeText(TexiHistoryLocationActivity.this, startCity + startLocation + " to " + endCity + endLocation, Toast.LENGTH_SHORT);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName(endCity, endLocation);
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode).to(enNode));

    }


    /**
     * 用于显示一条驾车路线的overlay，自3.4.0版本起可实例化多个添加在地图中显示，当数据中包含路况数据时，则默认使用路况纹理分段绘制
     */
    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_black_24dp);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_black_24dp);
            }
            return null;
        }
    }
}
