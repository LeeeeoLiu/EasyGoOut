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

### 待解决问题

- [x] Android:label居中显示  
 - 主要原理是，在ToolBar中添加一个TextView，然后你就可以通过设置TextView的样式，来达到你想要的效果。
 
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


- 报错

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

## 待完善功能

- [ ] 用户注册根据数据库判断是否重复

## 使用第三方

### 百度地图sdk

### bmob后端云服务平台

### LeanCloud

