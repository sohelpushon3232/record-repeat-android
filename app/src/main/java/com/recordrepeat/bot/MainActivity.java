package com.recordrepeat.bot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    RecordManager recordManager;
    ReplayEngine replayEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recordManager = new RecordManager();
        replayEngine = new ReplayEngine();

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

            recordManager.clear();

            Toast.makeText(
                    this,
                    "Recording Started",
                    Toast.LENGTH_SHORT
            ).show();

        });


        stopRecord.setOnClickListener(v -> {

            WorkflowStorage.save(
                    this,
                    recordManager.getSteps()
            );

            Toast.makeText(
                    this,
                    "Workflow Saved",
                    Toast.LENGTH_SHORT
            ).show();

        });


        run.setOnClickListener(v -> {

            replayEngine.run(
                    recordManager.getSteps()
            );

            Toast.makeText(
                    this,
                    "Automation Running",
                    Toast.LENGTH_SHORT
            ).show();

        });


        stop.setOnClickListener(v -> {

            replayEngine.stop();

            Toast.makeText(
                    this,
                    "Stopped",
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
