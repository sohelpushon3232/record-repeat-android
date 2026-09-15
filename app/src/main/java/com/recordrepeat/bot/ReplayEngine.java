package com.recordrepeat.bot;

import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import java.util.ArrayList;


public class ReplayEngine {


    private boolean running = false;

    private AutomationAccessibilityService service;


    public ReplayEngine(
            AutomationAccessibilityService service
    ){

        this.service = service;

    }



    public void run(
            ArrayList<ActionStep> steps
    ){

        running = true;


        new Thread(() -> {


            for(ActionStep step : steps){


                if(!running){
                    break;
                }


                try{

                    Thread.sleep(1000);


                    if(step.type.equals("CLICK")){


                        String[] point =
                                step.data.split(",");


                        float x =
                                Float.parseFloat(point[0]);


                        float y =
                                Float.parseFloat(point[1]);


                        click(x,y);

                    }


                }catch(Exception e){

                    e.printStackTrace();

                }

            }


        }).start();

    }



    private void click(
            float x,
            float y
    ){

        Path path = new Path();

        path.moveTo(x,y);


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
