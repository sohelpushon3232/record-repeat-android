package com.recordrepeat.bot;

import java.util.ArrayList;
import java.util.List;


public class RecordManager {


    private static final RecordManager INSTANCE =
            new RecordManager();


    private final ArrayList<ActionStep> steps =
            new ArrayList<>();


    private boolean recording = false;


    private long lastTime = 0;



    private RecordManager(){}



    public static RecordManager getInstance(){

        return INSTANCE;

    }




    public synchronized void startRecording(){


        steps.clear();


        recording = true;


        lastTime = System.currentTimeMillis();


    }






    public synchronized void stopRecording(){


        recording = false;


        lastTime = 0;


    }





    public synchronized boolean isRecording(){

        return recording;

    }





    public synchronized void clearSteps(){

        steps.clear();

    }







    public synchronized void addStep(
            ActionStep step
    ){


        if(!recording || step == null){

            return;

        }





        long now =
                System.currentTimeMillis();





        step.delay =
                Math.max(
                        200,
                        Math.min(
                                now-lastTime,
                                3000
                        )
                );





        lastTime = now;




        steps.add(step);



    }








    public synchronized List<ActionStep> getSteps(){


        return new ArrayList<>(steps);


    }






    public synchronized int getStepCount(){


        return steps.size();


    }



}
