package com.recordrepeat.bot;

import android.graphics.Path;
import android.accessibilityservice.GestureDescription;

import java.util.ArrayList;


public class ReplayEngine {

    private boolean running = false;

    private AutomationAccessibilityService service;


    public ReplayEngine(AutomationAccessibilityService service){

        this.service = service;

    }


    public void run(
            ArrayList<ActionStep> steps,
            int repeatCount
    ){

        running = true;


        new Thread(() -> {


            for(int r = 0; r < repeatCount; r++){


                for(ActionStep step : steps){


                    if(!running){
                        return;
                    }


                    try{

                        Thread.sleep(
                                step.delay
                        );


                        if(step.type.equals("CLICK")){


                            String[] pos =
                                    step.data.split(",");


                            float x =
                                    Float.parseFloat(pos[0]);


                            float y =
                                    Float.parseFloat(pos[1]);


                            click(
                                    x,
                                    y
                            );

                        }


                    }catch(Exception e){

                        e.printStackTrace();

                    }

                }

            }


        }).start();

    }



    private void click(
            float x,
            float y
    ){

        if(service == null){
            return;
        }


        Path path = new Path();

        path.moveTo(
                x,
                y
        );


        GestureDescription.StrokeDescription stroke =
                new GestureDescription.StrokeDescription(
                        path,
                        0,
                        100
                );


        GestureDescription gesture =
                new GestureDescription.Builder()
                        .addStroke(stroke)
                        .build();


        service.dispatchGesture(
                gesture,
                null,
                null
        );

    }



    public void stop(){

        running = false;

    }

}
