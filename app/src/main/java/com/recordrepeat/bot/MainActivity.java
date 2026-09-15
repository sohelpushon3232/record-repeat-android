package com.recordrepeat.bot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {

    private ReplayEngine replayEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                40, 40, 40, 40
        );

        EditText repeat =
                new EditText(this);

        repeat.setHint("Repeat Count");
        repeat.setInputType(2);


        Button permission =
                new Button(this);

        permission.setText(
                "ENABLE ACCESSIBILITY"
        );


        Button record =
                new Button(this);

        record.setText(
                "START RECORD"
        );


        Button stopRecord =
                new Button(this);

        stopRecord.setText(
                "STOP & SAVE"
        );


        Button run =
                new Button(this);

        run.setText(
                "RUN AUTOMATION"
        );


        Button stop =
                new Button(this);

        stop.setText(
                "STOP AUTOMATION"
        );


        permission.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            Settings.ACTION_ACCESSIBILITY_SETTINGS
                    )
            );
        });


        record.setOnClickListener(v -> {

            RecordManager
                    .getInstance()
                    .startRecording();

            Toast.makeText(
                    this,
                    "Recording Started",
                    Toast.LENGTH_SHORT
            ).show();
        });


        stopRecord.setOnClickListener(v -> {

            RecordManager manager =
                    RecordManager.getInstance();

            manager.stopRecording();

            WorkflowStorage.save(
                    this,
                    manager.getSteps()
            );

            Toast.makeText(
                    this,
                    "Workflow Saved",
                    Toast.LENGTH_SHORT
            ).show();
        });


        run.setOnClickListener(v -> {

            TouchRecorderService service =
                    TouchRecorderService.getInstance();

            if (service == null) {

                Toast.makeText(
                        this,
                        "Enable Accessibility first",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            int count = 1;

            try {
                count = Integer.parseInt(
                        repeat.getText()
                                .toString()
                                .trim()
                );
            } catch (Exception ignored) {}

            if (count < 1) count = 1;

            List<ActionStep> steps =
                    WorkflowStorage.load(this);

            if (steps.isEmpty()) {

                Toast.makeText(
                        this,
                        "No saved workflow",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            replayEngine =
                    new ReplayEngine(service);

            replayEngine.start(
                    steps,
                    count
            );

            Toast.makeText(
                    this,
                    "Automation Started",
                    Toast.LENGTH_SHORT
            ).show();
        });


        stop.setOnClickListener(v -> {

            if (replayEngine != null) {
                replayEngine.stop();
            }

            Toast.makeText(
                    this,
                    "Automation Stopped",
                    Toast.LENGTH_SHORT
            ).show();
        });


        layout.addView(permission);
        layout.addView(repeat);
        layout.addView(record);
        layout.addView(stopRecord);
        layout.addView(run);
        layout.addView(stop);

        setContentView(layout);
    }
}
