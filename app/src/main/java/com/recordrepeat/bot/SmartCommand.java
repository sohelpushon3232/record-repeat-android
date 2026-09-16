package com.recordrepeat.bot;


import org.json.JSONObject;



public class SmartCommand {


    private String command;

    private String value;

    private long waitTime;





    public SmartCommand(
            String command,
            String value,
            long waitTime
    ){

        this.command = command;

        this.value = value;

        this.waitTime = waitTime;

    }







    public String getCommand(){

        return command;

    }






    public String getValue(){

        return value;

    }






    public long getWaitTime(){

        return waitTime;

    }








    public JSONObject toJSON(){


        JSONObject object =
                new JSONObject();


        try{


            object.put(
                    "command",
                    command
            );


            object.put(
                    "value",
                    value
            );


            object.put(
                    "wait",
                    waitTime
            );



        }catch(Exception e){



        }



        return object;


    }









    public static SmartCommand fromJSON(
            JSONObject object
    ){



        try{


            return new SmartCommand(

                    object.getString(
                            "command"
                    ),


                    object.getString(
                            "value"
                    ),


                    object.getLong(
                            "wait"
                    )

            );



        }catch(Exception e){


            return null;

        }



    }



}
