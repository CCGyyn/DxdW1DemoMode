package com.dxdragon.retaildemomode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.io.File;
import java.util.List;
import android.os.SystemProperties;

public class RetailDemoModeReceiver extends BroadcastReceiver {

    private static final String TAG = "RetailDemoModeReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            if (Utils.isRetailDemoMode(context)) {
                boolean isChargeViewOn =  SystemProperties.get(Contants.SYSTEM_UI_CHARGE_VIEW_CONTROL,"0").equals("0");
                Utils.setChargeStatus(false);
                if (isChargeViewOn) {
                    SystemProperties.set(Contants.SYSTEM_UI_CHARGE_VIEW_CONTROL, "1");
                }
                Log.d(TAG, "isChargeViewOn = " + isChargeViewOn + " service.factory.state = " + SystemProperties.get(Contants.SYSTEM_UI_CHARGE_VIEW_CONTROL,"0"));
                Utils.setChargeStatus(true);
                Intent startService = new Intent();
                startService.setClassName("com.dxdragon.retaildemomode", "com.dxdragon.retaildemomode.RetailDemoModeService");
                context.startService(startService);

                Utils.playVideo(context);
            } /*else {
                PackageManager pm = context.getPackageManager();
                pm.setApplicationEnabledSetting("com.dxdragon.retaildemomode",
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
            }*/
        }

    }


}