package com.recordrepeat.bot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.content.Intent;

public class MainActivity extends Activity {

    Button startButton;
    Button stopButton;
    Button replayButton;
    EditText repeatInput;
    
    RecordManager recordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recordManager = new RecordManager(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        repeatInput = new EditText(this);
        repeatInput.setHint("Repeat count");
        repeatInput.setInputType(2);

        startButton = new Button(this);
        startButton.setText("Start Record");

        stopButton = new Button(this);
        stopButton.setText("Stop Record");

        replayButton = new Button(this);
        replayButton.setText("Run Automation");

        startButton.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Recording Started",
                    Toast.LENGTH_SHORT).show();

            recordManager.startRecording();
        });


        stopButton.setOnClickListener(v -> {
            recordManager.stopRecording();

            Toast.makeText(this,
                    "Recording Saved",
                    Toast.LENGTH_SHORT).show();
        });


        replayButton.setOnClickListener(v -> {

            int count = 1;

            try {
                count = Integer.parseInt(
                        repeatInput.getText().toString()
                );
            } catch(Exception e){}


            ReplayEngine engine =
                    new ReplayEngine(this);

            engine.start(count);

        });


        layout.addView(repeatInput);
        layout.addView(startButton);
        layout.addView(stopButton);
        layout.addView(replayButton);

        setContentView(layout);
    }
}
