package com.dxdragon.retaildemomode;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.os.UpdateEngineCallback;
import android.os.UpdateEngine;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.content.Context;
import android.os.SystemProperties;
import android.os.BatteryManager;
import android.content.Intent;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import com.dxdragon.retaildemomode.R;
import android.telephony.TelephonyManager;

public class FactoryReset extends Activity {
    private final String TAG="FactoryReset";
    private TextView tBatteryWarning;

    private Context mContext;

    private Button mCancel;
    private Button mConfirm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.factory_reset);
      mContext = this;
      initView();
      initAction();

    }

    @Override
    public void onResume(){
      super.onResume();
      /*tBatteryWarning = (TextView)findViewById(R.id.BatteryWarning);
      tBatteryWarning.setVisibility(View.INVISIBLE);
      BatteryManager batteryManager = (BatteryManager)getSystemService(Context.BATTERY_SERVICE);
      int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
      Log.d(TAG,"battery="+battery);        
      if(battery<30) {
        tBatteryWarning.setVisibility(View.VISIBLE);
        return;
      }*/

    }

    @Override
    public void onPause(){
      super.onPause();
      finish();
    }    

    private void initView() {
      mCancel = (Button) findViewById(R.id.cancel_btn);
      mConfirm = (Button) findViewById(R.id.confirm_btn);

    }

    private void initAction() {
      mCancel.setOnClickListener(v -> {
        finish();
      });
      mConfirm.setOnClickListener(v -> {
        /*Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
          intent.setPackage("android");
          intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
          sendBroadcast(intent);*/
          //am start -a com.wiz.settings.reset_factory ok
          Intent resetIntent = new Intent("com.wiz.settings.reset_factory");
          resetIntent.setPackage("com.android.settings");
          resetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          mContext.startActivity(resetIntent);
      });
    }

}
