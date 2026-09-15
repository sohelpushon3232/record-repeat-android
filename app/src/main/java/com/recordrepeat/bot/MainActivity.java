package com.recordrepeat.bot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    RecordBot recordBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recordBot = new RecordBot();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40,40,40,40);


        Button record = new Button(this);
        record.setText("RECORD");


        Button stopRecord = new Button(this);
        stopRecord.setText("STOP RECORD");


        Button run = new Button(this);
        run.setText("RUN AUTOMATION");


        Button stop = new Button(this);
        stop.setText("STOP");


        record.setOnClickListener(v -> {

            recordBot.startBot();

            Toast.makeText(
                    this,
                    "Recording Started",
                    Toast.LENGTH_SHORT
            ).show();

        });


        stopRecord.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Recording Saved",
                    Toast.LENGTH_SHORT
            ).show();

        });


        run.setOnClickListener(v -> {

            recordBot.startBot();

            Toast.makeText(
                    this,
                    "Running Automation",
                    Toast.LENGTH_SHORT
            ).show();

        });


        stop.setOnClickListener(v -> {

            recordBot.stopBot();

            Toast.makeText(
                    this,
                    "Automation Stopped",
                    Toast.LENGTH_SHORT
            ).show();

        });


        layout.addView(record);
        layout.addView(stopRecord);
        layout.addView(run);
        layout.addView(stop);


        setContentView(layout);
    }
}
