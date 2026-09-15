package com.recordrepeat.bot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText("Record Repeat Bot");
        title.setTextSize(25);

        Button start = new Button(this);
        start.setText("Start Automation");

        layout.addView(title);
        layout.addView(start);

        setContentView(layout);
    }
}
