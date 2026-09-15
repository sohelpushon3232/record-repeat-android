package com.recordrepeat.bot;

import java.util.List;


public class AutoRepeatEngine {


    private boolean running = false;


    private TouchRecorderService service;



    public AutoRepeatEngine(
            TouchRecorderService service
    ){

        this.service = service;

    }




    public void start(
            List<ActionStep> steps,
            int repeatCount
    ){

        running = true;



        new Thread(() -> {


            int current = 0;



            while(
                    running &&
                    (repeatCount == 0 ||
                    current < repeatCount)
            ){


                for(ActionStep step : steps){


                    if(!running){
                        return;
                    }


                    try{


                        Thread.sleep(
                                step.delay
                        );


                        execute(step);



                    }catch(Exception e){

                        e.printStackTrace();

                    }


                }


                current++;


            }



        }).start();


    }




    private void execute(
            ActionStep step
    ){


        switch(step.action){


            case "CLICK":

                service.performTap(
                        step.x,
                        step.y
                );

                break;



            case "TEXT":

                service.typeText(
                        step.text
                );

                break;



            case "OPEN_APP":

                service.openApp(
                        step.text
                );

                break;


        }


    }





    public void stop(){

        running = false;

    }


}
