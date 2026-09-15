package com.recordrepeat.bot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setText("Record Repeat Bot");
        tv.setTextSize(22);

        setContentView(tv);
    }
}
