package com.recordrepeat.bot;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("Record Repeat Bot");
        title.setTextSize(24);

        Button accessibility = new Button(this);
        accessibility.setText("Enable Automation Permission");

        accessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        Button start = new Button(this);
        start.setText("Start Automation");

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serviceIntent = new Intent(
                        MainActivity.this,
                        AutomationAccessibilityService.class
                );

                startService(serviceIntent);
            }
        });

        layout.addView(title);
        layout.addView(accessibility);
        layout.addView(start);

        setContentView(layout);
    }
}
