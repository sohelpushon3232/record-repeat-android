package com.recordrepeat.bot;


import android.os.Handler;
import android.os.Looper;



import java.util.ArrayList;



public class SmartAutomationEngine {



    private TouchRecorderService service;


    private Handler handler =
            new Handler(
                    Looper.getMainLooper()
            );



    private SmartClickEngine clickEngine;





    public SmartAutomationEngine(
            TouchRecorderService service
    ){

        this.service = service;


        clickEngine =
                new SmartClickEngine(
                        service
                );


    }







    public void start(
            ArrayList<SmartCommand> commands
    ){



        executeStep(
                commands,
                0
        );



    }








    private void executeStep(

            ArrayList<SmartCommand> commands,

            int index

    ){



        if(index >= commands.size()){


            return;

        }






        SmartCommand command =
                commands.get(index);






        handler.postDelayed(

                () -> {



                    runCommand(
                            command
                    );



                    executeStep(
                            commands,
                            index + 1
                    );



                },

                command.getDelay()

        );



    }









    private void runCommand(
            SmartCommand command
    ){



        String type =
                command.getType();



        String value =
                command.getValue();







        switch(type){



            case "OPEN_APP":


                service.openApp(
                        value
                );


                break;







            case "CLICK_TEXT":


                clickEngine.clickText(
                        value
                );


                break;







            case "TYPE_TEXT":


                service.typeText(
                        value
                );


                break;






            case "WAIT":


                break;



        }



    }




}
