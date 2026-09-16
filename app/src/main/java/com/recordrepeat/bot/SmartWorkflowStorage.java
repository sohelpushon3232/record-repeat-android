package com.recordrepeat.bot;


import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;



public class SmartWorkflowStorage {


    private static final String PREF_NAME =
            "SMART_WORKFLOW_PREF";


    private static final String KEY_WORKFLOW =
            "SMART_WORKFLOWS";






    public static void saveWorkflow(
            Context context,
            String name,
            ArrayList<SmartCommand> commands
    ){


        try{


            SharedPreferences pref =
                    context.getSharedPreferences(
                            PREF_NAME,
                            Context.MODE_PRIVATE
                    );



            JSONArray workflows =
                    new JSONArray(
                            pref.getString(
                                    KEY_WORKFLOW,
                                    "[]"
                            )
                    );



            JSONArray steps =
                    new JSONArray();




            for(SmartCommand command : commands){


                steps.put(
                        command.toJSON()
                );


            }




            JSONObject workflow =
                    new JSONObject();



            workflow.put(
                    "name",
                    name
            );



            workflow.put(
                    "steps",
                    steps
            );




            workflows.put(
                    workflow
            );




            pref.edit()
                    .putString(
                            KEY_WORKFLOW,
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


        try{


            SharedPreferences pref =
                    context.getSharedPreferences(
                            PREF_NAME,
                            Context.MODE_PRIVATE
                    );



            return new JSONArray(

                    pref.getString(
                            KEY_WORKFLOW,
                            "[]"
                    )

            );



        }catch(Exception e){


            return new JSONArray();


        }


    }









    public static ArrayList<SmartCommand> loadWorkflow(
            Context context,
            String name
    ){


        ArrayList<SmartCommand> commands =
                new ArrayList<>();



        try{


            JSONArray workflows =
                    getWorkflows(
                            context
                    );



            for(int i=0;i<workflows.length();i++){


                JSONObject workflow =
                        workflows.getJSONObject(i);



                if(workflow
                        .getString("name")
                        .equals(name)){



                    JSONArray steps =
                            workflow.getJSONArray(
                                    "steps"
                            );



                    for(int j=0;j<steps.length();j++){


                        SmartCommand command =
                                SmartCommand.fromJSON(
                                        steps.getJSONObject(j)
                                );



                        if(command != null){


                            commands.add(
                                    command
                            );


                        }


                    }


                }


            }



        }catch(Exception e){


            e.printStackTrace();


        }



        return commands;


    }






    public static void deleteWorkflow(
            Context context,
            String name
    ){


        try{


            SharedPreferences pref =
                    context.getSharedPreferences(
                            PREF_NAME,
                            Context.MODE_PRIVATE
                    );



            JSONArray oldList =
                    getWorkflows(
                            context
                    );



            JSONArray newList =
                    new JSONArray();




            for(int i=0;i<oldList.length();i++){


                JSONObject item =
                        oldList.getJSONObject(i);



                if(!item
                        .getString("name")
                        .equals(name)){


                    newList.put(
                            item
                    );


                }


            }




            pref.edit()
                    .putString(
                            KEY_WORKFLOW,
                            newList.toString()
                    )
                    .apply();



        }catch(Exception e){


            e.printStackTrace();


        }


    }


}
