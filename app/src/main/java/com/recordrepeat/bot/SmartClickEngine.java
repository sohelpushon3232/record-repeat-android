package com.recordrepeat.bot;


import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;


public class SmartClickEngine {



    private AccessibilityService service;



    public SmartClickEngine(
            AccessibilityService service
    ){

        this.service = service;

    }





    public boolean clickText(
            String text
    ){



        if(service == null){

            return false;

        }





        AccessibilityNodeInfo root =

                service.getRootInActiveWindow();





        if(root == null){

            return false;

        }





        return searchAndClick(
                root,
                text
        );


    }








    private boolean searchAndClick(
            AccessibilityNodeInfo node,
            String text
    ){



        if(node == null){

            return false;

        }






        CharSequence nodeText =
                node.getText();





        if(nodeText != null){


            if(nodeText
                    .toString()
                    .toLowerCase()
                    .contains(
                            text.toLowerCase()
                    )){



                node.performAction(
                        AccessibilityNodeInfo
                        .ACTION_CLICK
                );


                return true;


            }


        }







        for(int i=0;i<node.getChildCount();i++){



            AccessibilityNodeInfo child =
                    node.getChild(i);




            if(searchAndClick(
                    child,
                    text
            )){


                return true;

            }


        }



        return false;


    }



}
