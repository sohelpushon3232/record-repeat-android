package com.recordrepeat.bot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends Activity {

    RecordBot recordBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recordBot = new RecordBot();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40,40,40,40);

        Button start = new Button(this);
        start.setText("Start Automation");

        Button stop = new Button(this);
        stop.setText("Stop Automation");

        start.setOnClickListener(v -> {

            recordBot.startBot();

            Toast.makeText(
                    this,
                    "Automation Started",
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


        layout.addView(start);
        layout.addView(stop);

        setContentView(layout);
    }
}
