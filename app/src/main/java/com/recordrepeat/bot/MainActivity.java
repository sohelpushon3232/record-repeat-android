package com.recordrepeat.bot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import java.util.ArrayList;


public class MainActivity extends Activity {


    RecordManager recordManager;

    ReplayEngine replayEngine;


    EditText repeatInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        recordManager = new RecordManager();


        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setPadding(40,40,40,40);



        Button record = new Button(this);
        record.setText("RECORD");



        Button stopRecord = new Button(this);
        stopRecord.setText("STOP RECORD");



        Button save = new Button(this);
        save.setText("SAVE WORKFLOW");



        Button run = new Button(this);
        run.setText("RUN AUTOMATION");



        Button stop = new Button(this);
        stop.setText("STOP");



        repeatInput = new EditText(this);

        repeatInput.setHint("Repeat Count");

        repeatInput.setInputType(2);



        record.setOnClickListener(v -> {


            recordManager.clear();


            Toast.makeText(
                    this,
                    "Recording Started",
                    Toast.LENGTH_SHORT
            ).show();


        });



        stopRecord.setOnClickListener(v -> {


            Toast.makeText(
                    this,
                    "Recording Stopped",
                    Toast.LENGTH_SHORT
            ).show();


        });



        save.setOnClickListener(v -> {


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


            int repeat = 1;


            try{

                repeat =
                Integer.parseInt(
                    repeatInput.getText().toString()
                );


            }catch(Exception e){}



            replayEngine =
            new ReplayEngine(
                AutomationAccessibilityService.getInstance()
            );


            replayEngine.run(
                recordManager.getSteps(),
                repeat
            );


            Toast.makeText(
                    this,
                    "Automation Running",
                    Toast.LENGTH_SHORT
            ).show();


        });



        stop.setOnClickListener(v -> {


            if(replayEngine != null){

                replayEngine.stop();

            }


            Toast.makeText(
                    this,
                    "Stopped",
                    Toast.LENGTH_SHORT
            ).show();


        });



        layout.addView(record);

        layout.addView(stopRecord);

        layout.addView(save);

        layout.addView(repeatInput);

        layout.addView(run);

        layout.addView(stop);



        setContentView(layout);

    }

}
