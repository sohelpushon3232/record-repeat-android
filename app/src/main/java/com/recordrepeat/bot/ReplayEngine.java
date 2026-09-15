package com.recordrepeat.bot;

import java.util.ArrayList;

public class ReplayEngine {

    private boolean running = false;


    public void run(
            ArrayList<ActionStep> steps
    ) {

        running = true;

        for(ActionStep step : steps){

            if(!running){
                break;
            }

            try {

                Thread.sleep(1000);

                // Future:
                // Click
                // Type
                // App switch

            } catch(Exception e){

                e.printStackTrace();

            }
        }
    }


    public void stop(){

        running = false;

    }
}
