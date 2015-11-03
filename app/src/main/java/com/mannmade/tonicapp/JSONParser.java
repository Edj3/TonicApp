package com.mannmade.tonicapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by Mannb3ast on 11/2/2015.
 */
public class JSONParser {
    public ArrayList<LinkedHashMap<String, String>> getJSONforString(String json){
        //One Array list to house all mappings of key value pairs
        ArrayList<LinkedHashMap<String, String>> peopleList = new ArrayList<>();

        try{
            //create JSON Object
            JSONObject readObject = new JSONObject(json);
            JSONArray peopleArray = readObject.getJSONArray("people");
            //loop thru each item in jsonArray and store key value pairs in map for each object
            for(int i = 0; i < peopleArray.length(); i++){
                JSONObject jsonItem = peopleArray.getJSONObject(i);
                Log.i("Listing Items", "Item " + i);
                Iterator<String> keys = jsonItem.keys();
                LinkedHashMap<String, String> itemMap = new LinkedHashMap<>();

                while(keys.hasNext()){
                    String key = keys.next();
                    String value = jsonItem.getString(key);
                    itemMap.put(key, value);
                }
                peopleList.add(itemMap);
            }
            Log.i("Items in Array", "The count of the items in your array list is = " + peopleList.size());
        }catch(Exception e){
            e.printStackTrace();
        }
        return peopleList;
    }
}
