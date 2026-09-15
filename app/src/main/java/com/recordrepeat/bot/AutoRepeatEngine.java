package com.recordrepeat.bot;

import java.util.List;


public class AutoRepeatEngine {


    private boolean running = false;


    private ReplayEngine replayEngine;



    public AutoRepeatEngine(
            ReplayEngine replayEngine
    ){

        this.replayEngine = replayEngine;

    }



    public void start(
            List<ActionStep> steps,
            int repeatCount
    ){

        running = true;



        new Thread(() -> {



            int count = 0;



            while(running &&
                    (repeatCount == 0 ||
                    count < repeatCount)){



                replayEngine.start(
                        steps,
                        1
                );



                count++;



                try{

                    Thread.sleep(1000);

                }catch(Exception e){

                    e.printStackTrace();

                }


            }



        }).start();


    }




    public void stop(){

        running = false;

    }



}
