package com.recordrepeat.bot;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

public class TouchRecorderService extends AccessibilityService {

    private static TouchRecorderService instance;

    public static TouchRecorderService getInstance() {
        return instance;
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;

        Toast.makeText(
                this,
                "Recorder Service Connected",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        AccessibilityNodeInfo node = event.getSource();

        if (node != null) {

            String text = node.getText() != null
                    ? node.getText().toString()
                    : "";

            if (!text.isEmpty()) {
                System.out.println(
                    "Detected: " + text
                );
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    public void recordAction(String action) {

        System.out.println(
            "Recorded Action: " + action
        );
    }
}
