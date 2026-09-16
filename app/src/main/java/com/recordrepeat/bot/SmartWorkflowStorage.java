package com.recordrepeat.bot;


import android.content.Context;
import android.content.SharedPreferences;


import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;



public class SmartWorkflowStorage {



    private static final String PREF =
            "smart_workflows";


    private static final String KEY =
            "workflows";







    public static void saveWorkflow(

            Context context,

            String name,

            ArrayList<SmartCommand> commands

    ){


        try{


            JSONArray oldWorkflows =
                    getWorkflows(context);



            JSONArray workflows =
                    new JSONArray();





            // Remove same name old workflow

            for(int i=0;i<oldWorkflows.length();i++){


                JSONObject old =
                        oldWorkflows.getJSONObject(i);



                if(!old.getString("name")
                        .equals(name)){


                    workflows.put(old);


                }


            }







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









            context
                    .getSharedPreferences(
                            PREF,
                            Context.MODE_PRIVATE
                    )
                    .edit()
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


        try{


            String data =

                    context
                    .getSharedPreferences(
                            PREF,
                            Context.MODE_PRIVATE
                    )
                    .getString(
                            KEY,
                            "[]"
                    );




            return new JSONArray(
                    data
            );



        }catch(Exception e){


            return new JSONArray();


        }


    }









    public static ArrayList<SmartCommand> loadWorkflow(

            Context context,

            String name

    ){



        ArrayList<SmartCommand> list =
                new ArrayList<>();





        try{


            JSONArray workflows =
                    getWorkflows(context);







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


                            list.add(command);


                        }



                    }





                    break;


                }



            }



        }catch(Exception e){


            e.printStackTrace();


        }






        return list;


    }









    public static void deleteWorkflow(

            Context context,

            String name

    ){



        try{


            JSONArray workflows =
                    getWorkflows(context);





            JSONArray newArray =
                    new JSONArray();







            for(int i=0;i<workflows.length();i++){



                JSONObject workflow =

                        workflows.getJSONObject(i);







                if(!workflow
                        .getString("name")
                        .equals(name)){



                    newArray.put(
                            workflow
                    );


                }



            }








            context
                    .getSharedPreferences(
                            PREF,
                            Context.MODE_PRIVATE
                    )
                    .edit()
                    .putString(
                            KEY,
                            newArray.toString()
                    )
                    .apply();





        }catch(Exception e){


            e.printStackTrace();


        }



    }



}
