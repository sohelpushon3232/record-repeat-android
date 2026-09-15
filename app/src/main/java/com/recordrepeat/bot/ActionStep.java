package com.recordrepeat.bot;

public class ActionStep {

    public String type;
    public String data;
    public long delay;

    public ActionStep(String type, String data, long delay) {
        this.type = type;
        this.data = data;
        this.delay = delay;
    }
}
