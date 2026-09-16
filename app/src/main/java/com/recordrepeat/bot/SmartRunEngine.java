package com.recordrepeat.bot;


import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;



public class SmartRunEngine {



    private Context context;


    private TouchRecorderService service;



    public SmartRunEngine(
            Context context,
            TouchRecorderService service
    ){

        this.context = context;

        this.service = service;

    }







    public void runWorkflow(
            String workflowName
    ){



        if(service == null){


            Toast.makeText(
                    context,
                    "Accessibility Service Not Connected",
                    Toast.LENGTH_LONG
            ).show();


            return;


        }





        ArrayList<SmartCommand> commands =

                SmartWorkflowStorage.loadWorkflow(
                        context,
                        workflowName
                );





        if(commands.isEmpty()){


            Toast.makeText(
                    context,
                    "No Smart Steps Found",
                    Toast.LENGTH_SHORT
            ).show();


            return;


        }






        SmartAutomationEngine engine =

                new SmartAutomationEngine(
                        service
                );





        engine.start(
                commands
        );





        Toast.makeText(

                context,

                "Smart Workflow Started",

                Toast.LENGTH_SHORT

        ).show();



    }



}
