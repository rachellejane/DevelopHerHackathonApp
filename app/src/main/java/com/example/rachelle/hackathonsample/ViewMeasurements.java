package com.example.rachelle.hackathonsample;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by rachelle on 8/29/15.
 */
public class ViewMeasurements extends Activity {

    private static final String TAG = "ViewMeasurements: ";

    private ImageView backButton;
    private ImageView emailButton;
    private ImageView saveButton;

    private TextView bustMeasurement;
    private TextView waistMeasurement;
    private TextView hipMeasurement;

    private int bust, waist, hip;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmeasurements);

        bustMeasurement = (TextView) findViewById(R.id.bust_measurement);
        waistMeasurement = (TextView) findViewById(R.id.waist_measurement);
        hipMeasurement = (TextView) findViewById(R.id.hip_measurement);

        if (getIntent().getExtras() != null){
            bust = getIntent().getExtras().getInt("bust");
            waist = getIntent().getExtras().getInt("waist");
            hip = getIntent().getExtras().getInt("hip");

            bustMeasurement.setText("Bust: "+bust);
            waistMeasurement.setText("Waist: "+waist);
            hipMeasurement.setText("Hip: "+hip);
        }

        backButton = (ImageView) findViewById(R.id.cancel_action);
        emailButton = (ImageView) findViewById(R.id.email_button);
        saveButton = (ImageView) findViewById(R.id.finished_check);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Takes user back to last screen to do their measurements again
                Intent intent = new Intent(ViewMeasurements.this, MeasurementActivity.class);
                startActivity(intent);
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailDialog();
            }
        });
    }

    private void showEmailDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.email_input_dialog, null);
        builder.setView(view);
        EditText information = (EditText) view.findViewById(R.id.message_input);
        final EditText email = (EditText) view.findViewById(R.id.email_input);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Submitting to email: "+email.getText().toString());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
