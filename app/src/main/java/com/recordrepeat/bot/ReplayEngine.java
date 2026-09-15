package com.recordrepeat.bot;

import java.util.List;

public class ReplayEngine {

    private final TouchRecorderService service;

    private volatile boolean running = false;

    public ReplayEngine(TouchRecorderService service) {
        this.service = service;
    }

    public void start(
            List<ActionStep> steps,
            int repeatCount
    ) {

        if (service == null) return;

        running = true;

        new Thread(() -> {

            for (int r = 0;
                 r < repeatCount && running;
                 r++) {

                for (ActionStep step : steps) {

                    if (!running) return;

                    try {

                        Thread.sleep(
                                Math.max(100, step.delay)
                        );

                        switch (step.action) {

                            case "OPEN_APP":

                                service.openApp(step.text);
                                break;

                            case "CLICK":

                                service.performTap(
                                        step.x,
                                        step.y
                                );
                                break;

                            case "TEXT":

                                service.typeText(step.text);
                                break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            running = false;

        }).start();
    }

    public void stop() {
        running = false;
    }
}
