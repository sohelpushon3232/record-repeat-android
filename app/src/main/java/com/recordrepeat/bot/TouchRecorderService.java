package com.recordrepeat.bot;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;

import android.content.Intent;

import android.graphics.Path;
import android.graphics.Rect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import android.widget.Toast;


public class TouchRecorderService extends AccessibilityService {


    private static TouchRecorderService instance;


    private boolean connected = false;



    private final Handler handler =
            new Handler(
                    Looper.getMainLooper()
            );



    private String lastPackage = "";




    public static TouchRecorderService getInstance(){

        return instance;

    }



    public static boolean isConnected(){

        return instance != null;

    }






   @Override
protected void onServiceConnected() {

    super.onServiceConnected();


    instance = this;


    Toast.makeText(
            this,
            "Accessibility Connected",
            Toast.LENGTH_SHORT
    ).show();

}






    @Override
    public void onAccessibilityEvent(
            AccessibilityEvent event
    ){


        if(event == null) return;



        RecordManager manager =
                RecordManager.getInstance();



        if(!manager.isRecording()) return;




        String packageName = "";



        if(event.getPackageName()!=null){

            packageName =
                    event.getPackageName()
                    .toString();

        }





        if("com.recordrepeat.bot"
                .equals(packageName)){

            return;

        }





        if(event.getEventType()
                ==
                AccessibilityEvent
                .TYPE_WINDOW_STATE_CHANGED){



            if(!packageName.isEmpty()
                    &&
                    !packageName.equals(lastPackage)){



                lastPackage = packageName;



                manager.addStep(

                        new ActionStep(
                                "OPEN_APP",
                                0,
                                0,
                                packageName,
                                700
                        )

                );


            }


        }







        AccessibilityNodeInfo node =
                event.getSource();



        if(node == null) return;







        if(event.getEventType()
                ==
                AccessibilityEvent
                .TYPE_VIEW_CLICKED){



            Rect rect =
                    new Rect();


            node.getBoundsInScreen(
                    rect
            );



            manager.addStep(

                    new ActionStep(
                            "CLICK",
                            rect.centerX(),
                            rect.centerY(),
                            "",
                            500
                    )

            );


        }







        if(event.getEventType()
                ==
                AccessibilityEvent
                .TYPE_VIEW_TEXT_CHANGED){



            if(node.isPassword()){

                return;

            }




            CharSequence value =
                    node.getText();



            if(value != null){



                String text =
                        value.toString();



                if(!text.isEmpty()){


                    manager.replaceLastText(

                            new ActionStep(
                                    "TEXT",
                                    0,
                                    0,
                                    text,
                                    300
                            )

                    );

                }

            }

        }



    }









    public void performTap(
            float x,
            float y
    ){


        handler.post(() -> {


            Path path =
                    new Path();



            path.moveTo(
                    x,
                    y
            );



            GestureDescription.StrokeDescription stroke =

                    new GestureDescription
                    .StrokeDescription(
                            path,
                            0,
                            100
                    );



            GestureDescription gesture =

                    new GestureDescription.Builder()
                    .addStroke(stroke)
                    .build();




            dispatchGesture(
                    gesture,
                    null,
                    null
            );



        });


    }








    public void typeText(
            String text
    ){


        handler.post(() -> {


            AccessibilityNodeInfo root =
                    getRootInActiveWindow();



            if(root == null) return;




            AccessibilityNodeInfo node =

                    root.findFocus(
                            AccessibilityNodeInfo.FOCUS_INPUT
                    );



            if(node == null) return;





            Bundle args =
                    new Bundle();



            args.putCharSequence(

                    AccessibilityNodeInfo
                    .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,

                    text

            );



            node.performAction(

                    AccessibilityNodeInfo
                    .ACTION_SET_TEXT,

                    args

            );



        });



    }








    public void openApp(
            String packageName
    ){


        handler.post(() -> {


            Intent intent =

                    getPackageManager()
                    .getLaunchIntentForPackage(
                            packageName
                    );



            if(intent != null){


                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                );


                startActivity(intent);


            }



        });


    }








    @Override
    public void onInterrupt(){


        connected = false;


    }






    @Override
public void onDestroy() {


    if(instance == this){

        instance = null;

    }


    super.onDestroy();

}

    }


}
