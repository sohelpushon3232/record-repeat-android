package com.recordrepeat.bot;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class RecordManager {

    private boolean recording = false;
    private List<ActionStep> steps = new ArrayList<>();

    public RecordManager(Context context) {
        steps = new ArrayList<>();
    }


    public void startRecording() {
        recording = true;
        steps.clear();
    }


    public void stopRecording() {
        recording = false;
    }


    public boolean isRecording() {
        return recording;
    }


    public void addStep(ActionStep step) {
        if(recording){
            steps.add(step);
        }
    }


    public List<ActionStep> getSteps() {
        return steps;
    }


    public void clearSteps() {
        steps.clear();
    }
}
