package com.recordrepeat.bot;


import android.os.Handler;
import android.os.Looper;

import android.view.accessibility.AccessibilityNodeInfo;



import java.util.ArrayList;



public class SmartAutomationEngine {


    private TouchRecorderService service;


    private Handler handler =
            new Handler(
                    Looper.getMainLooper()
            );



    public SmartAutomationEngine(
            TouchRecorderService service
    ){

        this.service = service;

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

                command.getWaitTime()

        );



    }









    private void runCommand(
            SmartCommand command
    ){



        String type =
                command.getCommand();



        String value =
                command.getValue();





        if(type.equals("OPEN_APP")){


            service.openApp(
                    value
            );


        }







        else if(type.equals("CLICK_TEXT")){


            clickText(
                    value
            );


        }







        else if(type.equals("TYPE_TEXT")){


            service.typeText(
                    value
            );


        }







        else if(type.equals("WAIT")){


            // শুধু wait করবে


        }



    }








    private void clickText(
            String text
    ){


        AccessibilityNodeInfo root =
                service.getRootInActiveWindow();



        if(root == null){

            return;

        }





        ArrayList<AccessibilityNodeInfo> nodes =
                new ArrayList<>();



        findTextNodes(
                root,
                text,
                nodes
        );





        if(!nodes.isEmpty()){


            AccessibilityNodeInfo node =
                    nodes.get(0);



            node.performAction(
                    AccessibilityNodeInfo
                    .ACTION_CLICK
            );


        }



    }









    private void findTextNodes(
            AccessibilityNodeInfo node,
            String text,
            ArrayList<AccessibilityNodeInfo> result
    ){



        if(node == null){

            return;

        }




        if(node.getText()!=null
                &&
                node.getText()
                .toString()
                .equalsIgnoreCase(text)){


            result.add(
                    node
            );


        }





        for(int i=0;i<node.getChildCount();i++){


            findTextNodes(

                    node.getChild(i),

                    text,

                    result

            );


        }



    }



}
