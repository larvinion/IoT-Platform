package com.example.areumelec.smartpackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Intro extends Activity {
    //인트로 화면
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Intro.this,
                        ActivitySelect.class);//기능선택 화면으로 이동
                startActivity(intent);
                finish();
            }
        }, 2000);//2초간 실행
    }
}