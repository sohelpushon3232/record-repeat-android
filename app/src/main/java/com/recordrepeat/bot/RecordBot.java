package com.recordrepeat.bot;

public class RecordBot {

    private boolean running = false;

    public void startBot() {
        running = true;
        System.out.println("Record Repeat Bot Started");
    }

    public void stopBot() {
        running = false;
        System.out.println("Record Repeat Bot Stopped");
    }

    public boolean isRunning() {
        return running;
    }

    public void executeStep(String instruction) {

        if (!running) {
            return;
        }

        // এখানে পরে automation steps add হবে
        // যেমন:
        // Open app
        // Click button
        // Type text
        // Repeat action

        System.out.println("Executing: " + instruction);
    }
}
