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


        lastTime =
                System.currentTimeMillis();


    }





    public synchronized void stopRecording(){


        recording = false;


        lastTime = 0;


    }






    public synchronized void clearSteps(){


        steps.clear();


        lastTime = 0;


    }







    public synchronized boolean isRecording(){


        return recording;


    }








    public synchronized void addStep(
            ActionStep step
    ){



        if(!recording)
            return;





        long now =
                System.currentTimeMillis();





        if(lastTime > 0){


            long delay =
                    now - lastTime;


            step.delay =
                    Math.max(
                            150,
                            Math.min(
                                    delay,
                                    3000
                            )
                    );


        }






        // duplicate click prevent

        if(!steps.isEmpty()){


            ActionStep last =
                    steps.get(
                            steps.size()-1
                    );



            if(
                    last.action.equals(step.action)
                    &&
                    last.x == step.x
                    &&
                    last.y == step.y
            ){


                return;


            }


        }







        steps.add(step);



        lastTime = now;



    }









    public synchronized void replaceLastText(
            ActionStep step
    ){



        if(!recording)
            return;





        if(!steps.isEmpty()){



            ActionStep last =
                    steps.get(
                            steps.size()-1
                    );





            if(
                    "TEXT".equals(last.action)
            ){



                step.delay =
                        last.delay;


                steps.set(
                        steps.size()-1,
                        step
                );


                return;


            }


        }






        addStep(step);



    }








    public synchronized List<ActionStep> getSteps(){


        return new ArrayList<>(
                steps
        );


    }



}
