package com.mannmade.tonicapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {
    ConnectionManager cManager;
    JSONParser jParser;
    String json;
    ArrayList<LinkedHashMap<String, String>> peopleList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize
        cManager = new ConnectionManager(this.getApplicationContext());
        jParser = new JSONParser();

        //Borrowed raw URL from github link
        Thread downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                json = cManager.connectToURL("https://gist.githubusercontent.com/zorn/6572637c1a1cbb89d4c9/raw/88c80043d4bf6d0feac11de9a575db4573a9b024/people.json");
                Log.i("Downloading from URL", "JSON Object = " + json);
                peopleList = jParser.getJSONforString(json);
            }
        });
        downloadThread.start();
    }
}
