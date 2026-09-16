package com.recordrepeat.bot;


import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

import java.util.ArrayList;



public class SmartBuilderActivity extends Activity {



    Spinner commandSpinner;

    EditText valueInput;

    EditText workflowName;


    ListView stepList;


    ArrayList<SmartCommand> commands =
            new ArrayList<>();


    ArrayList<String> displaySteps =
            new ArrayList<>();



    ArrayAdapter<String> adapter;





    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ){


        super.onCreate(savedInstanceState);




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






        commandSpinner =
                new Spinner(this);



        String[] commandTypes = {


                "OPEN_APP",

                "CLICK_TEXT",

                "TYPE_TEXT",

                "WAIT"


        };



        ArrayAdapter<String> spinnerAdapter =

                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        commandTypes
                );



        commandSpinner.setAdapter(
                spinnerAdapter
        );







        valueInput =
                new EditText(this);


        valueInput.setHint(
                "Value"
        );







        Button add =

                new Button(this);


        add.setText(
                "ADD STEP"
        );








        stepList =
                new ListView(this);



        adapter =

                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        displaySteps
                );



        stepList.setAdapter(
                adapter
        );







        Button save =

                new Button(this);


        save.setText(
                "SAVE WORKFLOW"
        );







        add.setOnClickListener(v -> {



            String command =

                    commandSpinner
                    .getSelectedItem()
                    .toString();




            String value =

                    valueInput
                    .getText()
                    .toString();





            SmartCommand step =

                    new SmartCommand(
                            command,
                            value,
                            1000
                    );



            commands.add(
                    step
            );



            displaySteps.add(

                    command +
                    " : " +
                    value

            );



            adapter.notifyDataSetChanged();



            valueInput.setText("");



        });









        save.setOnClickListener(v -> {



            String name =

                    workflowName
                    .getText()
                    .toString();



            if(name.isEmpty()){

                name =
                "Smart Workflow";

            }





            SmartWorkflowStorage.saveWorkflow(

                    this,

                    name,

                    commands

            );





            Toast.makeText(

                    this,

                    "Workflow Saved",

                    Toast.LENGTH_SHORT

            ).show();



        });







        layout.addView(workflowName);

        layout.addView(commandSpinner);

        layout.addView(valueInput);

        layout.addView(add);

        layout.addView(stepList);

        layout.addView(save);



        setContentView(layout);



    }



}
