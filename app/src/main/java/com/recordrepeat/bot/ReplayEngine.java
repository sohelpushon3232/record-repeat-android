package com.recordrepeat.bot;

import android.graphics.Path;
import android.accessibilityservice.GestureDescription;

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

                try {

                    Thread.sleep(1000);


                    if(step.type.equals("CLICK")){

                        String[] data =
                                step.data.split(",");


                        float x =
                                Float.parseFloat(data[0]);

                        float y =
                                Float.parseFloat(data[1]);


                        performClick(x,y);

                    }


                } catch(Exception e){

                    e.printStackTrace();

                }

            }

        }).start();

    }


    private void performClick(
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
