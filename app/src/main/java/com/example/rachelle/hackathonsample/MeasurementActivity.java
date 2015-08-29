package com.example.rachelle.hackathonsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by rachelle on 8/29/15.
 */
public class MeasurementActivity extends Activity {

    private ImageView finishedCheck;
    private int bust, waist, hip;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        //initialize the bust waist and hip at 0
        bust = 0;
        waist = 0;
        hip = 0;

        finishedCheck = (ImageView) findViewById(R.id.finished_check);
        finishedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapshotUserMeasurements();
            }
        });
    }

    private void snapshotUserMeasurements(){
        /*
        try{
            BluetoothDevices.bluetoothSocket = device.createRfcommSocketToServiceRecord( getSerialPortUUID() );
            BluetoothDevices.bluetoothSocket.connect();
            final InputStream stream = BluetoothDevices.bluetoothSocket.getInputStream();
            final InputStreamReader streamReader = new InputStreamReader(stream);
            mBufferedReader = new BufferedReader(streamReader);

            new Runnable(){
                public void run(){
                    testLinesRead = 0;
                    while (BluetoothDevices.bluetoothSocket.isConnected() && testLinesRead < TEST_LINES_TO_READ){
                        try {
                            Log.d(TAG, "Output: " + mBufferedReader.readLine());
                            testLinesRead++;
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }.run();

            if (testLinesRead <= TEST_LINES_TO_READ){
                Intent intent = new Intent(this, MeasurementActivity.class);
                startActivity(intent);
            }

        } catch (IOException e){
            Log.e(TAG, "Could not connect to device", e);
            close( mBufferedReader );
            close( BluetoothDevices.bluetoothSocket );
            throw e;
        }
        */

        Intent intent = new Intent(this, ViewMeasurements.class);
        intent.putExtra("bust", bust);
        intent.putExtra("waist", waist);
        intent.putExtra("hip", hip);
        startActivity(intent);
        finish();
    }
}
