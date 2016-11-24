package com.example.areumelec.smartpackage;

import android.app.Activity;
import android.app.Notification;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;


import com.example.areumelec.Bluetooth.BluetoothSerialClient;
import com.example.areumelec.Bluetooth.BluetoothSerialClient.BluetoothStreamingHandler;
import com.example.areumelec.Bluetooth.BluetoothSerialClient.OnBluetoothEnabledListener;
import com.example.areumelec.Bluetooth.BluetoothSerialClient.OnScanListener;


import android.bluetooth.BluetoothDevice;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import android.widget.ArrayAdapter;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SensorDemo extends Activity {
    private LinkedList<BluetoothDevice> mBluetoothDevices = new LinkedList<BluetoothDevice>();
    private ArrayAdapter<String> mDeviceArrayAdapter;

    private TextView mTextView_temperature;
    private TextView mTextView_flame;
    private TextView mTextView_door;
    private TextView mTextView_gas;
    private ProgressDialog mLoadingDialog;
    private AlertDialog mDeviceListDialog;
    private Menu mMenu;
    private BluetoothSerialClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mClient = BluetoothSerialClient.getInstance();

        if(mClient == null) {
            Toast.makeText(getApplicationContext(), "Cannot use the Bluetooth device.", Toast.LENGTH_SHORT).show();
            finish();
        }
        initSensorTextView();
        initProgressDialog();
        initDeviceListDialog();
    }

    @Override
    protected void onPause() {
        mClient.cancelScan(getApplicationContext());
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        enableBluetooth();
    }
    private void initProgressDialog() {
        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setCancelable(false);
    }
    private void initWidget() {

     }

    private void initDeviceListDialog() {
        mDeviceArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_device);
        ListView listView = new ListView(getApplicationContext());
        listView.setAdapter(mDeviceArrayAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item =  (String) parent.getItemAtPosition(position);
                for(BluetoothDevice device : mBluetoothDevices) {
                    if(item.contains(device.getAddress())) {
                        connect(device);
                        mDeviceListDialog.cancel();
                    }
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select bluetooth device");
        builder.setView(listView);
        builder.setPositiveButton("Scan",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        scanDevices();
                    }
                });
        mDeviceListDialog = builder.create();
        mDeviceListDialog.setCanceledOnTouchOutside(false);
    }

    private void addDeviceToArrayAdapter(BluetoothDevice device) {
        if(mBluetoothDevices.contains(device)) {
            mBluetoothDevices.remove(device);
            mDeviceArrayAdapter.remove(device.getName() + "\n" + device.getAddress());
        }
        mBluetoothDevices.add(device);
        mDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress() );
        mDeviceArrayAdapter.notifyDataSetChanged();
    }

    private void enableBluetooth() {
        BluetoothSerialClient btSet =  mClient;
        btSet.enableBluetooth(this, new OnBluetoothEnabledListener() {
            @Override
            public void onBluetoothEnabled(boolean success) {
                if(success) {
                    getPairedDevices();
                } else {
                    finish();
                }
            }
        });
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> devices =  mClient.getPairedDevices();
        for(BluetoothDevice device: devices) {
            addDeviceToArrayAdapter(device);
        }
    }

    private void scanDevices() {
        BluetoothSerialClient btSet = mClient;
        btSet.scanDevices(getApplicationContext(), new OnScanListener() {
            String message ="";
            @Override
            public void onStart() {
                Log.d("Test", "Scan Start.");
                mLoadingDialog.show();
                message = "Scanning....";
                mLoadingDialog.setMessage("Scanning....");
                mLoadingDialog.setCancelable(true);
                mLoadingDialog.setCanceledOnTouchOutside(false);
                mLoadingDialog.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        BluetoothSerialClient btSet = mClient;
                        btSet.cancelScan(getApplicationContext());
                    }
                });
            }

            @Override
            public void onFoundDevice(BluetoothDevice bluetoothDevice) {
                addDeviceToArrayAdapter(bluetoothDevice);
                message += "\n" + bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress();
                mLoadingDialog.setMessage(message);
            }

            @Override
            public void onFinish() {
                Log.d("Test", "Scan finish.");
                message = "";
                mLoadingDialog.cancel();
                mLoadingDialog.setCancelable(false);
                mLoadingDialog.setOnCancelListener(null);
                mDeviceListDialog.show();
            }
        });
    }

    private void connect(BluetoothDevice device) {
        mLoadingDialog.setMessage("Connecting....");
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show();
        BluetoothSerialClient btSet =  mClient;
        btSet.connect(getApplicationContext(), device, mBTHandler);
    }
    private BluetoothStreamingHandler mBTHandler = new BluetoothStreamingHandler() {
        ByteBuffer mmByteBuffer = ByteBuffer.allocate(1024);

        @Override
        public void onError(Exception e) {
            mLoadingDialog.cancel();
            Toast.makeText(getApplicationContext(), "The the Bluetooth device Connection error - " +  e.toString(), Toast.LENGTH_SHORT).show();
            mMenu.getItem(0).setTitle(R.string.action_connect);
        }

        @Override
        public void onDisconnected() {
            mMenu.getItem(0).setTitle(R.string.action_connect);
            mLoadingDialog.cancel();
            Toast.makeText(getApplicationContext(), "The the Bluetooth device Disconnected.", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onData(byte[] buffer, int length) {
            if(length == 0) return;
            if(mmByteBuffer.position() + length >= mmByteBuffer.capacity()) {
                ByteBuffer newBuffer = ByteBuffer.allocate(mmByteBuffer.capacity() * 2);
                newBuffer.put(mmByteBuffer.array(), 0,  mmByteBuffer.position());
                mmByteBuffer = newBuffer;
            }
            mmByteBuffer.put(buffer, 0, length);
            if(buffer[length - 1] == '\0') {
                /* 데이터를 받아와서 집어넣는 부분 Buffer*/
                readSensorData(new String(mmByteBuffer.array(), 0, mmByteBuffer.position()));
                mmByteBuffer.clear();
            }
        }

        @Override
        public void onConnected() {
            Toast.makeText(getApplicationContext(), "Messgae : " + mClient.getConnectedDevice().getName() + "Connected. ", Toast.LENGTH_SHORT).show();
            mLoadingDialog.cancel();
            mMenu.getItem(0).setTitle(R.string.action_disconnect);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean connect = mClient.isConnection();
        if(item.getItemId() == R.id.action_connect) {
            if (!connect) {
                mDeviceListDialog.show();
            } else {
                mBTHandler.close();
            }
            return true;
        }else {
            return true;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        mClient.claer();
    };

    private void initSensorTextView(){
        mTextView_temperature = (TextView) findViewById(R.id.temperature_value);
        mTextView_flame = (TextView) findViewById(R.id.flame_value);
        mTextView_door = (TextView) findViewById(R.id.door_value);
        mTextView_gas = (TextView) findViewById(R.id.gas_value);
    }

    private void readSensorData(String text) {
        if(text.contains("c")){
            mTextView_door.setText("Close");
        }else{
            mTextView_door.setText("Open");
        }
        text.
        mTextView_flame.setText(text);
    }
}