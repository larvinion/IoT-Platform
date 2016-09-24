package com.example.areumelec.Model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by hbkim on 2016. 9. 24..
 */
public class SocketListener extends Thread{

    private InputStream im;
    private BufferedReader br;

    Handler mHandler;

    Context context;

    public SocketListener(Context context, Handler handler) {
        this.mHandler = handler;
        this.context = context;

        try {
            im = SocketManager.getSocket().getInputStream();
            br = new BufferedReader(new InputStreamReader(im));
        } catch (IOException e) {
            Log.e("SocketListener", e.getMessage());
        }
    }

    @Override
    public void run() {
        super.run();

        while(true)
        {
            try {
                String receivedmsg;
                while((receivedmsg = br.readLine())!= null)
                {
                    Log.e("SocketListener", receivedmsg);
                    Message msg = Message.obtain(mHandler, 0,0,0,receivedmsg);
                    mHandler.sendMessage(msg);
                }
            } catch (IOException e) {
                Log.e("SocketListener",e.getMessage());
            }
        }

    }
}
