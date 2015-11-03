package com.mannmade.tonicapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    ConnectionManager cManager;
    JSONParser jParser;
    String json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize
        cManager = new ConnectionManager(this.getApplicationContext());
        jParser = new JSONParser();

        //Borrowed raw URL from github link
        json = cManager.connectToURL("https://gist.githubusercontent.com/zorn/6572637c1a1cbb89d4c9/raw/88c80043d4bf6d0feac11de9a575db4573a9b024/people.json");
        Log.i("Downloading from URL", "JSON Object = " + json);
        jParser.getJSONforString(json);
    }
}
