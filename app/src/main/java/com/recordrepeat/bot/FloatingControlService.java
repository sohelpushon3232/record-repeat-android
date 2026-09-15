package com.recordrepeat.bot;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


public class FloatingControlService extends Service {


    private WindowManager windowManager;

    private View floatingView;



    @Override
    public void onCreate(){

        super.onCreate();



        windowManager =
                (WindowManager)
                getSystemService(
                        WINDOW_SERVICE
                );



        Button button =
                new Button(this);


        button.setText(
                "BOT"
        );



        button.setOnClickListener(v -> {


            Toast.makeText(
                    this,
                    "Automation Control",
                    Toast.LENGTH_SHORT
            ).show();


        });



        WindowManager.LayoutParams params =
                new WindowManager.LayoutParams();


        params.width = 150;

        params.height = 150;

        params.gravity =
                Gravity.TOP |
                Gravity.RIGHT;


        params.format =
                PixelFormat.TRANSLUCENT;


        params.type =
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;


        params.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;



        floatingView = button;



        windowManager.addView(
                floatingView,
                params
        );

    }




    @Override
    public void onDestroy(){

        super.onDestroy();


        if(floatingView != null){

            windowManager.removeView(
                    floatingView
            );

        }

    }




    @Override
    public IBinder onBind(
            Intent intent
    ){

        return null;

    }


}
