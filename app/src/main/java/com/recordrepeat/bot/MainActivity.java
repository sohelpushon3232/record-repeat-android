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
    ListView smartWorkflowList;



    ArrayList<String> names =
            new ArrayList<>();


    ArrayList<String> smartNames =
            new ArrayList<>();



    String selectedWorkflow = "";

    String selectedSmartWorkflow = "";



    RecordManager recordManager;


    AutoRepeatEngine autoRepeatEngine;



    Button floatingButton;

    Button smartBuilderButton;

    Button smartRunButton;





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
                30,
                30,
                30,
                30
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
                "RUN RECORD WORKFLOW"
        );







        Button stop =
                new Button(this);


        stop.setText(
                "STOP REPEAT"
        );






        Button delete =
                new Button(this);


        delete.setText(
                "DELETE WORKFLOW"
        );








        floatingButton =
                new Button(this);


        floatingButton.setText(
                "START FLOATING BOT"
        );








        smartBuilderButton =
                new Button(this);


        smartBuilderButton.setText(
                "SMART BUILDER"
        );








        smartRunButton =
                new Button(this);


        smartRunButton.setText(
                "SMART RUN"
        );








        workflowList =
                new ListView(this);



        smartWorkflowList =
                new ListView(this);





        loadWorkflows();


        loadSmartWorkflows();
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
                            selectedWorkflow+" selected",
                            Toast.LENGTH_SHORT
                    ).show();


                });







        smartWorkflowList.setOnItemClickListener(
                (parent, view, position, id) -> {


                    selectedSmartWorkflow =
                            smartNames.get(position);



                    Toast.makeText(
                            this,
                            selectedSmartWorkflow+" selected",
                            Toast.LENGTH_SHORT
                    ).show();


                });








        run.setOnClickListener(v -> {



            if(selectedWorkflow.isEmpty()){


                Toast.makeText(
                        this,
                        "Select Record Workflow",
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






            List<ActionStep> steps =

                    WorkflowStorage.loadWorkflow(
                            this,
                            selectedWorkflow
                    );






            autoRepeatEngine =

                    new AutoRepeatEngine(
                            service
                    );





            autoRepeatEngine.start(
                    steps,
                    1
            );




        });








        stop.setOnClickListener(v -> {



            if(autoRepeatEngine != null){

                autoRepeatEngine.stop();

            }


            Toast.makeText(
                    this,
                    "Stopped",
                    Toast.LENGTH_SHORT
            ).show();



        });








        delete.setOnClickListener(v -> {



            if(selectedWorkflow.isEmpty()){

                return;

            }




            WorkflowStorage.deleteWorkflow(
                    this,
                    selectedWorkflow
            );



            selectedWorkflow = "";



            loadWorkflows();



        });








        floatingButton.setOnClickListener(v -> {



            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    &&
                    !Settings.canDrawOverlays(this)){



                Intent intent =
                        new Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse(
                                        "package:"+getPackageName()
                                )
                        );



                startActivity(intent);



            }else{


                startService(

                        new Intent(
                                this,
                                FloatingControlService.class
                        )

                );


            }


        });








        smartBuilderButton.setOnClickListener(v -> {


            startActivity(

                    new Intent(
                            this,
                            SmartBuilderActivity.class
                    )

            );


        });








        smartRunButton.setOnClickListener(v -> {



            if(selectedSmartWorkflow.isEmpty()){


                Toast.makeText(
                        this,
                        "Select Smart Workflow",
                        Toast.LENGTH_SHORT
                ).show();


                return;

            }





            TouchRecorderService service =

                    TouchRecorderService.getInstance();





            SmartRunEngine engine =

                    new SmartRunEngine(
                            this,
                            service
                    );





            engine.runWorkflow(
                    selectedSmartWorkflow
            );



        });








        layout.addView(workflowName);

        layout.addView(repeatInput);

        layout.addView(workflowList);

        layout.addView(smartWorkflowList);

        layout.addView(record);

        layout.addView(save);

        layout.addView(run);

        layout.addView(smartRunButton);

        layout.addView(stop);

        layout.addView(delete);

        layout.addView(floatingButton);

        layout.addView(smartBuilderButton);





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








    private void loadSmartWorkflows(){



        smartNames.clear();



        JSONArray array =

                SmartWorkflowStorage.getWorkflows(this);






        for(int i=0;i<array.length();i++){


            try{


                smartNames.add(

                        array.getJSONObject(i)
                        .getString("name")

                );


            }catch(Exception ignored){}



        }







        smartWorkflowList.setAdapter(

                new ArrayAdapter<>(

                        this,

                        android.R.layout.simple_list_item_1,

                        smartNames

                )

        );



    }



}
