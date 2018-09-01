package com.example.administrator.httpdemo.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 存储工具类
 * @author Lion 2018-6-12
 */
public class PolyvStorageUtils {

    private final static String MOUNTED = "mounted";

    /**
     * 获取可使用的外部存储目录<br/>
     * https://developer.android.com/guide/topics/data/data-storage?hl=zh-cn#filesExternal
     * @param context
     * @return 外部存储目录列表
     */
    @NonNull
    public static ArrayList<File> getExternalFilesDirs(@NonNull Context context) {
        File[] files;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            files = context.getExternalFilesDirs(null);
        } else {
            files = ContextCompat.getExternalFilesDirs(context, null);
        }

        ArrayList<File> storageList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //判断存储设备可用性
            for (File file : files) {
                if (file != null) {
                    String state = Environment.getExternalStorageState(file);
                    if (MOUNTED.equals(state)) {
                        storageList.add(file);
                    }
                }
            }
        } else {
            storageList.addAll(Arrays.asList(files));
        }

        return storageList;
    }
}
