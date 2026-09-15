package com.recordrepeat.bot;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
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


        String text = "";

        if(event.getText() != null){

            text = event.getText().toString();

        }


        // Event save করার base
        if(!text.isEmpty()){

            recordManager.addStep(
                    "TEXT",
                    text
            );

        }

    }


    @Override
    public void onInterrupt() {

    }


    public static AutomationAccessibilityService getInstance(){

        return instance;

    }
}
