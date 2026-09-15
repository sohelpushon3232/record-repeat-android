package com.recordrepeat.bot;

import android.content.Context;
import android.widget.Toast;

public class ReplayEngine {

    private Context context;

    public ReplayEngine(Context context) {
        this.context = context;
    }


    public void start(int repeatCount) {

        Toast.makeText(
                context,
                "Automation Running: " + repeatCount + " times",
                Toast.LENGTH_SHORT
        ).show();


        for(int i = 0; i < repeatCount; i++){

            runSteps();

        }

    }


    private void runSteps(){

        // Future:
        // saved ActionStep execute হবে এখানে

    }

}
