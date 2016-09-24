package com.example.areumelec.smartpackage;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;

public class SensorCheck extends AppCompatActivity {
    private Handler mainHandler;
    private SocketListener sl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_check);

        this.getMessage();
        this.request();
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMessage()
    {
        sl = new SocketListener(getApplicationContext(), mainHandler);
        sl.start();
    }

    public void sendMsg(String msg) throws IOException {
        SocketManager.sendMsg(msg);
    }

    public void closeSocket() throws IOException {
        SocketManager.closeSocket();
    }

    public void request() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMsg("sensor");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}