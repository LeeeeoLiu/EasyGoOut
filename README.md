# EasyGoOut



### 所有原型设计已初步完成layout

- 登录页
- 导航页 
- 用户中心
 - 修改用户信息
 - 修改登录密码
 - 分享
 - 修改成功
- 供需查询
- 打车点历史记录
- 热点查询


### 已解决问题
#### Android:label居中显示 
主要原理是，在ToolBar中添加一个TextView，然后你就可以通过设置TextView的样式，来达到你想要的效果。
设置原来的toolbar不显示，在styles.xml

```
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
```
改为

```
<style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
```
   
将ToolBar自定义
   
```
   <?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.Toolbar xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/toolbar8"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_alignParentLeft="true"
    android:layout_alignParentStart="true"
    android:layout_alignParentTop="true"
    android:background="?attr/colorPrimary"
    android:minHeight="?attr/actionBarSize"
    android:theme="?attr/actionBarTheme">

    <TextView
        android:id="@+id/toolbar_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:text="出行易，让你的出行从容不迫"
        android:textColor="@android:color/white"
        android:textSize="20sp"
        android:textStyle="bold" />
</android.support.v7.widget.Toolbar>

```
   
最后在你要使用ToolBar的地方include即可
   
```
   <include layout="@layout/tool_bar_top"/>
```

#### build报错

```

Error:Execution failed for task ':app:processDebugManifest'.
> Manifest merger failed : Attribute meta-data#android.support.VERSION@value value=(25.3.1) from [com.android.support:design:25.3.1] AndroidManifest.xml:27:9-31
  	is also present at [com.android.support:appcompat-v7:26.0.0-alpha1] AndroidManifest.xml:27:9-38 value=(26.0.0-alpha1).
  	Suggestion: add 'tools:replace="android:value"' to <meta-data> element at AndroidManifest.xml:25:5-27:34 to override.

```

把下面这些代码添加到build.gradle最后里即可


```
configurations.all {
    resolutionStrategy.eachDependency { DependencyResolveDetails details ->
        def requested = details.requested
        if (requested.group == 'com.android.support') {
            if (!requested.name.startsWith("multidex")) {
                details.useVersion '25.3.0'
            }
        }
    }
}
```

#### Caused by: android.os.NetworkOnMainThreadException

这个是因为一个APP如果在主线程中请求网络操作，将会抛出此异常。Android这个设计是为了防止网络请求时间过长而导致界面假死的情况发生。

##### 解决办法

将请求网络资源的代码使用Thread去操作。在Runnable中做HTTP请求，不用阻塞UI线程。


### 待解决问题
- [ ] tool bar返回按钮设置
- [ ] TextView下划线
- [ ] button right arrow
- [ ] edit text 空白输入框
- [ ] 热点查询结果网格分区
- [ ] map显示
- [ ] map下方附近地点显示
- [ ] 搜索结果listview显示
- [ ] listview 随滑动变长
- [ ] qq、微信、微博分享功能
- [ ] mData.get(0).get("texi_record_loc").toString()


## 待完善功能

- [x] 用户注册根据数据库判断是否重复
- [ ] 找回密码功能

## 使用第三方

### 百度地图sdk

*****

### bmob后端云服务平台

*****

用于发送验证短信，

### LeanCloud

*****

用于数据存储，免费或收费，免费数据API请求30,000／天，HTTP文件流量 500 MB/天，开发够用

领先的 BaaS 提供商，为移动开发提供强有力的后端支持

#### 一站式后端云服务
包括云存储、数据分析、用户关系、消息推送、即时通信等现代应用基础模块，满足移动开发所有需求。
#### 高效开发
提供全平台 SDK 支持，帮助各平台开发者快速集成，研发与业务高效联动，让产品迅速到达市场。


### 心知天气

*****

用于天气预报，免费或收费，免费访问量限额：400次/小时，开发够用

#### 天气实况

获取指定城市的天气实况。付费用户可获取全部数据，免费用户只返回天气现象文字、代码和气温3项数据。注：中国城市暂不支持云量和露点温度。

#### 请求地址示例
https://api.seniverse.com/v3/weather/now.json?key=qnr3not5ghiemnec&location=beijing&language=zh-Hans&unit=c
#### 参数

key

- 你的API密钥|

location

- 所查询的位置

参数值范围：

- 城市ID 例如：location=WX4FBXXFKE4F
- 城市中文名 例如：location=北京
- 省市名称组合 例如：location=辽宁朝阳、location=北京朝阳
- 城市拼音/英文名 例如：location=beijing（如拼音相同城市，可在之前加省份和空格，例：shanxi yulin）
- 经纬度 例如：location=39.93:116.40（纬度前经度在后，冒号分隔）
- IP地址 例如：location=220.181.111.86（某些IP地址可能无法定位到城市）
- “ip”两个字母 自动识别请求IP地址，例如：location=ip

language

