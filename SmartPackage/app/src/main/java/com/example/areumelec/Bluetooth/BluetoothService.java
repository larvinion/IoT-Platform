package com.example.areumelec.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.areumelec.smartpackage.SensorDemo;

public class BluetoothService {
	
	private static final String TAG = "BluetoothService";
	private static final boolean D = true;
	
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	public int mState;
	

	public long BytesSent=0;
	public long BytesRecieved=0;
	
	// ???¸? ??????? ???? ????
	public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
	
	
	private BluetoothAdapter btAdpter;
	
	private Activity mActivity;
	private Handler mHandler;
	
	private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

	SimpleQueue<Integer> fifo = new SimpleQueue<Integer>();

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
	public BluetoothService(Activity ac, Handler h){
		
		btAdpter = BluetoothAdapter.getDefaultAdapter();//bluetoothAdapter ???
		mActivity = ac;
		mHandler = h;
		mState = STATE_NONE;
	}
	
	
	private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
        Log.d(TAG, "setState() " + mState);
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(SensorDemo.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }
	
    // Bluetooth ???? get
    public synchronized int getState() {
        return mState;
    }
    
    
    public synchronized void start() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread == null) {
        } 
        else {
            mConnectThread.cancel();
            mConnectThread = null;
        }
 
        // Cancel any thread currently running a connection
        if (mConnectedThread == null) {
        }
        else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }
    
 // ConnectThread ???? device?? ??? ???? ????
    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device);
 
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread == null) {
 
            } else {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
 
        // Cancel any thread currently running a connection
        if (mConnectedThread == null) {
 
        } else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
 
        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
 
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
 
    // ConnectedThread ????
    public synchronized void connected(BluetoothSocket socket,
            BluetoothDevice device) {
        Log.d(TAG, "connected");
 
        // Cancel the thread that completed the connection
        if (mConnectThread == null) {
 
        } else {
            mConnectThread.cancel();
            mConnectThread = null;
        }
 
        // Cancel any thread currently running a connection
        if (mConnectedThread == null) {
 
        } else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
 
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
 
        setState(STATE_CONNECTED);
    }
    
    // ??? thread stop
    public synchronized void stop() {
        Log.d(TAG, "stop");
 
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
 
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
 
        //setState(STATE_NONE);
    }
    

	public synchronized boolean dataAvailable() {
		return !fifo.isEmpty();
	}
	
	public synchronized byte Read() {
		BytesRecieved+=1;
		return (byte) (fifo.get() & 0xff);
	}
    
    // ???? ???? ?κ?(?????? ?κ?)
    public void write(byte[] out) { // Create temporary object
    	ConnectedThread r; // Synchronize a copy of the ConnectedThread
    	synchronized (this) {
    		Log.d(TAG, " "+mState);
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
    	}
    	r.write(out);
    }
 
    // ???? ??????????
    private void connectionFailed() {
        setState(STATE_LISTEN);
    }
 
    // ?????? ????? ?? 
    private void connectionLost() {
        setState(STATE_LISTEN);
 
    }
	
	
	//????? ??????? ???? ?????? ?????? ???? 
	public boolean getDeviceState(){
		Log.d(TAG, "Check the Bluetooth support");
		
		if(btAdpter == null){//??????? ?????????
			Log.d(TAG, "Bluetooth is not available");
			return false;//false ????
		}
		else{//???????
			Log.d(TAG, "Bluetooth is available");
			return true;//true ????
		}
	}
	
	//??????? ???? ????
	public void enableBluetooth(){
		if(btAdpter.isEnabled()){
			//????? ??????? ???°? On?? ???
			scanDevice();//??? ??? ???
		}
		else {        
            //????? ??????? ???°? Off?? ???
            //??????? ??????? ?????
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
	}
	
	//??? ??? ???????? ?????? ????
	public void scanDevice(){
		Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
		mActivity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}
	
	
	public void getDeviceInfo(Intent data) {
		//?????? ????? ??? ????
	    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

	    BluetoothDevice device = btAdpter.getRemoteDevice(address);	     
	    connect(device);
	}
	
	private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
		public ConnectThread(BluetoothDevice device) {
	        mmDevice = device;
	        BluetoothSocket tmp = null;
	         
	        // ?????? ?????? ??? BluetoothSocket ????
	        try {
	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) {
	            Log.e(TAG, "create() failed", e);
	        }
	        mmSocket = tmp;
	    }
	
	    public void run() {
	        Log.i(TAG, "BEGIN mConnectThread");
	        setName("ConnectThread");
	         
	        // ?????? ?????? ?????? ??? ??? ????? ???????.
	        // ??? ????? ????? ???????? ???????? ???????.
	        btAdpter.cancelDiscovery();
	
	        // BluetoothSocket ???? ???
	        try {
	            // BluetoothSocket ???? ????? ???? return ???? succes ??? exception???.
	            mmSocket.connect();
	            Log.d(TAG, "Connect Success");
	             
	        } catch (IOException e) {
	            connectionFailed();     // ???? ???н? ??????? ????
	            Log.d(TAG, "Connect Fail");
	             
	            // socket?? ??´?.
	            try {
	                mmSocket.close();
	            } catch (IOException e2) {
	                Log.e(TAG,
	                        "unable to close() socket during connection failure",
	                        e2);
	            }
	            // ??????? ??? ???? ???????? ???? ??????.
	            BluetoothService.this.start();
	            return;
	        }
	
	        // ConnectThread ??????? reset???.
	        synchronized (BluetoothService.this) {
	            mConnectThread = null;
	        }
	
	        // ConnectThread?? ???????.
	        connected(mmSocket, mmDevice);
	    }
	
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) {
	            Log.e(TAG, "close() of connect socket failed", e);
	        }
	    }
	}
	
	private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
 
        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
 
            // BluetoothSocket?? inputstream ?? outputstream?? ??´?.
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
 
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
 
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // InputStream???κ??? ???? ??? ?д? ?κ?(???? ??´?)
                    bytes = mmInStream.read(buffer);
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }
 
        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                // ???? ???? ?κ?(???? ??????)
                mmOutStream.write(buffer);
 
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }
 
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
	


}