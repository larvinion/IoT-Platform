package com.example.areumelec.smartpackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.messaging.FirebaseMessaging;

public class ActivitySelect extends AppCompatActivity {
    //메인 액티비티, 기능 선택
    LinearLayout mn_layout;
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_select);

        Intent intent = getIntent();

        backPressCloseHandler = new BackPressCloseHandler(this);

        mn_layout = (LinearLayout)findViewById(R.id.mn_layout);
        FirebaseMessaging.getInstance().subscribeToTopic("notice");
    }

    public void startStreaming(View v){
        //camera 버튼 클릭 시
        Intent intent = new Intent(ActivitySelect.this, Camera.class);
        startActivity(intent);
    }

    public void sensorCheck(View v){
        //sensor 버튼 클릭 시
        Intent intent = new Intent(ActivitySelect.this, SensorCheck.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {//뒤로가기 두번 눌러야 어플 종료되도록
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.streaming:
                //camera activity로 가는 인텐트를 생성
                Intent intent = new Intent(getApplicationContext(), Camera.class);
                //액티비티 시작!
                startActivity(intent);
                break;
            case R.id.sensorcheck:
                //SensorCheck activity로 가는 인텐트 생성
                Intent intent2 = new Intent(getApplicationContext(), SensorCheck.class);
                //액티비티 시작!
                startActivity(intent2);
        }
    }

//    if (getIntent().getExtras() != null) {
//        for (String key : getIntent().getExtras().keySet()) {
//            Object value = getIntent().getExtras().get(key);
//            Log.d(TAG, "Key: " + key + " Value: " + value);
//        }
//    }
//    // [END handle_data_extras]
//
//    Button subscribeButton = (Button) findViewById(R.id.subscribeButton);
//    subscribeButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            // [START subscribe_topics]
//            FirebaseMessaging.getInstance().subscribeToTopic("news");
//            // [END subscribe_topics]
//
//            // Log and toast
//            String msg = getString(R.string.msg_subscribed);
//            Log.d(TAG, msg);
//            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//        }
//    });
//
//    Button logTokenButton = (Button) findViewById(R.id.logTokenButton);
//    logTokenButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            // Get token
//            String token = FirebaseInstanceId.getInstance().getToken();
//
//            // Log and toast
//            String msg = getString(R.string.msg_token_fmt, token);
//            Log.d(TAG, msg);
//            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//        }
//    });
}
