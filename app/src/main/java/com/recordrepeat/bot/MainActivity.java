package com.recordrepeat.bot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import android.os.Build;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {


    EditText workflowName;
    EditText repeatInput;

    ListView workflowList;

    ArrayList<String> names =
            new ArrayList<>();


    RecordManager recordManager;

    String selectedWorkflow = "";


    AutoRepeatEngine autoRepeatEngine;


    Button floatingButton;



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
                30,30,30,30
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
                "RUN SELECTED"
        );



        Button stop =
                new Button(this);

        stop.setText(
                "STOP REPEAT"
        );



        floatingButton =
                new Button(this);

        floatingButton.setText(
                "START FLOATING BOT"
        );


        workflowList =
                new ListView(this);


        loadWorkflows();



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


            loadWorkflows();


            Toast.makeText(
                    this,
                    "Workflow Saved",
                    Toast.LENGTH_SHORT
            ).show();


        });



        workflowList.setOnItemClickListener(
                (parent, view, position, id) -> {


                    selectedWorkflow =
                            names.get(position);


                    Toast.makeText(
                            this,
                            selectedWorkflow + " selected",
                            Toast.LENGTH_SHORT
                    ).show();


                });
                run.setOnClickListener(v -> {


            if(selectedWorkflow.isEmpty()){


                Toast.makeText(
                        this,
                        "Select workflow first",
                        Toast.LENGTH_SHORT
                ).show();


                return;

            }



            TouchRecorderService service =
                    TouchRecorderService.getInstance();



            if(service == null){


                Toast.makeText(
                        this,
                        "Enable Accessibility First",
                        Toast.LENGTH_LONG
                ).show();


                return;

            }




            int count = 1;


            try{

                count =
                Integer.parseInt(
                        repeatInput
                        .getText()
                        .toString()
                );


            }catch(Exception ignored){}




            List<ActionStep> steps =
                    WorkflowStorage.loadWorkflow(
                            this,
                            selectedWorkflow
                    );



            if(steps.isEmpty()){


                Toast.makeText(
                        this,
                        "No steps found",
                        Toast.LENGTH_SHORT
                ).show();


                return;

            }





            autoRepeatEngine =
                    new AutoRepeatEngine(
                            service
                    );



            autoRepeatEngine.start(
                    steps,
                    count
            );



            Toast.makeText(
                    this,
                    "Auto Repeat Started",
                    Toast.LENGTH_SHORT
            ).show();


        });






        stop.setOnClickListener(v -> {


            if(autoRepeatEngine != null){


                autoRepeatEngine.stop();


            }



            Toast.makeText(
                    this,
                    "Automation Stopped",
                    Toast.LENGTH_SHORT
            ).show();


        });







        floatingButton.setOnClickListener(v -> {


            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !Settings.canDrawOverlays(this)){



                Intent intent =
                        new Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse(
                                        "package:"+
                                        getPackageName()
                                )
                        );



                startActivity(intent);



            }else{


                Intent intent =
                        new Intent(
                                this,
                                FloatingControlService.class
                        );


                startService(intent);



                Toast.makeText(
                        this,
                        "Floating BOT Started",
                        Toast.LENGTH_SHORT
                ).show();


            }



        });






        layout.addView(workflowName);

        layout.addView(repeatInput);

        layout.addView(record);

        layout.addView(save);

        layout.addView(workflowList);

        layout.addView(run);

        layout.addView(stop);

        layout.addView(floatingButton);



        setContentView(layout);


    }







    private void loadWorkflows(){



        names.clear();



        JSONArray array =
                WorkflowStorage.getWorkflows(this);




        for(int i=0;i<array.length();i++){


            try{


                names.add(
                        array.getJSONObject(i)
                        .getString("name")
                );



            }catch(Exception ignored){}



        }





        workflowList.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        names
                )
        );



    }


}
