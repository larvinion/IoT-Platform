package com.example.areumelec.Model;

import java.io.IOException;
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