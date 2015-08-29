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
    private static final double BASELINE_BUST_MEASUREMENT = 36.0; //starting measurements in resting garment
    private static final double BASELINE_WAIST_MEASUREMENT = 26.0;
    private static final double BASELINE_HIP_MEASUREMENT = 36.0;
    private static final int HALF_INCH_OR_CM_SENSOR_CALIBRATION = 25;

    private int testLinesRead;

    private ImageView finishedCheck;
    private double bust, waist, hip;

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

        //initialize the bust waist and hip at 0.0
        bust = 0.0;
        waist = 0.0;
        hip = 0.0;


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
    }

    private void computeFinalValues(){
        //Compute averages from the sensor values, which will then need to be calibrated via our sensor value-inch relationship
        double bustAverage = 0.0;
        double waistAverage = 0.0;
        double hipAverage = 0.0;

        int bustSum = 0;
        int waistSum = 0;
        int hipSum = 0;

        for (Integer number : bustValues){
            bustSum += number;
        }

        for (Integer number : waistValues){
            waistSum += number;
        }

        for (Integer number : hipValues){
            hipSum += number;
        }

        bustAverage = bustSum/LINES_TO_READ;
        waistAverage = waistSum/LINES_TO_READ;
        hipAverage = hipSum/LINES_TO_READ;

        Log.d(TAG, "Bust average: "+bustAverage);
        Log.d(TAG, "Waist average: "+waistAverage);
        Log.d(TAG, "Hip average: "+hipAverage);

        //The assumption is that <190 represents a resting sensor with no change in the original measurement,
        //which will be determined by measuring the garment in a relaxed state
        if (bustAverage < 190){
            bust = BASELINE_BUST_MEASUREMENT;
        } else {

        }

                /*
        Intent intent = new Intent(this, ViewMeasurements.class);
        intent.putExtra("bust", bust);
        intent.putExtra("waist", waist);
        intent.putExtra("hip", hip);
        startActivity(intent);
        finish();
        */
    }

    private double computeGrowthAmount(int growthBeyondSensorBaseline){
        
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
