package com.recordrepeat.bot;


import android.view.accessibility.AccessibilityNodeInfo;


import java.util.List;



public class AutoRepeatEngine {



    private TouchRecorderService service;


    private volatile boolean running = false;



    private Thread worker;






    public AutoRepeatEngine(
            TouchRecorderService service
    ){

        this.service = service;

    }








    public void start(

            List<ActionStep> steps,

            int repeatCount

    ){



        if(service == null || steps == null){

            return;

        }





        stop();



        running = true;





        worker = new Thread(() -> {



            int count = 0;





            while(
                    running
                    &&
                    (
                    repeatCount == 0
                    ||
                    count < repeatCount
                    )
            ){





                for(ActionStep step : steps){



                    if(!running){

                        return;

                    }






                    try{



                        Thread.sleep(
                                Math.max(
                                        300,
                                        step.delay
                                )
                        );





                        execute(step);




                    }catch(Exception e){


                        e.printStackTrace();


                    }



                }





                count++;


            }




            running = false;



        });





        worker.start();



    }









    private void execute(
            ActionStep step
    ){



        switch(step.action){



            case "CLICK":


                boolean clicked =

                        clickSmartElement(
                                step
                        );



                if(!clicked){


                    service.performTap(

                            step.x,

                            step.y

                    );


                }


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









    private boolean clickSmartElement(
            ActionStep step
    ){



        AccessibilityNodeInfo root =

                service.getRootInActiveWindow();






        if(root == null){

            return false;

        }







        AccessibilityNodeInfo target =

                findNode(
                        root,
                        step
                );







        if(target != null){



            target.performAction(

                    AccessibilityNodeInfo
                    .ACTION_CLICK

            );



            target.recycle();


            root.recycle();



            return true;


        }





        root.recycle();


        return false;


    }









    private AccessibilityNodeInfo findNode(

            AccessibilityNodeInfo node,

            ActionStep step

    ){



        if(node == null){

            return null;

        }







        String text = "";



        if(node.getText()!=null){


            text =
                    node.getText()
                    .toString();


        }







        String desc = "";



        if(node.getContentDescription()!=null){


            desc =
                    node.getContentDescription()
                    .toString();


        }







        if(
                (!step.viewText.isEmpty()
                &&
                step.viewText.equals(text))

                ||

                (!step.contentDescription.isEmpty()
                &&
                step.contentDescription.equals(desc))
        ){


            return node;


        }







        for(int i=0;i<node.getChildCount();i++){



            AccessibilityNodeInfo child =

                    findNode(

                            node.getChild(i),

                            step

                    );





            if(child != null){


                return child;


            }


        }






        return null;



    }









    public void stop(){


        running = false;



        if(worker != null){


            worker.interrupt();


            worker = null;


        }


    }



}
