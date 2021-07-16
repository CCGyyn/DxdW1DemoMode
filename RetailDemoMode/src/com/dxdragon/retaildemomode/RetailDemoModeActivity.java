package com.dxdragon.retaildemomode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.os.SystemClock;
import java.io.File;
import java.io.IOException;
import android.provider.Settings;

@SuppressLint("NewApi")
public class RetailDemoModeActivity extends Activity{

    private static final String TAG = "RetailDemoModeActivity";
    private Uri demoUri;
    private RelativeLayout parent;
    private static VideoView videoView;

    private PowerManager.WakeLock mLock = null;
    PowerManager mPowerManager;
    private final int DELAY_TIME = 60*60*1000;
    private final int DELAY_TEN_SECOND = 10*1000 * 3;
    private final int MSG_ONE_HOUR_COUNT_DOWN = 1;
    private final int MSG_AGAIN_ONE_HOUR_COUNT_DOWN = 2;
    private final int SET_SCREEN_OFF_TIMEOUT= 8*1000;

    private Context mContext;

    //handler 方式定时循环
    private Handler handlerTimer = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case MSG_ONE_HOUR_COUNT_DOWN:
                    pause();
                    release();
                    Utils.setDemoVideoControl(mContext, false);
                    //screen off
                    mPowerManager.goToSleep(SystemClock.uptimeMillis());
                    stop();
                    break;
                case MSG_AGAIN_ONE_HOUR_COUNT_DOWN:
                    start();
                    sendCountDownMsg();
                    wakeup();
                    break;
                default:
                    break;
            }

        }
    };

    private void wakeup() {
        Log.d("ccg","wakeup");
        if (mLock == null) {
            mLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass().getName());
            Log.d(TAG,"isHeld=" + mLock.isHeld());
            if (!mLock.isHeld()) {
                Log.d("ccg","wakeup acquire");
                mLock.acquire();
            }
        }
        /*KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(KEYGUARD_SERVICE);
        keyguardManager.requestDismissKeyguard(this, null);*/
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void release() {
        Log.d("ccg","release");
        if (mLock != null) {
            if (mLock.isHeld()) {
                Log.d("ccg","release release");
                mLock.release();
                mLock = null;
            }
        }
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    protected void onCreate(Bundle paramBundle) {
        Log.d(TAG,"onCreate");
        super.onCreate(paramBundle);

        setShowWhenLocked(true);
        setTurnScreenOn(true);

        setContentView(R.layout.activity_retail_demo_mode);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mContext = this;

        videoView = ((VideoView) findViewById(R.id.videoView));
        parent = ((RelativeLayout) findViewById(R.id.parent));

        parent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (isPlaying()) {
                    pause();
                    try {
                        Settings.System.putInt(mContext.getContentResolver(),
                                Settings.System.SCREEN_OFF_TIMEOUT, SET_SCREEN_OFF_TIMEOUT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stop();
                } else {
                    start();
                }
            }
        });

        /*demoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.demo);
        Log.d(TAG, "demoUri " + demoUri.toString());
        videoView.setVideoURI(demoUri);*/

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "video play prepared.");
                mp.setLooping(true);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "video play end.");
                finish();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i(TAG, "error occurs when play video, what = " + what + " , extra = " + extra);
                return false;
            }
        });

        //sendCountDownMsg();
    }

    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        stop();
        handlerTimer.removeCallbacksAndMessages(null);
    }

    public boolean onKeyDown(int keyCode, KeyEvent paramKeyEvent) {
        Log.d( TAG,"onKeyDown -- keyCode:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            //重置定时器
            handlerTimer.removeMessages(MSG_ONE_HOUR_COUNT_DOWN);
            sendCountDownMsg();

        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            // stop();
        }
        return true;
    }

    protected void onPause() {
        Log.d(TAG,"onPause");
        super.onPause();
        handlerTimer.removeMessages(MSG_ONE_HOUR_COUNT_DOWN);
        release();
    }

    protected void onResume() {
        Log.d(TAG,"onResume");
        setDemoVideoPath();
        super.onResume();
        wakeup();
        start();
        Utils.setDemoVideoControl(mContext, true);
        sendCountDownMsg();
    }

    private void sendCountDownMsg() {
        Message msg = new Message();
        msg.what = MSG_ONE_HOUR_COUNT_DOWN;
        Log.d("ccg","getScreenOffTime=" + Utils.getScreenOffTime(this));
        handlerTimer.sendMessageDelayed(msg, DELAY_TIME);
    }

    public void start() {
        Log.d("ccg", "start");
        if (!videoView.isPlaying()) {
            videoView.start();
        }
    }

    private static void pause() {
        Log.d("ccg", "pause");
        videoView.pause();
    }

    public void stop() {
        Log.d("ccg", "stop");
        videoView.stopPlayback();
        // Utils.setDemoVideoControl(this, false);
        finish();
    }

    public static boolean isPlaying() {
        if (videoView != null)
            return videoView.isPlaying();
        else
            return false;
    }

    public static void pausePlaying() {
        if (videoView != null) {
            if (videoView.isPlaying()) {
                pause();
            }
        }
    }

    private void setDemoVideoPath() {
        String path = "/sdcard/DCIM";
        File dir = new File(path);
        String[] arr = dir.list();
        for (String str : arr) {
            Log.d(TAG,"str=" + str);
            if(str.startsWith("demo") && Utils.isVedioFile(str)) {
                String videoPath = path + "/" + str;
                File videoFile = new File(videoPath);
                Log.d(TAG,"videoFile.exists=" + videoFile.exists());
                if (!videoFile.exists()) {
                    continue;
                }
                videoView.setVideoPath(videoPath);
                return ;
            }
        }
        demoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.demo);
        videoView.setVideoURI(demoUri);
    }

}
