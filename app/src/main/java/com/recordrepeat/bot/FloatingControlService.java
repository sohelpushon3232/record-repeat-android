package com.recordrepeat.bot;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


public class FloatingControlService extends Service {


    private WindowManager windowManager;

    private Button floatingButton;

    private WindowManager.LayoutParams params;



    @Override
    public void onCreate(){

        super.onCreate();



        windowManager =
                (WindowManager)
                        getSystemService(
                                WINDOW_SERVICE
                        );



        floatingButton =
                new Button(this);



        floatingButton.setText(
                "BOT"
        );



        floatingButton.setOnClickListener(v -> {


            Toast.makeText(
                    this,
                    "Automation Control",
                    Toast.LENGTH_SHORT
            ).show();


        });




        params =
                new WindowManager.LayoutParams();


        params.width = 180;

        params.height = 180;


        params.gravity =
                Gravity.TOP |
                Gravity.RIGHT;



        params.format =
                PixelFormat.TRANSLUCENT;




        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            params.type =
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        }else{

            params.type =
                    WindowManager.LayoutParams.TYPE_PHONE;

        }




        params.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;





        floatingButton.setOnTouchListener(
                (view,event)->{


                    switch(event.getAction()){


                        case MotionEvent.ACTION_DOWN:

                            return true;



                        case MotionEvent.ACTION_MOVE:


                            params.x =
                                    (int)(event.getRawX());


                            params.y =
                                    (int)(event.getRawY());



                            try{

                                windowManager.updateViewLayout(
                                        floatingButton,
                                        params
                                );


                            }catch(Exception ignored){}



                            return true;


                    }


                    return false;

                });




        try{


            windowManager.addView(
                    floatingButton,
                    params
            );


        }catch(Exception e){


            Toast.makeText(
                    this,
                    "Overlay permission required",
                    Toast.LENGTH_LONG
            ).show();


        }


    }





    @Override
    public void onDestroy(){

        super.onDestroy();



        if(floatingButton != null){


            try{

                windowManager.removeView(
                        floatingButton
                );


            }catch(Exception ignored){}



        }


    }




    @Override
    public IBinder onBind(
            Intent intent
    ){

        return null;

    }


}
