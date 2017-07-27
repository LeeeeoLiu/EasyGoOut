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


