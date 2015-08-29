package com.example.rachelle.hackathonsample;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String UUID_SERIAL_PORT_PROFILE = "00001101-0000-1000-8000-00805F9B34FB";
    private static final String TAG = "MainActivity: ";
    private static final String NAME_BLUETOOTH_MODULE="HC-06";
    private static final String NAME_APP = "Fiddle";

    private Button listBtn;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private List<BluetoothDevice> pairedDevicesList;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;
    private ImageView pairWithFiddleButton;

    private BluetoothSocket mSocket = null;
    private BufferedReader mBufferedReader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // take an instance of BluetoothAdapter
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            listBtn.setEnabled(false);

            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {
            pairWithFiddleButton = (ImageView) findViewById(R.id.pair_with_fiddle_button);
            pairWithFiddleButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    list(v);
                }
            });

            myListView = (ListView)findViewById(R.id.listView1);

            // create the arrayAdapter that contains the BTDevices, and set it to the ListView
            BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            myListView.setAdapter(BTArrayAdapter);
        }
    }

    public void on(View view){
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            if(!myBluetoothAdapter.isEnabled()) {
                Toast.makeText(MainActivity.this, "Please enable bluetooth to use this app!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void list(View view){
        // get paired devices
        pairedDevicesList = new ArrayList<BluetoothDevice>();
        pairedDevices = myBluetoothAdapter.getBondedDevices();

        // put it's one to the adapter
        for(BluetoothDevice device : pairedDevices) {
            if (device.getName().equalsIgnoreCase(NAME_BLUETOOTH_MODULE)){
                pairedDevicesList.add(device);
            }
        }

        for (BluetoothDevice device : pairedDevicesList){
            Log.d(TAG, "Found device on list: "+device.getName());
            if (device.getName().equalsIgnoreCase(NAME_BLUETOOTH_MODULE)){
                try {
                    readFromBluetoothModule(device);
                } catch (IOException e){
                    Log.d(TAG, "Caught IOException trying to open device connection");
                    e.printStackTrace();
                    Log.d(TAG, "E: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Failed to connect with Fiddle", Toast.LENGTH_LONG);
                }
            }
        }

    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void off(View view){
        myBluetoothAdapter.disable();

        Toast.makeText(getApplicationContext(),"Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }

    //Big issue is that reading continually from the buffer chokes the UI thread, run on a worker thread
    private void readFromBluetoothModule(BluetoothDevice device) throws IOException{
        Log.d(TAG, "Entered readFromBluetoothModule");

        try{
            mSocket = device.createRfcommSocketToServiceRecord( getSerialPortUUID() );
            mSocket.connect();
            final InputStream stream = mSocket.getInputStream();
            final InputStreamReader streamReader = new InputStreamReader(stream);
            mBufferedReader = new BufferedReader(streamReader);

            new Runnable(){
                public void run(){
                    int linesRead = 0;
                    while (mSocket.isConnected() && linesRead < 20){
                        try {
                            Log.d(TAG, "Output: " + mBufferedReader.readLine());
                            linesRead++;
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }.run();

        } catch (IOException e){
            Log.e(TAG, "Could not connect to device", e);
            close( mBufferedReader );
            close( mSocket );
            throw e;
        }
    }


    private void openDeviceConnection(BluetoothDevice aDevice)
        throws IOException {
            Log.d(TAG, "Entered openDeviceConnection");
            InputStream aStream = null;
            InputStreamReader aReader = null;
            try {
                Log.d(TAG, "Successfully connected to device");
                mSocket = aDevice.createRfcommSocketToServiceRecord( getSerialPortUUID() );
                mSocket.connect();
                aStream = mSocket.getInputStream();
                aReader = new InputStreamReader( aStream );
                mBufferedReader = new BufferedReader( aReader );
                String output = "";

                while (mSocket.isConnected()){
                    output = mBufferedReader.readLine();
                    Log.d(TAG, "Output: "+output);
                }

                Log.d(TAG, "Found output: "+output);

            } catch ( IOException e ) {
                Log.e(TAG, "Could not connect to device", e);
                close( mBufferedReader );
                close( aReader );
                close( aStream );
                close( mSocket );
                throw e;
            }
    }

    private void close(Closeable aConnectedObject) {
        if ( aConnectedObject == null ) return;
        try {
            aConnectedObject.close();
        } catch ( IOException e ) {
        }
        aConnectedObject = null;
    }

    private UUID getSerialPortUUID() {
        return UUID.fromString( UUID_SERIAL_PORT_PROFILE );
    }

}