- 语言 (可选)
- 参数值范围：[点此查看!](https://www.seniverse.com/doc#language)

unit

- 单位 (可选)
- 参数值范围：
 - c 当参数为c时，温度c、风速km/h、能见度km、气压mb
 - f 当参数为f时，温度f、风速mph、能见度mile、气压inch
 - 默认值：c

#### 返回结果  200

```
{
  "results": [{
  "location": {
      "id": "C23NB62W20TF",
      "name": "西雅图",
      "country": "US",
      "timezone": "America/Los_Angeles",
      "timezone_offset": "-07:00"
  },
  "now": {
      "text": "多云", //天气现象文字
      "code": "4", //天气现象代码
      "temperature": "14", //温度，单位为c摄氏度或f华氏度
      "feels_like": "14", //体感温度，单位为c摄氏度或f华氏度
      "pressure": "1018", //气压，单位为mb百帕或in英寸
      "humidity": "76", //相对湿度，0~100，单位为百分比
      "visibility": "16.09", //能见度，单位为km公里或mi英里
      "wind_direction": "西北", //风向文字
      "wind_direction_degree": "340", //风向角度，范围0~360，0为正北，90为正东，180为正南，270为正西
      "wind_speed": "8.05", //风速，单位为km/h公里每小时或mph英里每小时
      "wind_scale": "2", //风力等级，请参考：http://baike.baidu.com/view/465076.htm
      "clouds": "90", //云量，范围0~100，天空被云覆盖的百分比 #目前不支持中国城市#
      "dew_point": "-12" //露点温度，请参考：http://baike.baidu.com/view/118348.htm #目前不支持中国城市#
  },
  "last_update": "2015-09-25T22:45:00-07:00" //数据更新时间（该城市的本地时间）
  }]
}
```


### citypicker

https://github.com/crazyandcoder/citypicker

#### 前言
在实际的项目中需要使用到省市区三级联动的功能，在网上找来找去，都没有找到一个合适的库， 所以自己就封装了一个，不需要自己添加数据源，直接引用即可，一行代码搞定城市选择。怎么简单，怎么方便，怎么来，就是这么任性！

#### 亮点

 1. 无需自己配置省市区域的数据，不需要再进行解析之类的繁杂操作，只需引用即可，结果返回省市区和邮编等四项数据信息，如果不满意样式的话可以自己修改源码！
 2. 多种样式选择，高仿iOS滚轮实现以及列表选择。

#### 效果预览

![](http://img.blog.csdn.net/20170526093653244?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGlqaV94Yw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

#### 应用在实际项目中效果

**1、高仿iOS滚轮实现城市选择器**

![](http://img.blog.csdn.net/20161209211413273?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGlqaV94Yw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)  ![](http://img.blog.csdn.net/20161209211426836?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGlqaV94Yw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) ![](http://img.blog.csdn.net/20161209211442594?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGlqaV94Yw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)




### 百度地图 静态图api

注意中文名要用EFCODE UTF编码

#### 使用条款与限制

- URL长度：2048
- 点标记的数量：50个
- 调用次数：同一个开发者帐号下的HTTP/HTTPS请求，配额、并发共享。
- 默认配额及并发量说明如下：

|分类|	未认证|	个人认证|	企业认证|
|-----|-----|----|-----|
|日配额（次）|	1,000,000|	3,000,000|6,000,000|
|分钟并发量（次/分钟）|	24,000|	30,000|30,000|


#### 服务地址

http://api.map.baidu.com/staticimage/v2

##### 组成说明：

- 域名：http://api.map.baidu.com
- 服务名：staticimage
- 版本号：v2


#### 服务参数列表

|参数名|必选	|默认值|	描述|
|---|---|---|---|
|ak|	是	|无|	用户的访问密钥。支持浏览器端AK和Android/IOS SDK的AK，服务端AK不支持sn校验方式。|
|mcode	|否	|无	|安全码。若为Android/IOS SDK的ak, 该参数必需。|
|width	|否	|400	|图片宽度。取值范围：(0, 1024]。Scale=2,取值范围：(0, 512]。|
|height	|否	|300	|图片高度。取值范围：(0, 1024]。Scale=2,取值范围：(0, 512]。|
|center	|否|	北京	|地图中心点位置，参数可以为经纬度坐标或名称。坐标格式：lng<经度>，lat<纬度>，例如116.43213,38.76623。|
|zoom	|否|	11	|地图级别。高清图范围[3, 18]；低清图范围[3,19]|
|copyright|	否	|pl|	静态图版权样式，0表示log+文字描述样式，1表示纯文字描述样式，默认为0。|
|dpiType	|否|	pl|	手机屏幕类型。取值范围:{ph：高分屏，pl：低分屏(默认)}，高分屏即调用高清地图，低分屏为普通地图。|
|coordtype|	否	|bd09ll|	静态图的坐标类型。支持wgs84ll（wgs84坐标）/gcj02ll（国测局坐标）/bd09ll（百度经纬度）/bd09mc（百度墨卡托）。默认bd09ll（百度经纬度）|
|scale|	否|	null|	返回图片大小会根据此标志调整。取值范围为1或2：1表示返回的图片大小为size= width * height;2表示返回图片为(width*2)*(height *2)，且zoom加1 注：如果zoom为最大级别，则返回图片为（width*2）*（height*2），zoom不变。|
|bbox	|否|	null|	地图视野范围。格式：minX,minY;maxX,maxY。|
|markers	|否	|null	|标注，可通过经纬度或地址/地名描述；多个标注之间用竖线分隔。|
|markerStyles	|否|	null	|与markers有对应关系。markerStyles可设置默认图标样式和自定义图标样式。其中设置默认图标样式时，可指定的属性包括size,label和color；设置自定义图标时，可指定的属性包括url，注意，设置自定义图标时需要先传-1以此区分默认图标。|
|labels	|否|	null	|标签，可通过经纬度或地址/地名描述；多个标签之间用竖线分隔。坐标格式：lng<经度>，lat<纬度>，例如116.43213,38.76623。|
|labelStyles	|否|	null|	标签样式 content, fontWeight,fontSize,fontColor,bgColor, border。与labels一一对应。|
|paths	|否	|null	|折线，可通过经纬度或地址/地名描述；多个折线用竖线"|"分隔；每条折线的点用分号";"分隔；点坐标用逗号","分隔。坐标格式：lng<经度>，lat<纬度>，例如116.43213,38.76623。|
|pathStyles|	否	|null	|折线样式color,weight,opacity[,fillColor]。

