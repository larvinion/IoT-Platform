package com.example.areumelec.smartpackage;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * Created by hbkim on 2016. 9. 24..
 */
public class SocketManager {

    private static final String IP = "192.168.44.1";
    private static final int PORT = 5002;

    private static Socket socket;

    public static Socket getSocket() throws IOException
    {
        if( socket == null)
            socket = new Socket();

        if( !socket.isConnected() )
            socket.connect(new InetSocketAddress(IP, PORT));

        return socket;
    }

    public static void closeSocket() throws IOException
    {
        if ( socket != null )
            socket.close();
    }

    public static void sendMsg(String msg) throws IOException
    {
        getSocket().getOutputStream().write((msg + '\n').getBytes());
    }
}

class SocketListener extends Thread{

    private InputStream im;
    private BufferedReader br;

    Handler mHandler ;

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