package com.dxdragon.retaildemomode;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.LocaleList;
import android.os.Message;
import android.os.PowerManager;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.os.SystemProperties;
import android.os.BatteryManager;


public class RetailDemoModeService extends Service {

    private static final String TAG = "ccg";

    private static final int DELAY_30_Minutes =  30 * 60 * 1000;
    private static final int DELAY_1_Minute =  60 * 1000;

    private static int mLastBatteryLevel = 0;
    private static int mChargingStatus = -1;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "action=" + intent.getAction());
            // PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (Utils.isRetailDemoMode(context)) {
                switch (intent.getAction()) {
                    case Intent.ACTION_BATTERY_CHANGED : {
                        mLastBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                        mChargingStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                        Log.d(TAG, "mLastBatteryLevel=" + mLastBatteryLevel + ", mChargingStatus=" + mChargingStatus);
                        if(mLastBatteryLevel >= Contants.BATTERY_MAX) {
                            //disable charge control
                            Utils.setChargeStatus(false);
                            Log.d(TAG, "     --set_charging discharging--   ");
                        } else if(mLastBatteryLevel <= Contants.BATTERY_MIN){
                            //enable charge control
                            Utils.setChargeStatus(true);
                            Log.d(TAG, "     ++set_charging charging++   ");
                        }
                        break;
                    }
                    case Intent.ACTION_SCREEN_ON : {
                        if (!RetailDemoModeActivity.isPlaying()) {
                            Log.d(TAG, "playVideo ACTION_SCREEN_ON");
                            Utils.playVideo(context);
                        }
                        break;
                    }
                    case Intent.ACTION_SCREEN_OFF : {
                        if (Utils.isDemoVideoControl(context)) {
                            Utils.playVideo(context);
                            break;
                        }

                        if (RetailDemoModeActivity.isPlaying()) {
                            Log.d(TAG, "playVideo ACTION_SCREEN_OFF");
                            RetailDemoModeActivity.pausePlaying();
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        super.onDestroy();

        Log.d(TAG, "onDestroy");

        Intent mIntent = new Intent();
        mIntent.setClassName("com.dxdragon.retaildemomode", "com.dxdragon.retaildemomode.RetailDemoModeService");
        startService(mIntent);
    }

    private void registerBroadcastReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

}
