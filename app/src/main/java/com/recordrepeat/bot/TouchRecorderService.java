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



    private final Handler handler =
            new Handler(
                    Looper.getMainLooper()
            );



    private String lastPackage = "";



    private long lastClickTime = 0;


    private int lastX = -1;


    private int lastY = -1;







    public static TouchRecorderService getInstance(){

        return instance;

    }






    public static boolean isConnected(){

        return instance != null;

    }







    @Override
    protected void onServiceConnected(){


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


        if(event == null){

            return;

        }





        RecordManager manager =
                RecordManager.getInstance();





        if(!manager.isRecording()){

            return;

        }






        String packageName = "";



        if(event.getPackageName()!=null){


            packageName =
                    event.getPackageName()
                    .toString();


        }







        if(packageName.equals(getPackageName())){


            return;


        }








        // APP OPEN RECORD


        if(event.getEventType()
                ==
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){



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

                                500

                        )

                );

            }


        }







        AccessibilityNodeInfo node =
                event.getSource();





        if(node == null){

            return;

        }






        // CLICK RECORD


        if(event.getEventType()
                ==
                AccessibilityEvent.TYPE_VIEW_CLICKED){



            Rect rect =
                    new Rect();



            node.getBoundsInScreen(
                    rect
            );




            int x =
                    rect.centerX();


            int y =
                    rect.centerY();






            if(x <= 0 || y <= 0){


                node.recycle();

                return;

            }






            long now =
                    System.currentTimeMillis();






            if(x == lastX
                    &&
                    y == lastY
                    &&
                    now-lastClickTime < 600){


                node.recycle();

                return;

            }





            lastX = x;

            lastY = y;

            lastClickTime = now;






            manager.addStep(

                    new ActionStep(

                            "CLICK",

                            x,

                            y,

                            "",

                            400

                    )

            );


        }
                // TEXT RECORD


        if(event.getEventType()
                ==
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){



            if(!node.isPassword()){



                CharSequence text =
                        node.getText();





                if(text != null){


                    String value =
                            text.toString();





                    if(!value.isEmpty()){



                        manager.replaceLastText(

                                new ActionStep(

                                        "TEXT",

                                        0,

                                        0,

                                        value,

                                        300

                                )

                        );


                    }


                }


            }



        }







        node.recycle();



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





            if(root == null){

                return;

            }







            AccessibilityNodeInfo input =

                    root.findFocus(

                            AccessibilityNodeInfo.FOCUS_INPUT

                    );







            if(input == null){

                root.recycle();

                return;

            }







            Bundle args =
                    new Bundle();







            args.putCharSequence(

                    AccessibilityNodeInfo
                    .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,

                    text

            );







            input.performAction(

                    AccessibilityNodeInfo.ACTION_SET_TEXT,

                    args

            );







            input.recycle();

            root.recycle();




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



    }









    @Override
    public void onDestroy(){



        if(instance == this){


            instance = null;


        }





        super.onDestroy();



    }




}
