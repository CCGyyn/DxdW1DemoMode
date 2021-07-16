package com.dxdragon.retaildemomode;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;

import android.text.TextUtils;
import java.net.FileNameMap;
import java.net.URLConnection;
import android.util.Log;
import android.os.SystemProperties;

public class Utils {

    private static final String TAG = "ccg";

    private final static String PREFIX_VIDEO="video/";

	public static boolean isRetailDemoMode(Context context) {
        return (Settings.Secure.getInt(context.getContentResolver(), Contants.VZW_RETAIL_DEMO_MODE, 0) == 1);
    }

    public static boolean isDemoVideoControl(Context context) {
        return (Settings.Secure.getInt(context.getContentResolver(), Contants.VZW_DEMO_VIDEO_CONTROL, 0) == 1);
    }

    public static void setDemoVideoControl(Context context, boolean on) {
        Settings.Secure.putInt(context.getContentResolver(), Contants.VZW_DEMO_VIDEO_CONTROL, on ? 1:0);
    }

    public static void playVideo(Context context) {
        Intent videoIntent = new Intent();
        videoIntent.setClassName("com.dxdragon.retaildemomode", "com.dxdragon.retaildemomode.RetailDemoModeActivity");
        videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(videoIntent);
    }

    public static int getScreenOffTime(Context context) {
        int screenOffTime = 0;
        try {
            screenOffTime = Settings.System.getInt(context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Exception localException) {
        }
        return screenOffTime;
    }

    private static void echoExecute(int num, String file) {
        try {
            BufferedWriter bufWriter = null;
            bufWriter = new BufferedWriter(new FileWriter(file));
            bufWriter.write(String.valueOf(num));
            bufWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get the Mime Type from a File
     * @param fileName 文件名
     * @return 返回MIME类型
     * thx https://www.oschina.net/question/571282_223549
     * add by fengwenhua 2017年5月3日09:55:01
     */
    private static String getMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(fileName);
        return type;
    }

    /**
     * 根据文件后缀名判断 文件是否是视频文件
     * @param fileName 文件名
     * @return 是否是视频文件
     */
    public static boolean isVedioFile(String fileName){
        String mimeType = getMimeType(fileName);
        if (!TextUtils.isEmpty(fileName)&&mimeType.contains(PREFIX_VIDEO)){
            return true;
        }
        return false;
    }

    public static void setChargeStatus(boolean on) {
        if (on) {
            SystemProperties.set(Contants.INPUT_SUSPEND_CONTROL_PROPERTY, "1");
        } else {
            SystemProperties.set(Contants.INPUT_SUSPEND_CONTROL_PROPERTY, "0");
        }
    }

}