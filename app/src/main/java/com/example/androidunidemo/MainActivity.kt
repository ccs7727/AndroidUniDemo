package com.example.androidunidemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.androidunidemo.util.DownloadUtil
import io.dcloud.feature.sdk.DCUniMPSDK
import io.dcloud.feature.sdk.Interface.IUniMP
import io.dcloud.feature.unimp.config.UniMPOpenConfiguration
import io.dcloud.feature.unimp.config.UniMPReleaseConfiguration
import org.json.JSONObject
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {

    /**
     * unimp小程序实例缓存
     */
    var mUniMPCaches = HashMap<String, IUniMP>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnJump = findViewById<Button>(R.id.btnJump)
        btnJump.setOnClickListener {
            try {
                val uniMPOpenConfiguration = UniMPOpenConfiguration()
                uniMPOpenConfiguration.extraData.put("MSG", "Hello DCUniMPConfiguration")
                val uniMP = DCUniMPSDK.getInstance().openUniMP(this, "__UNI__04E3A11", uniMPOpenConfiguration)
                mUniMPCaches[uniMP.appid] = uniMP
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val btnJump2 = findViewById<Button>(R.id.btnJump2)
        btnJump2.setOnClickListener {
            try {
                val uniMP = DCUniMPSDK.getInstance().openUniMP(this, "__UNI__4138A06")
                mUniMPCaches[uniMP.appid] = uniMP
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val btnJump3 = findViewById<Button>(R.id.btnJump3)
        btnJump3.setOnClickListener {
            try {
                val uniMP = DCUniMPSDK.getInstance().openUniMP(this, "__UNI__B61D13B")
                mUniMPCaches[uniMP.appid] = uniMP
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //远程下载加密wgt 并安装


        //远程下载加密wgt 并安装
        val btnJump4 = findViewById<Button>(R.id.btnJump4)
        btnJump4.setOnClickListener {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1002
            )
            // 远程下载并且安装
            updateWgt()
        }

        //获取运行时路径
        val btnJump5 = findViewById<Button>(R.id.btnJump5)

        btnJump5.setOnClickListener {
            val info = DCUniMPSDK.getInstance().getAppVersionInfo("__UNI__B61D13B")
            if (info != null) {
                Log.e("unimp", "info===$info")
            }

            val path = DCUniMPSDK.getInstance().getAppBasePath(this)
            val btnJump5Text = findViewById<TextView>(R.id.btnJump5Text)
            btnJump5Text.text = "info===$info\npath===$path"
        }

        initListener()

        checkPermission()
    }

    private fun initListener() {
        //用来测试sdkDemo 胶囊×的点击事件，是否生效；lxl增加的
        DCUniMPSDK.getInstance().setCapsuleCloseButtonClickCallBack { appid: String? ->
//            Toast.makeText(this, "点击×号了", Toast.LENGTH_SHORT).show()
            if (mUniMPCaches.containsKey(appid)) {
                val mIUniMP = mUniMPCaches[appid]
                if (mIUniMP != null && mIUniMP.isRuning) {
                    mIUniMP.closeUniMP()
                    mUniMPCaches.remove(appid)
                }
            }
        }

        DCUniMPSDK.getInstance().setDefMenuButtonClickCallBack { appid: String, id: String? ->
            when (id) {
                "gy" -> {
                    Log.e("unimp", "点击了关于$appid")
                    //宿主主动触发事件
                    val data = JSONObject()
                    try {
                        val uniMP = mUniMPCaches[appid]
                        if (uniMP != null) {
                            data.put("sj", "点击了关于")
                            uniMP.sendUniMPEvent("gy", data)
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                "hqdqym" -> {
                    val uniMP = mUniMPCaches[appid]
                    if (uniMP != null) {
                        Log.e("unimp", "当前页面url=" + uniMP.currentPageUrl)
                    } else {
                        Log.e("unimp", "未找到相关小程序实例")
                    }
                }
                "gotoTestPage" -> {
                    val intent = Intent()
                    intent.setClassName(this, "com.example.unimpdemo.TestPageActivity")
                    DCUniMPSDK.getInstance().startActivityForUniMPTask(appid, intent)
                }
            }
        }
    }

    /**
     * 模拟更新wgt
     */
    private fun updateWgt() {
        val wgtUrl = "http://81.70.104.12:8006/examples/__UNI__E0FD4CB_en.wgt"
        val wgtName = "__UNI__E0FD4CB_en.wgt"
        val downFilePath = externalCacheDir!!.path
        val uiHandler = Handler()
        DownloadUtil.get().download(this@MainActivity, wgtUrl, downFilePath, wgtName, object : DownloadUtil.OnDownloadListener {
            override fun onDownloadSuccess(file: File) {
                Log.e("unimp", "onDownloadSuccess --- file === " + file.path)
                Log.e("unimp", "onDownloadSuccess --- file length === " + file.length())
                val uniMPReleaseConfiguration = UniMPReleaseConfiguration()
                uniMPReleaseConfiguration.wgtPath = file.path
                uniMPReleaseConfiguration.password = "789456123"
                uiHandler.post {
                    DCUniMPSDK.getInstance().releaseWgtToRunPath("__UNI__E0FD4CB", uniMPReleaseConfiguration) { code, pArgs ->
                        Log.e("unimp", "code ---  $code  pArgs --$pArgs")
                        if (code == 1) {
                            //释放wgt完成
                            try {
                                DCUniMPSDK.getInstance().openUniMP(this@MainActivity, "__UNI__E0FD4CB")
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            //释放wgt失败
                        }
                    }
                }
            }

            override fun onDownloading(progress: Int) {}
            override fun onDownloadFailed() {
                Log.e("unimp", "downFilePath  ===  onDownloadFailed")
            }
        })
    }

    /**
     * 检查并申请权限
     */
    fun checkPermission() {
        var targetSdkVersion = 0
        val PermissionString = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        try {
            val info = this.packageManager.getPackageInfo(this.packageName, 0)
            targetSdkVersion = info.applicationInfo.targetSdkVersion //获取应用的Target版本
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Build.VERSION.SDK_INT是获取当前手机版本 Build.VERSION_CODES.M为6.0系统
            //如果系统>=6.0
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                //第 1 步: 检查是否有相应的权限
                val isAllGranted = checkPermissionAllGranted(PermissionString)
                if (isAllGranted) {
                    Log.e("err", "所有权限已经授权！")
                    return
                }
                // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
                ActivityCompat.requestPermissions(this, PermissionString, 1)
            }
        }
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private fun checkPermissionAllGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                //Log.e("err","权限"+permission+"没有授权");
                return false
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mUniMPCaches.clear()
    }
}