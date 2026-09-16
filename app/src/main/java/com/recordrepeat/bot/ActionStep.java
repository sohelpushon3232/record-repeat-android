package com.recordrepeat.bot;

import org.json.JSONObject;


public class ActionStep {


    // পুরোনো Record / Replay system
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
    ){

        this.action = action;

        this.x = x;

        this.y = y;

        this.text = text;

        this.delay = delay;

    }



    // New-style getters
    // Smart system compatibility এর জন্য রাখা হয়েছে

    public String getType(){

        return action;

    }


    public int getX(){

        return x;

    }


    public int getY(){

        return y;

    }


    public String getValue(){

        return text;

    }


    public long getDelay(){

        return delay;

    }



    public JSONObject toJSON(){

        JSONObject object =
                new JSONObject();

        try{

            // পুরোনো format
            object.put(
                    "action",
                    action
            );

            object.put(
                    "x",
                    x
            );

            object.put(
                    "y",
                    y
            );

            object.put(
                    "text",
                    text
            );

            object.put(
                    "delay",
                    delay
            );


            // নতুন format compatibility
            object.put(
                    "type",
                    action
            );

            object.put(
                    "value",
                    text
            );


        }catch(Exception e){

            e.printStackTrace();

        }


        return object;

    }



    public static ActionStep fromJSON(
            JSONObject object
    ){

        try{

            String actionValue;

            String textValue;


            // পুরোনো saved workflow হলে
            if(object.has("action")){

                actionValue =
                        object.optString(
                                "action",
                                ""
                        );

            }else{

                // নতুন format হলে
                actionValue =
                        object.optString(
                                "type",
                                ""
                        );

            }



            if(object.has("text")){

                textValue =
                        object.optString(
                                "text",
                                ""
                        );

            }else{

                textValue =
                        object.optString(
                                "value",
                                ""
                        );

            }



            return new ActionStep(

                    actionValue,

                    object.optInt(
                            "x",
                            0
                    ),

                    object.optInt(
                            "y",
                            0
                    ),

                    textValue,

                    object.optLong(
                            "delay",
                            500
                    )

            );


        }catch(Exception e){

            e.printStackTrace();

            return null;

        }

    }

}
