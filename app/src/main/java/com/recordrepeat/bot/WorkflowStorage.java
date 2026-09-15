package com.recordrepeat.bot;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WorkflowStorage {


    private static final String PREF =
            "workflow_storage";


    private static final String KEY =
            "saved_workflows";



    public static void saveWorkflow(
            Context context,
            String name,
            List<ActionStep> steps
    ){

        try{

            SharedPreferences pref =
                    context.getSharedPreferences(
                            PREF,
                            Context.MODE_PRIVATE
                    );


            JSONArray workflows =
                    new JSONArray(
                            pref.getString(
                                    KEY,
                                    "[]"
                            )
                    );


            JSONObject workflow =
                    new JSONObject();


            workflow.put(
                    "name",
                    name
            );


            JSONArray actions =
                    new JSONArray();


            for(ActionStep step : steps){

                JSONObject obj =
                        new JSONObject();


                obj.put(
                        "action",
                        step.action
                );


                obj.put(
                        "x",
                        step.x
                );


                obj.put(
                        "y",
                        step.y
                );


                obj.put(
                        "text",
                        step.text
                );


                obj.put(
                        "delay",
                        step.delay
                );


                actions.put(obj);

            }


            workflow.put(
                    "steps",
                    actions
            );


            workflows.put(workflow);


            pref.edit()
                    .putString(
                            KEY,
                            workflows.toString()
                    )
                    .apply();


        }catch(Exception e){

            e.printStackTrace();

        }

    }



    public static JSONArray getWorkflows(
            Context context
    ){

        SharedPreferences pref =
                context.getSharedPreferences(
                        PREF,
                        Context.MODE_PRIVATE
                );


        try{

            return new JSONArray(
                    pref.getString(
                            KEY,
                            "[]"
                    )
            );


        }catch(Exception e){

            return new JSONArray();

        }

    }

}
