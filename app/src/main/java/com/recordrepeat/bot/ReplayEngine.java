package com.recordrepeat.bot;

import android.content.Context;
import android.widget.Toast;

import java.util.List;


public class ReplayEngine {


    private Context context;


    public ReplayEngine(Context context){

        this.context = context;

    }



    public void start(
            int repeatCount,
            List<ActionStep> steps
    ){


        new Thread(() -> {


            for(int i = 0; i < repeatCount; i++){


                for(ActionStep step : steps){


                    try{

                        Thread.sleep(
                                step.delay
                        );


                        execute(step);


                    }catch(Exception e){

                        e.printStackTrace();

                    }

                }

            }


            Toast.makeText(
                    context,
                    "Automation Finished",
                    Toast.LENGTH_SHORT
            ).show();


        }).start();


    }



    private void execute(
            ActionStep step
    ){


        if(step.action.equals("CLICK")){


            System.out.println(
                    "Click: " + step.text
            );


        }



        if(step.action.equals("TEXT")){


            System.out.println(
                    "Type: " + step.text
            );


        }



        if(step.action.equals("VIEW")){


            System.out.println(
                    "View: " + step.text
            );


        }


    }

}
