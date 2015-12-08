package com.mannmade.tonicapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //needs to be global to access within onCheckedListener and function that takes in parameter to switch order of names
    boolean lastNameFirst = false;
    BroadcastReceiver tonicReceiver;
    public static final String RESTART_TONIC_ACTION = "com.mannmade.tonic.custom.intent.action.RESTART_TONIC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //grab references to UI views
        final EditText getFirstName = (EditText) findViewById(R.id.get_first_name);
        final EditText getLastName = (EditText) findViewById(R.id.get_last_name);
        final TextView labelFullName = (TextView) findViewById(R.id.label_full_name);
        ToggleButton orderButton = (ToggleButton) findViewById(R.id.order_button);
        final Button getFullName = (Button) findViewById(R.id.get_full_name);
        Button clearButton = (Button) findViewById(R.id.clear_names);
        Button grabList = (Button) findViewById(R.id.grab_name_list);

        //set boolean value for order based on the value change of the order button
        orderButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lastNameFirst = isChecked;
                //process the full name only if one already exists
                if ((getFirstName.getText().toString().length() > 0 || !getFirstName.getText().toString().equalsIgnoreCase("")) ||
                    (getLastName.getText().toString().length() > 0 || !getLastName.getText().toString().equalsIgnoreCase(""))){

                    String processedFullName = processFullName(lastNameFirst, getFirstName.getText().toString(), getLastName.getText().toString());
                    labelFullName.setText(processedFullName);
                }
            }
        });

        //onlick listener for processing full name
        getFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String processedFullName = processFullName(lastNameFirst, getFirstName.getText().toString(), getLastName.getText().toString());
                labelFullName.setText(processedFullName);
            }
        });

        //clear out edittext fields for first and last name
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFirstName.getText().clear();
                getLastName.getText().clear();
                labelFullName.setText(R.string.full_name);
            }
        });

        //Throw intent to name list activity when download list is clicked
        grabList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checks if the user has an active internet / wifi connection in order to download
                if(checkConnectivity()){
                    Intent intent = new Intent(getApplicationContext(), NameListActivity.class);
                    startActivity(intent);
                }else{
                    AlertDialog.Builder connectBuilder = new AlertDialog.Builder(MainActivity.this);
                    connectBuilder.setTitle(R.string.connection_unavailable);
                    connectBuilder.setMessage(R.string.could_not_connect);
                    connectBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    connectBuilder.show();
                }
            }
        });
    }

    //this is called after onCreate and then is repeatedly called after onRestart
    @Override
    protected void onStart() {
        registerTonicReceiver();
        List<String> myList = Arrays.asList("hello", "helicopter", "hellbent", "hell frozen over");
        Toast.makeText(getApplicationContext(), commonPrefix(myList), Toast.LENGTH_LONG).show();
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Intent startTonicIntent = new Intent(RESTART_TONIC_ACTION);
        sendBroadcast(startTonicIntent);
        super.onRestart();
    }

    protected void registerTonicReceiver(){
        this.tonicReceiver = new TonicReceiver();
        registerReceiver(this.tonicReceiver, new IntentFilter(RESTART_TONIC_ACTION));
    }

    protected void writeGameJobsToDataBase(){
        //Implementation of Game SQLite Database practice
        GameDbHelper gDbHelper = new GameDbHelper(getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = gDbHelper.getWritableDatabase();

        //You can only insert one item at a time using contentValues. Use bulkinsert to do multiple inserts or cheat like this
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(GameDbContract.GameEntry.COLUMN_NAME_ENTRY_ID, 1);
        values.put(GameDbContract.GameEntry.COLUMN_NAME_JOB, "MONK");
        values.put(GameDbContract.GameEntry.COLUMN_NAME_ABILITY, "Final Heaven");

        // Insert the new row, returning the primary key value of the new row
        db.insert(GameDbContract.GameEntry.TABLE_NAME, null, values);

        // Create a new map of values, where column names are the keys
        values = new ContentValues();
        values.put(GameDbContract.GameEntry.COLUMN_NAME_ENTRY_ID, 2);
        values.put(GameDbContract.GameEntry.COLUMN_NAME_JOB, "DRK");
        values.put(GameDbContract.GameEntry.COLUMN_NAME_ABILITY, "Living Dead");

        // Insert the new row, returning the primary key value of the new row
        db.insert(GameDbContract.GameEntry.TABLE_NAME, null, values);
    }

    //Function to check if internet connection is available for the user to download the list
    protected boolean checkConnectivity(){
        ConnectivityManager userConnection = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connectInfo = userConnection.getActiveNetworkInfo();

        return (connectInfo != null && connectInfo.isConnectedOrConnecting());
    }

    //function that takes in the boolean for the order, the first name string, and the last name string and processes the full name based on the requests
    public String processFullName(boolean reverseOrder, String firstName, String lastName){
        if( (firstName.length() < 1 || firstName.equalsIgnoreCase("")) && (lastName.length() < 1 || lastName.equalsIgnoreCase("")) ){
            AlertDialog.Builder nameBlankBuilder = new AlertDialog.Builder(this);
            nameBlankBuilder.setTitle(R.string.blank_names);
            nameBlankBuilder.setMessage(R.string.blank_info);
            nameBlankBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            nameBlankBuilder.show();
            return getResources().getString(R.string.full_name);
        }else if(firstName.length() < 1 || firstName.equalsIgnoreCase("")){
            //Show only last name
            return lastName;
        }else if(lastName.length() < 1 || lastName.equalsIgnoreCase("")) {
            //Show only first name
            return firstName;
        }else{
            //order matters if both are not null
            if(!reverseOrder){
                return firstName + " " + lastName;
            }else{
                return lastName + ", " + firstName;
            }
        }
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
            //write to database before everything else fires
            writeGameJobsToDataBase();
            startService();
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

    //We want to start the service from the main activity, so create the function to access the service class
    public void startService() {
        startService(new Intent(getBaseContext(), TonicService.class));
    }

    //both onDestroy and onCreate are called once thru the entire lifetime of the app. unregister receiver here to make sure all broadcasts occur thru restart process
    @Override
    protected void onDestroy() {
        unregisterReceiver(tonicReceiver);
        super.onDestroy();
    }





    //Common Exercises for job applications
    public String reverseString(String anyString){
        char[] stringArray = anyString.toCharArray();
        char temp;
        int numberOfLoops = stringArray.length / 2;
        int maxLength = stringArray.length - 1;

        for(int i =0; i < numberOfLoops; i++){
            //temp storage of current element
            temp = stringArray[i];
            //set first character to last character (remember, last element is carriage return that's why we use -1 for maxLength value
            stringArray[i] = stringArray[maxLength - i];
            //set last character to temp value
            stringArray[maxLength - i] = temp;
        }
        Log.v("Result String", new String(stringArray));
        return new String(stringArray);
    }

    public Boolean isPalindrome(String anyString){
        char[] stringArray = anyString.toLowerCase().toCharArray();
        int numberOfLoops = stringArray.length / 2;
        int maxLength = stringArray.length - 1;
        boolean isPalin = true; //start on true so that if it passes all the way through, then it remains true

        for(int i =0; i < numberOfLoops; i++){
            if(stringArray[i] != stringArray[maxLength - i]){
                isPalin = false;
            }
        }

        Log.v("Result String", new String(stringArray));
        return isPalin;
    }

    public String commonPrefix(List<String> stringList){
        String commonPre = stringList.get(0).toLowerCase(); //commonPre is always the currently found prefix and is returned after process
        char[] a1; //character array of commonPre
        char[] a2; //character array of current element being compared to commonPre
        String newPre; //used to create newly found prefix between two current elements being compared

        //use for loop to be able to grab previous index (could have use list iterator too)
        for(int i = 0; i < stringList.size(); i++){
            if (i != 0){
                a1 = commonPre.toCharArray();
                a2 = stringList.get(i).toLowerCase().toCharArray();
                newPre = "";

                for(int j = 0; j < commonPre.length(); j++){
                    if(a1[j] != a2[j]){
                        commonPre = newPre;
                        break;
                    }else{
                        newPre = newPre + a1[j];
                    }
                }
            }
        }

        Log.v("Result String", commonPre);
        return commonPre;
    }
}
