package com.recordrepeat.bot;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;

public class AutomationAccessibilityService extends AccessibilityService {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    public void clickScreen(float x, float y) {

        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.StrokeDescription stroke =
                new GestureDescription.StrokeDescription(
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
    }

    public void startDemoAutomation() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // Example click position
                clickScreen(500, 500);

            }
        }, 2000);
    }
}
