package com.example.areumelec.smartpackage;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;


import com.example.areumelec.Bluetooth.*;


import android.bluetooth.BluetoothDevice;

import java.util.LinkedList;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SensorDemo extends Activity {
    private LinkedList<BluetoothDevice> mBluetoothDevices = new LinkedList<BluetoothDevice>();
    private ArrayAdapter<String> mDeviceArrayAdapter;

    private TextView mTextView_temperature;
    private TextView mTextView_flame;
    private TextView mTextView_door;
    private TextView mTextView_gas;

    // //////////////////블루투스 관련////////////////////

    private static final String TAG = "BluetoothService";
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    public BluetoothService bt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // MainActivity에서 연결한 BlueTooth 정보를 받아옴 ///////////
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        bt = MainActivity.btService;
        if (bt != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (bt.getState() == BluetoothService.STATE_NONE) {
                bt.start();
            }
        }
        /////////////////////////////////////////////////

        //mw = new MultirotorData(bt);

        initSensorTextView();
    }

    private void initSensorTextView(){
        mTextView_temperature = (TextView) findViewById(R.id.temperature_value);
        mTextView_flame = (TextView) findViewById(R.id.flame_value);
        mTextView_door = (TextView) findViewById(R.id.door_value);
        mTextView_gas = (TextView) findViewById(R.id.gas_value);
    }
/*
        public void onData(byte[] buffer, int length) {
            if(length == 0) return;
            if(mmByteBuffer.position() + length >= mmByteBuffer.capacity()) {
                ByteBuffer newBuffer = ByteBuffer.allocate(mmByteBuffer.capacity() * 2);
                newBuffer.put(mmByteBuffer.array(), 0,  mmByteBuffer.position());
                mmByteBuffer = newBuffer;
            }
            mmByteBuffer.put(buffer, 0, length);
            if(buffer[length - 1] == '\0') {
                // 데이터를 받아와서 집어넣는 부분 Buffer
readSensorData(new String(mmByteBuffer.array(), 0, mmByteBuffer.position()));
    mmByteBuffer.clear();
}
}
 */
    private void readSensorData(String text) {
        String buf = text;

        // Door
        if(buf.contains("c")){
            buf = buf.replace("c","");
            mTextView_door.setText("Close");
        }else if(buf.contains("o")){
            buf = buf.replace("o","");
            mTextView_door.setText("Open");
        }

        // MQ-7 Gas
        if(buf.contains("g")){
            buf = buf.replace("g","");
            mTextView_gas.setText("Detect");
        }else if(buf.contains("n")){
            buf = buf.replace("n","");
            mTextView_gas.setText("None");
        }

        // Flame
        if(buf.contains("f")){
            buf = buf.replace("f","");
            mTextView_flame.setText("Detect");
        }else if(buf.contains("s")){
            buf = buf.replace("s","");
            mTextView_flame.setText("None");
        }

        // Temperature
        mTextView_temperature.setText(buf);
    }
}