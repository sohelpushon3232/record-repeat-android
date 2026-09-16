package com.recordrepeat.bot;


public class ActionStep {


    public String action;


    public int x;

    public int y;


    public String text;


    public long delay;



    // New smart information

    public String viewText;

    public String contentDescription;

    public String className;

    public String resourceId;





    public ActionStep(
            String action,
            int x,
            int y,
            String text,
            long delay
    ){

        this.action = action;

        this.x = x;

        this.y = y;

        this.text = text;

        this.delay = delay;


        this.viewText = "";

        this.contentDescription = "";

        this.className = "";

        this.resourceId = "";

    }







    public ActionStep(
            String action,
            int x,
            int y,
            String text,
            long delay,
            String viewText,
            String contentDescription,
            String className,
            String resourceId
    ){


        this.action = action;

        this.x = x;

        this.y = y;

        this.text = text;

        this.delay = delay;


        this.viewText = viewText;

        this.contentDescription = contentDescription;

        this.className = className;

        this.resourceId = resourceId;


    }



}
