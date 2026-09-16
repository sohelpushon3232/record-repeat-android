package com.recordrepeat.bot;


import java.util.List;



public class AutoRepeatEngine {


    private volatile boolean running = false;


    private TouchRecorderService service;


    private Thread worker;





    public AutoRepeatEngine(
            TouchRecorderService service
    ){

        this.service = service;

    }








    public synchronized void start(

            List<ActionStep> steps,

            int repeatCount

    ){



        stop();



        if(service == null || steps == null){

            return;

        }




        running = true;





        worker = new Thread(() -> {



            try {



                int current = 0;





                while(
                        running &&
                        (
                        repeatCount == 0 ||
                        current < repeatCount
                        )
                ){



                    for(ActionStep step : steps){



                        if(!running){

                            return;

                        }






                        long delay =
                                step.delay;



                        if(delay > 1500){

                            delay = 800;

                        }



                        if(delay < 100){

                            delay = 100;

                        }





                        Thread.sleep(delay);





                        execute(step);



                    }




                    current++;



                }





            }catch(Exception e){


                e.printStackTrace();



            }finally{


                running = false;


            }





        });




        worker.start();



    }









    private void execute(
            ActionStep step
    ){



        if(service == null)
            return;





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









    public synchronized void stop(){



        running = false;



        if(worker != null){


            worker.interrupt();


            worker = null;


        }


    }







    public boolean isRunning(){


        return running;


    }



}
