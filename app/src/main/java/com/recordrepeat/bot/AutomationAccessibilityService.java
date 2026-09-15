package com.recordrepeat.bot;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class AutomationAccessibilityService extends AccessibilityService {

    private static AutomationAccessibilityService instance;

    public static AutomationAccessibilityService getInstance(){
        return instance;
    }


    @Override
    public void onServiceConnected() {
        super.onServiceConnected();

        instance = this;

        Toast.makeText(
                this,
                "Automation Service Connected",
                Toast.LENGTH_SHORT
        ).show();
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        // Future:
        // screen action capture হবে এখানে

    }


    @Override
    public void onInterrupt() {

    }


    public void performAction(){

        // Future:
        // replay action execute হবে এখানে

    }


    @Override
    public void onDestroy(){

        instance = null;
        super.onDestroy();

    }

}
