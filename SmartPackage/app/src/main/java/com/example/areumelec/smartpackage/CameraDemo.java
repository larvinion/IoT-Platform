package com.example.areumelec.smartpackage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;

public class CameraDemo extends Activity implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // SharedPreferences에 저장할 때 key 값으로 사용됨.
    public static final String PROPERTY_REG_ID = "registration_id";

    // SharedPreferences에 저장할 때 key 값으로 사용됨.
    private static final String PROPERTY_APP_VERSION = "4.3";
//    private static final String TAG = "ICELANCER";

    String SENDER_ID = "SmartPackage-142611";

    SharedPreferences prefs;
    Context context;

    String regid;
    private TextView mDisplay;


    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private String path;
    private int ip;
    private String ipAddress;
    private String port;
    private Bundle extras;

    private ProgressBar progressBar;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;

    private WifiManager wifiManager;
    private WifiInfo wifiInfo;

    private static final String TAG = "MediaPlayer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        ip = wifiInfo.getIpAddress();
//        ipAddress = String.format("%d.%d.%d.%d",(ip & 0xff),(ip >> 8 & 0xff),(ip >> 16 & 0xff),0x01);
        ipAddress = "192.168.0.62";
        port = "8554";

        if (!LibsChecker.checkVitamioLibs(this))
            return;
        mPreview = (SurfaceView) findViewById(R.id.display);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBA_8888);
        extras = getIntent().getExtras();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


    }

    private void playVideo() {
        doCleanUp();
        try {

            if (path == "") {
                // Tell the user to provide a media file URL.
                Toast.makeText(CameraDemo.this, "Please edit MainActivity," + " and set the path variable to your media file URL.", Toast.LENGTH_LONG).show();
                return;
            }
//          path = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
            path = "rtsp://"+ipAddress+":"+port+"/test";


            // Create a new media player and set the listeners
            mMediaPlayer = new MediaPlayer(this);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
//            setVolumeControlStream(AudioManager.STREAM_MUSIC);

            ///////////////////////////버퍼링되기 전까지 progressbar돌아가게하는 거/////////
            new Thread() {
                @Override
                public void run() {
                    while (!mMediaPlayer.isPlaying()) {
                        Log.d(TAG, "isPlaying : " + mMediaPlayer.isPlaying() + " " + path);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, "isPlaying : " + mMediaPlayer.isPlaying());
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }
            }.start();

        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }

    }



    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(ProgressBar.GONE);
        }
    };

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "onBufferingUpdate percent:" + percent);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated called");
        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged called");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed called");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion called");
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.v(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        mMediaPlayer.start();
    }

}
