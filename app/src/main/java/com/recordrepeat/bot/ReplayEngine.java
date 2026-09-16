package com.recordrepeat.bot;


import java.util.List;



public class ReplayEngine {


    private final TouchRecorderService service;


    private volatile boolean running = false;


    private Thread replayThread;





    public ReplayEngine(
            TouchRecorderService service
    ){

        this.service = service;

    }








    public void start(

            List<ActionStep> steps,

            int repeatCount

    ){



        if(service == null || steps == null)
            return;





        stop();



        running = true;





        replayThread = new Thread(() -> {



            try {



                for(int r = 0;
                    r < repeatCount && running;
                    r++){





                    for(ActionStep step : steps){



                        if(!running)
                            return;






                        long delay =
                                step.delay;





                        // delay optimize

                        if(delay > 1500){

                            delay = 1000;

                        }



                        if(delay < 150){

                            delay = 150;

                        }






                        Thread.sleep(delay);







                        switch(step.action){



                            case "OPEN_APP":



                                service.openApp(
                                        step.text
                                );


                                break;







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



                        }





                    }



                }




            }catch(Exception e){


                e.printStackTrace();



            }finally{


                running = false;


            }




        });




        replayThread.start();



    }









    public void stop(){



        running = false;



        if(replayThread != null){


            replayThread.interrupt();


            replayThread = null;


        }



    }






    public boolean isRunning(){


        return running;


    }



}
