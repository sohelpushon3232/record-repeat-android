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

public static List<ActionStep> loadWorkflow(
        Context context,
        String workflowName
){

    ArrayList<ActionStep> steps =
            new ArrayList<>();


    JSONArray workflows =
            getWorkflows(context);


    try{


        for(int i = 0; i < workflows.length(); i++){


            JSONObject workflow =
                    workflows.getJSONObject(i);



            if(workflow
                    .getString("name")
                    .equals(workflowName)){



                JSONArray actions =
                        workflow.getJSONArray(
                                "steps"
                        );



                for(int j = 0;
                     j < actions.length();
                     j++){



                    JSONObject obj =
                            actions.getJSONObject(j);



                    steps.add(
                            new ActionStep(
                                    obj.getString("action"),
                                    obj.getInt("x"),
                                    obj.getInt("y"),
                                    obj.getString("text"),
                                    obj.getLong("delay")
                            )
                    );

                }


                break;

            }


        }


    }catch(Exception e){

        e.printStackTrace();

    }


    return steps;

}
}
