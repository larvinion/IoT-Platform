package com.example.areumelec.smartpackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import com.example.areumelec.Bluetooth.*;

/**
 * Created by hbkim on 2016. 11. 10..
 */

public class MainActivity extends AppCompatActivity {

    LinearLayout mn_layout;
    private BackPressCloseHandler backPressCloseHandler;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    public static BluetoothService btService = null;
    private final Handler mHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backPressCloseHandler = new BackPressCloseHandler(this);
        mn_layout = (LinearLayout) findViewById(R.id.mn_layout);

        if (btService == null) {// BluetoothService가 없으면 생성
            btService = new BluetoothService(this, mHandler);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("notice");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.streaming:
                //camera activity로 가는 인텐트를 생성
                Intent intent = new Intent(getApplicationContext(), CameraDemo.class);
                //액티비티 시작!
                startActivity(intent);
                break;
            case R.id.sensorcheck:
                //SensorCheck activity로 가는 인텐트 생성
                Intent intent2 = new Intent(getApplicationContext(), SensorDemo.class);
                //액티비티 시작!
                startActivity(intent2);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:// 블루투스 사용이 허용되면
                if (resultCode == Activity.RESULT_OK) {// 블루투스 사용을 허용하면
                    Toast.makeText(this, "Bluetooth on", Toast.LENGTH_SHORT).show();
                    btService.scanDevice();// 주변 기기 검색
                } else {// 블루투스 사용을 거부했을 경우
                    Toast.makeText(this, "Bluetooth is not enabled",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CONNECT_DEVICE:// 기기와 연결요청을 하면
                if (resultCode == Activity.RESULT_OK) {
                    btService.getDeviceInfo(data);// 기기 정보를 가져옴
                    Intent intent = new Intent(getApplicationContext(),	MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
        }

    }

    public void startButton(View target) {
        // 블루투스 사용 요청 창 호출
        if (btService.getDeviceState()) {
            btService.enableBluetooth();
        } else {
            finish();
        }
    }
}
