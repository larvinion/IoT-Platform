package com.example.areumelec.smartpackage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class SensorCheck extends AppCompatActivity {
    private static final String ip = "192.168.44.1";
    private static final int port = 5002;
    protected DatagramSocket ds = null;
    protected Thread socketThread = null;
    private boolean change = true;
    private String sndOpkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_check);
    }


    public class Client implements Runnable {

        @Override
        public void run() {
            try {
                ds = new DatagramSocket();
                SocketAddress socketAddress = new InetSocketAddress(ip, port);
                byte[] buffer = sndOpkey.getBytes();
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length, socketAddress);
                ds.send(dp);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    socketThread = new Thread(new Client());
    socketThread.start();
    socketThread = null;

}
