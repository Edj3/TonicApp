package com.mannmade.tonicapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class NameListActivity extends AppCompatActivity {
    //Declare
    ConnectionManager cManager;
    JSONParser jParser;
    String json;
    ArrayList<LinkedHashMap<String, String>> peopleList;
    ListView nameListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_list);

        //initialize
        cManager = new ConnectionManager(this.getApplicationContext());
        jParser = new JSONParser();
        nameListView = (ListView) findViewById(R.id.name_list_view);

        //Run AsyncTask to handle background process for downloading list of names
        new DownloadListTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            AlertDialog.Builder infoBuilder = new AlertDialog.Builder(this);
            infoBuilder.setTitle(R.string.about_logic);
            infoBuilder.setMessage(R.string.logic);
            infoBuilder.setPositiveButton(R.string.very_cool, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            infoBuilder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Uses Custom Name Base Adapter to handle processing of list with viewholder pattern
    public void setNameAdapter(){
        nameListView.setAdapter(new NameBaseAdapter(this.getBaseContext(), peopleList));
    }

    //AsyncTask to download list
    private class DownloadListTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Boolean doInBackground(Void... Void) {
            try{
                //download all images into the list that holds
                if (peopleList == null || peopleList.isEmpty()){
                    //Borrowed raw URL from github link
                    json = cManager.connectToURL("https://gist.githubusercontent.com/zorn/6572637c1a1cbb89d4c9/raw/88c80043d4bf6d0feac11de9a575db4573a9b024/people.json");
                    Log.i("Downloading from URL", "JSON Object = " + json);
                    peopleList = jParser.getJSONforString(json);
                }
                return false;
            }catch(Exception e){
                e.printStackTrace();
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean isError) {
            super.onPostExecute(isError);
            if (!isError) {
                setNameAdapter();
            }
        }
    }
}
