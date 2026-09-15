package com.recordrepeat.bot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

import java.util.List;


public class MainActivity extends Activity {


    RecordManager recordManager;

    ReplayEngine replayEngine;


    EditText repeatInput;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);


        recordManager =
                new RecordManager(this);


        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                40,40,40,40
        );



        repeatInput =
                new EditText(this);

        repeatInput.setHint(
                "Repeat Count"
        );

        repeatInput.setInputType(2);



        Button record =
                new Button(this);

        record.setText(
                "START RECORD"
        );



        Button stop =
                new Button(this);

        stop.setText(
                "STOP RECORD"
        );



        Button run =
                new Button(this);

        run.setText(
                "RUN AUTOMATION"
        );



        record.setOnClickListener(v -> {

            recordManager.startRecording();

            Toast.makeText(
                    this,
                    "Recording Started",
                    Toast.LENGTH_SHORT
            ).show();

        });



        stop.setOnClickListener(v -> {


            recordManager.stopRecording();


            Toast.makeText(
                    this,
                    "Recording Saved",
                    Toast.LENGTH_SHORT
            ).show();


        });



        run.setOnClickListener(v -> {


            int count = 1;


            try{

                count =
                Integer.parseInt(
                    repeatInput.getText().toString()
                );


            }catch(Exception e){}



            List<ActionStep> steps =
                    recordManager.getSteps();



            replayEngine =
                    new ReplayEngine(this);



            replayEngine.start(
                    count,
                    steps
            );


        });



        layout.addView(repeatInput);

        layout.addView(record);

        layout.addView(stop);

        layout.addView(run);



        setContentView(layout);

    }

}
