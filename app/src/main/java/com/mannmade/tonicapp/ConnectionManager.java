package com.mannmade.tonicapp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mannb3ast on 11/2/2015.
 */
public class ConnectionManager {
    Context mainContext;

    public ConnectionManager(Context ctext){
        this.mainContext = ctext;
    }
    public String connectToURL(String givenLink){
        //use string builder because every normal string concatenation creates a new string object. StringBuilder manages it all into one and is more efficient
        StringBuilder jsonBuilder = new StringBuilder();
        try{
            //Connect to given URL
            URL givenURL = new URL(givenLink);
            HttpURLConnection connection = (HttpURLConnection) givenURL.openConnection();
            InputStream iStream = connection.getInputStream();

            //Read in page
            BufferedReader reader = new BufferedReader(new InputStreamReader(iStream, "UTF-8"));
            String lineFromFile;

            //loop through and create string by concatenating all lines from page
            while ((lineFromFile = reader.readLine()) != null){
                jsonBuilder.append(lineFromFile);
                jsonBuilder.append(("\n"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return jsonBuilder.toString();
    }
}
