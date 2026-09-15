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

        Button startButton = new Button(this);
        startButton.setText("Start Bot");

        Button stopButton = new Button(this);
        stopButton.setText("Stop Bot");

        startButton.setOnClickListener(v -> {
            recordBot.startBot();
            Toast.makeText(this, "Bot Started", Toast.LENGTH_SHORT).show();
        });

        stopButton.setOnClickListener(v -> {
            recordBot.stopBot();
            Toast.makeText(this, "Bot Stopped", Toast.LENGTH_SHORT).show();
        });

        layout.addView(startButton);
        layout.addView(stopButton);

        setContentView(layout);
    }
}
