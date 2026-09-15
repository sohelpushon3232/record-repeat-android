package com.recordrepeat.bot;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

public class AutomationAccessibilityService extends AccessibilityService {


    private static AutomationAccessibilityService instance;

    private RecordManager recordManager;


    @Override
    protected void onServiceConnected() {

        super.onServiceConnected();

        instance = this;

        recordManager = new RecordManager();

        Toast.makeText(
                this,
                "Automation Service Ready",
                Toast.LENGTH_SHORT
        ).show();
    }


    @Override
    public void onAccessibilityEvent(
            AccessibilityEvent event
    ) {

        if(event == null){
            return;
        }


        AccessibilityNodeInfo node =
                event.getSource();


        if(node != null){

            CharSequence text =
                    node.getText();


            if(text != null){

                recordManager.addStep(
                        "TEXT",
                        text.toString()
                );

            }


            String className =
                    node.getClassName()
                            != null ?
                    node.getClassName().toString()
                    :
                    "";


            recordManager.addStep(
                    "VIEW",
                    className
            );
        }


    }


    @Override
    public void onInterrupt(){

    }


    public static AutomationAccessibilityService getInstance(){

        return instance;

    }
}
