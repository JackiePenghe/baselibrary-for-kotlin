# BaseSample
BaseSample

当前最新版本：[![](https://jitpack.io/v/com.gitee.sscl/baselibrary-for-kotlin.svg)](https://jitpack.io/#com.gitee.sscl/baselibrary-for-kotlin)

lasts version now:[![](https://jitpack.io/v/com.gitee.sscl/baselibrary-for-kotlin.svg)](https://jitpack.io/#com.gitee.sscl/baselibrary-for-kotlin)

```xml
allprojects {
	repositories {
		...
        maven { url 'https://jitpack.io' }
	}
}
```

```xml
dependencies {
    //version is release tag
    implementation 'com.gitee.sscl:baselibrary-for-kotlin:version'
    //用到AutoSwipeRefreshLayout时需要附加依赖库
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
    //BaseSplashActivity需要引入SplashScreen库
    implementation 'androidx.core:core-splashscreen:1.0.0'
}
```

用到BaseDataBindingAppCompatActivity时需要在主module的buid.gradle中开启dataBinding

```build
    android {
        ...
        //开启dataBinding
        dataBinding {
            enabled = true
        }
        ...
    }
```

使用DataBinding一般会同时使用ViewModel

```xml
     /* * * * * * * * * * * * * * * * * * * ViewModel库开始 * * * * * * * * * * * * * * * * * * */
    //ViewModel库基本
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1'
    //ViewModel库高级
    implementation "androidx.activity:activity-ktx:1.3.1"
    /* * * * * * * * * * * * * * * * * * * ViewModel库结束 * * * * * * * * * * * * * * * * * * */
```

# 使用说明

## Activity

以下Activity基类均继承自 androidx.appcompat.app.AppCompatActivity

## BaseAppCompatActivity

封装Activity一些基本的使用流程，使用com.google.android.material.appbar.AppBarLayout + androidx.appcompat.widget.Toolbar替代Activity默认的ActionBar
使用时需要注意在style文件中配置以下属性
具体使用方法请下载源码参考

```xml
     <item name="windowActionBar">false</item>
     <item name="windowNoTitle">true</item>
```

![BaseAppCompatActivity示例](samplePicture/SampleBaseAppcompatActivity.png)


## BaseDataBindingAppCompatActivity

用法与 BaseAppCompatActivity 完全相同，但加入的关于databinding的特性，具体使用方法请下载源码参考，加上ViewModel与LiveData组合实现通过输入框的内容更改文本内容
![SampleDataBindingActivity](samplePicture/SampleDataBindingActivity.mp4)

## BaseDrawerActivity

基于google官方的DrawerLayout示例修改，可方便的集成使用DrawerLayout + NavigationView
