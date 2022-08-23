package com.example.androidunidemo

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import io.dcloud.feature.sdk.DCSDKInitConfig
import io.dcloud.feature.sdk.DCUniMPSDK
import io.dcloud.feature.sdk.MenuActionSheetItem
import java.util.*

/**
 * Created by ccs 2022/8/10 9:29
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initUniMPSdk()
    }

    private fun initUniMPSdk() {
        //初始化 uni小程序SDK ----start----------
        val item = MenuActionSheetItem("关于", "gy")
        val item1 = MenuActionSheetItem("获取当前页面url", "hqdqym")
        val item2 = MenuActionSheetItem("跳转到宿主原生测试页面", "gotoTestPage")
        val sheetItems: MutableList<MenuActionSheetItem> = ArrayList()
        sheetItems.add(item)
        sheetItems.add(item1)
        sheetItems.add(item2)
        val config = DCSDKInitConfig.Builder()
            .setCapsule(true)//false : 隐藏胶囊按钮 true: 显示胶囊按钮
            .setMenuDefFontSize("16px")
            .setMenuDefFontColor("#ff00ff")
            .setMenuDefFontWeight("normal")
            .setMenuActionSheetItems(sheetItems)
            .setEnableBackground(true)//开启多任务模式
            .build()
        DCUniMPSDK.getInstance().initialize(this, config) {
            Log.i("Application==", "onInitFinished----: $it")
        }
        //初始化 uni小程序SDK ----end----------
        val uniPath = DCUniMPSDK.getInstance().getAppBasePath(this)
        Log.i("Application==", "uniPath----: $uniPath")
    }

    override fun attachBaseContext(base: Context?) {
        MultiDex.install(base)
        super.attachBaseContext(base)
    }
}