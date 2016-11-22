package com.example.areumelec.smartpackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by hbkim on 2016. 11. 10..
 */

public class MainActivity extends AppCompatActivity {

    LinearLayout mn_layout;
    private BackPressCloseHandler backPressCloseHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backPressCloseHandler = new BackPressCloseHandler(this);
        mn_layout = (LinearLayout) findViewById(R.id.mn_layout);

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
}
