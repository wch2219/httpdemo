package com.example.administrator.httpdemo;

import android.app.Application;
import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.easefun.polyvsdk.PolyvDevMountInfo;
import com.easefun.polyvsdk.PolyvDownloaderManager;
import com.easefun.polyvsdk.PolyvSDKClient;
import com.lzy.okgo.OkGo;

import java.io.File;

/**
 * Created by Administrator on 2018/8/30.
 */

public class MyApplication extends MultiDexApplication {
    /*加密秘钥
      */
    private String aeskey = "VXtlHmwfS2oYm0CZ";
    /**
     * 加密向量
     */
    private String iv = "2u9gDPKdX6GyQJKU";
    //使用SDK加密串来配置
    String config = "o9Yr9r/ThWYmezDZktyg4hdccnE5rtHbAVtzvjJJ+EyIuNszyzSq7ssNDLSU2YpV8A8fJmwFskwd9He2iyaT79M8903wSrBC9XlumWhHdS7RVrgj6na56a47qGZ7b9w86Csxqs5oMXgiCpi3/EAO3g==";//后台发的

    @Override
    public void onCreate() {
        super.onCreate();
        OkGo.getInstance().init(this);
        initPolyvCilent();
    }

    public void initPolyvCilent() {
        //网络方式取得SDK加密串，（推荐）
        //网络获取到的SDK加密串可以保存在本地SharedPreference中，下次先从本地获取
//		new LoadConfigTask().execute();
        PolyvSDKClient client = PolyvSDKClient.getInstance();
        //使用SDK加密串来配置
        //String config = "o9Yr9r/ThWYmezDZktyg4hdccnE5rtHbAVtzvjJJ+EyIuNszyzSq7ssNDLSU2YpV8A8fJmwFskwd9He2iyaT79M8903wSrBC9XlumWhHdS7RVrgj6na56a47qGZ7b9w86Csxqs5oMXgiCpi3/EAO3g==";//后台发的
        client.setConfig(config, aeskey, iv, getApplicationContext());
//        client.setConfig("CMWht3MlpVkgpFzrLNAebYi4RdQDY/Nhvk3Kc+qWcck6chwHYKfl9o2aOVBvXVTRZD/14XFzVP7U5un43caq1FXwl0cYmTfimjTmNUYa1sZC1pkHE8gEsRpwpweQtEIiTGVEWrYVNo4/o5jI2/efzA==", aeskey, iv, getApplicationContext());
        //初始化SDK设置
        client.initSetting(getApplicationContext());
        //启动Bugly
        client.initCrashReport(getApplicationContext());
        //启动Bugly后，在学员登录时设置学员id
//		client.crashReportSetUserId(userId);
        //获取SD卡信息
        PolyvDevMountInfo.getInstance().init(this, new PolyvDevMountInfo.OnLoadCallback() {

            @Override
            public void callback() {
                //是否有可移除的存储介质（例如 SD 卡）或内部（不可移除）存储可供使用。
                if (!PolyvDevMountInfo.getInstance().isSDCardAvaiable()) {
                    // TODO 没有可用的存储设备,后续不能使用视频缓存功能
                    Log.e("TAG", "没有可用的存储设备,后续不能使用视频缓存功能");
                    return;
                }

                //可移除的存储介质（例如 SD 卡），需要写入特定目录/storage/sdcard1/Android/data/包名/。
                String externalSDCardPath = PolyvDevMountInfo.getInstance().getExternalSDCardPath();
                if (!TextUtils.isEmpty(externalSDCardPath)) {
                    StringBuilder dirPath = new StringBuilder();
                    dirPath.append(externalSDCardPath).append(File.separator).append("Android").append(File.separator).append("data")
                            .append(File.separator).append(getPackageName()).append(File.separator).append("zgwxdownload");
                    File saveDir = new File(dirPath.toString());
                    if (!saveDir.exists()) {
                        getExternalFilesDir(null); // 生成包名目录
                        saveDir.mkdirs();//创建下载目录
                    }
                    //设置下载存储目录
                    PolyvSDKClient.getInstance().setDownloadDir(saveDir);
                    return;
                }

                //如果没有可移除的存储介质（例如 SD 卡），那么一定有内部（不可移除）存储介质可用，都不可用的情况在前面判断过了。
                File saveDir = new File(PolyvDevMountInfo.getInstance().getInternalSDCardPath() + File.separator + "zgwxdownload");
                if (!saveDir.exists()) {
                    saveDir.mkdirs();//创建下载目录
                }

                //设置下载存储目录
                PolyvSDKClient.getInstance().setDownloadDir(saveDir);
            }
        });

        // 设置下载队列总数，多少个视频能同时下载。(默认是1，设置负数和0是没有限制)
        PolyvDownloaderManager.setDownloadQueueCount(1);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)
            getResources();
        super.onConfigurationChanged(newConfig);
    }

}
