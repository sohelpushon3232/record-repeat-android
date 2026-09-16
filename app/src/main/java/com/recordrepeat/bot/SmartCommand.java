package com.recordrepeat.bot;


import org.json.JSONObject;



public class SmartCommand {


    private String type;


    private String value;


    private long delay;





    public SmartCommand(

            String type,

            String value,

            long delay

    ){


        this.type = type;

        this.value = value;

        this.delay = delay;


    }







    public String getType(){


        return type;


    }







    public String getValue(){


        return value;


    }







    public long getDelay(){


        return delay;


    }







    public JSONObject toJSON(){



        JSONObject object =
                new JSONObject();



        try{


            object.put(
                    "type",
                    type
            );


            object.put(
                    "value",
                    value
            );


            object.put(
                    "delay",
                    delay
            );



        }catch(Exception e){


            e.printStackTrace();


        }



        return object;


    }








    public static SmartCommand fromJSON(
            JSONObject object
    ){



        try{


            return new SmartCommand(


                    object.optString(
                            "type",
                            ""
                    ),



                    object.optString(
                            "value",
                            ""
                    ),



                    object.optLong(
                            "delay",
                            1000
                    )


            );



        }catch(Exception e){


            e.printStackTrace();


            return null;


        }


    }



}
