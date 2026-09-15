package com.recordrepeat.bot;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;


public class TouchRecorderService extends AccessibilityService {


    private static TouchRecorderService instance;

    private RecordManager recordManager;


    public static TouchRecorderService getInstance(){

        return instance;

    }


    @Override
    public void onServiceConnected(){

        super.onServiceConnected();

        instance = this;

        recordManager = new RecordManager(this);


        Toast.makeText(
                this,
                "Recorder Ready",
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


        AccessibilityNodeInfo node =
                event.getSource();



        if(node != null){


            // Text capture

            if(node.getText() != null){

                String text =
                        node.getText().toString();


                if(!text.isEmpty()){


                    ActionStep textStep =
                            new ActionStep(
                                    "TEXT",
                                    0,
                                    0,
                                    text,
                                    1000
                            );


                    recordManager.addStep(textStep);

                }

            }



            // View/Class capture

            if(node.getClassName() != null){


                ActionStep viewStep =
                        new ActionStep(
                                "VIEW",
                                0,
                                0,
                                node.getClassName().toString(),
                                500
                        );


                recordManager.addStep(viewStep);

            }


        }

    }



    @Override
    public void onInterrupt(){

    }


    @Override
    public void onDestroy(){

        instance = null;

        super.onDestroy();

    }

}
