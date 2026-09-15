package com.recordrepeat.bot;

import java.util.ArrayList;

public class RecordManager {

    private ArrayList<ActionStep> steps = new ArrayList<>();

    public void addStep(String type, String data) {

        steps.add(
            new ActionStep(
                type,
                data,
                System.currentTimeMillis()
            )
        );
    }


    public ArrayList<ActionStep> getSteps() {

        return steps;
    }


    public void clear() {

        steps.clear();
    }
}
