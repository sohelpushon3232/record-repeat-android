package com.recordrepeat.bot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import android.os.Build;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {


    EditText workflowName;
    EditText repeatInput;

    ListView workflowList;


    ArrayList<String> names =
            new ArrayList<>();


    RecordManager recordManager;


    String selectedWorkflow = "";


    AutoRepeatEngine autoRepeatEngine;


    Button floatingButton;

    Button smartBuilderButton;




    @Override
    protected void onCreate(Bundle savedInstanceState){


        super.onCreate(savedInstanceState);



        recordManager =
                RecordManager.getInstance();




        LinearLayout layout =
                new LinearLayout(this);



        layout.setOrientation(
                LinearLayout.VERTICAL
        );



        layout.setPadding(
                30,30,30,30
        );




        workflowName =
                new EditText(this);


        workflowName.setHint(
                "Workflow Name"
        );




        repeatInput =
                new EditText(this);


        repeatInput.setHint(
                "Repeat Count"
        );


        repeatInput.setInputType(2);




        Button record =
                new Button(this);


        record.setText(
                "START RECORD"
        );




        Button save =
                new Button(this);


        save.setText(
                "SAVE WORKFLOW"
        );




        Button run =
                new Button(this);


        run.setText(
                "RUN SELECTED"
        );




        Button stop =
                new Button(this);


        stop.setText(
                "STOP REPEAT"
        );




        Button delete =
                new Button(this);


        delete.setText(
                "DELETE WORKFLOW"
        );




        floatingButton =
                new Button(this);


        floatingButton.setText(
                "START FLOATING BOT"
        );




        smartBuilderButton =
                new Button(this);


        smartBuilderButton.setText(
                "SMART BUILDER"
        );




        workflowList =
                new ListView(this);



        loadWorkflows();
