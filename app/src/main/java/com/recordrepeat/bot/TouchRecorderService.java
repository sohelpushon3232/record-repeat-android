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


            String text = "";


            if(node.getText()!=null){

                text = node.getText().toString();

            }



            if(!text.isEmpty()){


                ActionStep step =
                        new ActionStep(
                                "TEXT",
                                0,
                                0,
                                text,
                                1000
                        );


                recordManager.addStep(step);

            }


        }


    }



    @Override
    public void onInterrupt(){

    }


}
