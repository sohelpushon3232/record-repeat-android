package com.recordrepeat.bot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40,40,40,40);

        Button accessibility = new Button(this);
        accessibility.setText("Enable Accessibility Service");

        accessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                    Settings.ACTION_ACCESSIBILITY_SETTINGS
                );
                startActivity(intent);
            }
        });

        Button record = new Button(this);
        record.setText("RECORD");

        Button run = new Button(this);
        run.setText("RUN AUTOMATION");

        Button stop = new Button(this);
        stop.setText("STOP");

        layout.addView(accessibility);
        layout.addView(record);
        layout.addView(run);
        layout.addView(stop);

        setContentView(layout);
    }
}
