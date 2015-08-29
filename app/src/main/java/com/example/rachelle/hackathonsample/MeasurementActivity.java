package com.example.rachelle.hackathonsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by rachelle on 8/29/15.
 */
public class MeasurementActivity extends Activity {

    private static final String TAG = "MeasurementActivity: ";

    private static final int LINES_TO_READ = 50;
    private int testLinesRead;

    private ImageView finishedCheck;
    private int bust, waist, hip;

    private List<Integer> bustValues;
    private List<Integer> waistValues;
    private List<Integer> hipValues;

    private BufferedReader mBufferedReader = null;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        testLinesRead = 0;

        //initialize the lists for our measurement values
        bustValues = new ArrayList<Integer>();
        waistValues = new ArrayList<Integer>();
        hipValues = new ArrayList<Integer>();

        //initialize the bust waist and hip at 0
        bust = 0;
        waist = 0;
        hip = 0;


        finishedCheck = (ImageView) findViewById(R.id.finished_check);
        finishedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    snapshotUserMeasurements();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void snapshotUserMeasurements() throws IOException{
        Log.d(TAG, "Entering snapshotUserMeasurements");
        try{
            final InputStream stream = BluetoothDevices.bluetoothSocket.getInputStream();
            final InputStreamReader streamReader = new InputStreamReader(stream);
            mBufferedReader = new BufferedReader(streamReader);

            new Runnable(){
                public void run(){
                    testLinesRead = 0;
                    while (BluetoothDevices.bluetoothSocket.isConnected() && testLinesRead < LINES_TO_READ){
                        try {
                            Log.d(TAG, "Output: " + mBufferedReader.readLine());
                            testLinesRead++;

                            //The anticipated form of the data coming in will be int, int, int, and so we can split the incoming string
                            //around the commas, and put the strings as integers into the arrays
                            String[] splitString = mBufferedReader.readLine().split(",");
                            for (int i=0; i<splitString.length; i++){
                                Log.d(TAG, "Split at "+i+": "+splitString[i]);
                                //if 0, add to bustValues; if 1, add to waistValues; if 2, add to hipValues
                                if (i == 0){
                                    bustValues.add(Integer.parseInt(splitString[i]));
                                } else if (i == 1){
                                    waistValues.add(Integer.parseInt(splitString[i]));
                                } else if (i == 2){
                                    hipValues.add(Integer.parseInt(splitString[i]));
                                }
                            }

                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }

                    Log.d(TAG, "Finished reading values, computing...");
                    computeFinalValues();
                }
            }.run();

            if (testLinesRead <= LINES_TO_READ){
                Intent intent = new Intent(this, MeasurementActivity.class);
                startActivity(intent);
            }

        } catch (IOException e){
            Log.e(TAG, "Could not connect to device", e);
            close( mBufferedReader );
            close( BluetoothDevices.bluetoothSocket );
            throw e;
        }


        Intent intent = new Intent(this, ViewMeasurements.class);
        intent.putExtra("bust", bust);
        intent.putExtra("waist", waist);
        intent.putExtra("hip", hip);
        startActivity(intent);
        finish();
    }

    private void computeFinalValues(){
        int bustAverage = 0;
        int waistAverage = 0;
        int hipAverage = 0;

        //for (Integer number : )
    }

    private void close(Closeable aConnectedObject) {
        if ( aConnectedObject == null ) return;
        try {
            aConnectedObject.close();
        } catch ( IOException e ) {
        }
        aConnectedObject = null;
    }
}
