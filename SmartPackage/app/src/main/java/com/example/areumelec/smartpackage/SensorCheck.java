package com.example.areumelec.smartpackage;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class SensorCheck extends AppCompatActivity {
    private SocketTask socketTask;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_check);

        socketTask = new SocketTask();
        socketTask.execute();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SensorCheck Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.areumelec.smartpackage/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    public void request() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    sendMsg("sensor");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

//    void doJSONParser() {
//        StringBuffer sb = new StringBuffer();
//
//        String str =
//                "[{'name':'배트맨','age':43,'address':'고담'}," +
//                        "{'name':'슈퍼맨','age':36,'address':'뉴욕'}," +
//                        "{'name':'앤트맨','age':25,'address':'LA'}]";
//
//        try {
//            JSONArray jarray = new JSONArray(str);   // JSONArray 생성
//            for (int i = 0; i < jarray.length(); i++) {
//                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
//                String address = jObject.getString("address");
//                String name = jObject.getString("name");
//                int age = jObject.getInt("age");
//
//                sb.append(
//                        "주소:" + address +
//                                "이름:" + name +
//                                "나이:" + age + "\n"
//                );
//            }
////            tv.setText(sb.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    } // end doJSONParser()

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SensorCheck Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.areumelec.smartpackage/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    public class SocketTask extends AsyncTask<Void, byte[], Boolean> {
        Socket nsocket; //Network Socket
        DataOutputStream dos;
        DataInputStream dis;
        SocketAddress sockaddr;

        @Override
        protected void onPreExecute() {
            Log.i("AsyncTask", "onPreExecute");
        }

        @Override
        protected Boolean doInBackground(Void... params) { //This runs on a different thread
            boolean result = false;
            String read;
            try {
                Log.i("AsyncTask", "doInBackground: Creating socket");
//                SocketAddress sockaddr = new InetSocketAddress("192.168.0.10", 5000);
                sockaddr = new InetSocketAddress("192.168.0.62", 5000);
                nsocket = new Socket();
                nsocket.connect(sockaddr); //10 second connection timeout
                if (nsocket.isConnected()) {
                    dis = new DataInputStream(nsocket.getInputStream());
                    dos = new DataOutputStream(nsocket.getOutputStream());
                    Log.i("AsyncTask", "doInBackground: Socket created, streams assigned");
                    Log.i("AsyncTask", "doInBackground: Waiting for inital data...");
                    SendDataToServer("add");
                    while((read = ReceiveDataFromServer())!= ""){
                        Thread.sleep(2000);
                        SendDataToServer("sensor");
                        Log.i("AsyncTask", "doInBackground: Got some data : "+read);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("AsyncTask", "doInBackground: IOException");
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AsyncTask", "doInBackground: Exception");
                result = true;
            } finally {
                try {
                    dis.close();
                    dos.close();
                    nsocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("AsyncTask", "doInBackground: Finished");
            }
            return result;
        }

        public void SendDataToServer(String cmd) { //You run this from the main thread.
            try {
                if (nsocket.isConnected()) {
                    Log.i("AsyncTask", "SendDataToNetwork: Writing received message to socket");
                    byte[] sendWhat = cmd.getBytes("ms949");
                    int writeLen= sendWhat.length;

                    dos.writeInt(writeLen);
                    dos.flush();

                    dos.write(sendWhat, 0, writeLen);
                    dos.flush();
                } else {
                    Log.i("AsyncTask", "SendDataToNetwork: Cannot send message. Socket is closed");
                }
            } catch (Exception e) {
                Log.i("AsyncTask", "SendDataToNetwork: Message send failed. Caught an exception");
            }
        }

        public String ReceiveDataFromServer() {

            String retMsg = null;
            try {
                int size = dis.readInt();
                byte[] receiveWhat = new byte[size];
                dis.read(receiveWhat);
                retMsg = new String(receiveWhat, "ms949");

            } catch (Exception e) {
                Log.i("AsyncTask", "ReceiveDataFromNetwork: Message receive failed. Caught an exception");
            }
            return retMsg;
        }
        @Override
        protected void onProgressUpdate(byte[]... values) {
            if (values.length > 0) {
                Log.i("AsyncTask", "onProgressUpdate: " + values[0].length + " bytes received.");
//                textStatus.setText(new String(values[0]));
            }
        }
        @Override
        protected void onCancelled() {
            Log.i("AsyncTask", "Cancelled.");
//            btnStart.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.i("AsyncTask", "onPostExecute: Completed with an Error.");
//                textStatus.setText("There was a connection error.");
            } else {
                Log.i("AsyncTask", "onPostExecute: Completed.");
            }
//            btnStart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketTask.cancel(true); //In case the task is currently running
    }

}