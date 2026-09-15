package com.recordrepeat.bot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

import java.util.List;


public class MainActivity extends Activity {


    EditText workflowName;
    EditText repeatInput;

    RecordManager recordManager;

    ReplayEngine replayEngine;



    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);


        recordManager =
                RecordManager.getInstance();



        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                40,40,40,40
        );


        workflowName =
                new EditText(this);

        workflowName.setHint(
                "Workflow Name"
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


        Button save =
                new Button(this);

        save.setText(
                "SAVE WORKFLOW"
        );


        Button run =
                new Button(this);

        run.setText(
                "RUN"
        );



        record.setOnClickListener(v -> {

            recordManager.startRecording();

            Toast.makeText(
                    this,
                    "Recording Started",
                    Toast.LENGTH_SHORT
            ).show();

        });



        save.setOnClickListener(v -> {


            String name =
                    workflowName
                    .getText()
                    .toString();


            if(name.isEmpty()){

                name = "My Workflow";

            }


            WorkflowStorage.saveWorkflow(
                    this,
                    name,
                    recordManager.getSteps()
            );


            Toast.makeText(
                    this,
                    "Saved: " + name,
                    Toast.LENGTH_SHORT
            ).show();


        });



        run.setOnClickListener(v -> {


            List<ActionStep> steps =
                    recordManager.getSteps();


            int count = 1;


            try{

                count =
                Integer.parseInt(
                        repeatInput
                        .getText()
                        .toString()
                );

            }catch(Exception ignored){}



            TouchRecorderService service =
                    TouchRecorderService.getInstance();



            if(service != null){


                replayEngine =
                        new ReplayEngine(service);


                replayEngine.start(
                        steps,
                        count
                );

            }


        });



        layout.addView(workflowName);

        layout.addView(repeatInput);

        layout.addView(record);

        layout.addView(save);

        layout.addView(run);


        setContentView(layout);

    }

}
