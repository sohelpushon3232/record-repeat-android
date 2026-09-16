package com.recordrepeat.bot;


import org.json.JSONObject;


public class ActionStep {


    private String type;

    private int x;

    private int y;

    private String value;

    private long delay;



    public ActionStep(
            String type,
            int x,
            int y,
            String value,
            long delay
    ){

        this.type = type;

        this.x = x;

        this.y = y;

        this.value = value;

        this.delay = delay;

    }





    public String getType(){

        return type;

    }



    public int getX(){

        return x;

    }



    public int getY(){

        return y;

    }



    public String getValue(){

        return value;

    }



    public long getDelay(){

        return delay;

    }







    public JSONObject toJSON(){


        JSONObject obj =
                new JSONObject();


        try{


            obj.put(
                    "type",
                    type
            );


            obj.put(
                    "x",
                    x
            );


            obj.put(
                    "y",
                    y
            );


            obj.put(
                    "value",
                    value
            );


            obj.put(
                    "delay",
                    delay
            );


        }catch(Exception ignored){}



        return obj;


    }







    public static ActionStep fromJSON(
            JSONObject obj
    ){


        try{


            return new ActionStep(

                    obj.getString("type"),

                    obj.getInt("x"),

                    obj.getInt("y"),

                    obj.getString("value"),

                    obj.getLong("delay")

            );


        }catch(Exception e){


            return null;

        }


    }


}
