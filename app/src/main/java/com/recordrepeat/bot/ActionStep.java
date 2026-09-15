package com.recordrepeat.bot;

public class ActionStep {

    public String action;
    public int x;
    public int y;
    public String text;
    public long delay;

    public ActionStep(
            String action,
            int x,
            int y,
            String text,
            long delay
    ) {
        this.action = action;
        this.x = x;
        this.y = y;
        this.text = text;
        this.delay = delay;
    }
}
