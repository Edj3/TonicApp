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
import android.widget.ToggleButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

        //Practice Implementation of Anonymous Class
        SpecialString myString = new SpecialString(new myInterface() {
            @Override
            public void printOut() {
                Log.v("Special String", "This is my special string called from my anonymous class");
            }
        });
    }

    //New class Special String that takes in the interface
    public class SpecialString{
        SpecialString(myInterface i){
            i.printOut();
        }
    }
    //this is called after onCreate and then is repeatedly called after onRestart
    @Override
    protected void onStart() {
        registerTonicReceiver();
        List<String> myList = Arrays.asList("hello", "helicopter", "hellbent", "hell frozen over");
        Log.v("Word Pattern Match", String.valueOf(isSamePattern("abba", "dog cat cat dog")));
        Log.v("Common Prefix", commonPrefix(myList));
        Log.v("Reverse String", reverseString("STRESSED"));
        Log.v("Palindrome", isPalindrome("Level").toString());
        Log.v("Integer to String", intToString(25));
        Log.v("is Anagram", String.valueOf(isAnagram("abc", "cab")));
        Log.v("is Prime Number", String.valueOf(isPrime(49)));
        //Toast.makeText(getApplicationContext(), commonPrefix(myList), Toast.LENGTH_LONG).show();
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

        Log.v("Before Reversal", new String(stringArray));

        for(int i =0; i < numberOfLoops; i++){
            //temp storage of current element
            temp = stringArray[i];
            //set first character to last character (remember, last element is carriage return that's why we use -1 for maxLength value
            stringArray[i] = stringArray[maxLength - i];
            //set last character to temp value
            stringArray[maxLength - i] = temp;
        }

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

        Log.v("Palindrome Given", new String(stringArray));
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
            Log.v("Current Prefix", commonPre);
        }


        return commonPre;
    }

    public boolean isSamePattern(String pattern, String str){
        //basic null check
        if((str == null) || pattern == null){
            return false;
        }

        //split word using space as delimiter
        String[] words = str.split("\\s");

        //if lengths arent equal, the list of words does not match the pattern due to the extra words
        if(pattern.length() != words.length){
            return false;
        }

        //initialize and use two Hashmaps for swapping (Maps have no duplicates in java)
        HashMap<Character, String> map1 = new HashMap<>();
        HashMap<String, Character> map2 = new HashMap<>();

        //for loop that goes through each character in pattern
        for(int i = 0; i < pattern.length(); i++){
            System.out.println("Current Element = " + i);

            //check if map one contains the current character, if it doesnt, add it to map1. If it does, check to see if the current word matches the current entry of map one with a key of the current pattern character
            if(map1.containsKey(pattern.charAt(i))){
                //return false if current word does not match word at current element with key of current character
                if(!words[i].equals(map1.get(pattern.charAt(i)))){
                    return false;
                }
            }else{
                //add to map1 statement
                map1.put(pattern.charAt(i), words[i]);
                //if map2 does not contain the key of the current word, then add it to map 2
                if(!map2.containsKey(words[i])){
                    map2.put(words[i], pattern.charAt(i));
                }else{
                    //if the current word does not exist in both map2 & map1, then return false. You have an extra word brother.
                    return false;
                }
            }
        }
        return true;
    }

    public String intToString(int number) {  //Handle 0 - 99
        //Example = 6 - "Six"
        LinkedHashMap<Integer, String> intStringMap = new LinkedHashMap<Integer, String>();
        intStringMap.put(0, "Zero"); //Handle 0 thru 19
        intStringMap.put(1, "One");
        intStringMap.put(2, "Two");
        intStringMap.put(3, "Three");
        intStringMap.put(4, "Four");
        intStringMap.put(5, "Five");
        intStringMap.put(6, "Six");
        intStringMap.put(7, "Seven");
        intStringMap.put(8, "Eight");
        intStringMap.put(9, "Nine");
        intStringMap.put(10, "Ten");
        intStringMap.put(11, "Eleven");
        intStringMap.put(12, "Twelve");
        intStringMap.put(13, "Thirteen");
        intStringMap.put(14, "Fourteen");
        intStringMap.put(15, "Fifteen");
        intStringMap.put(16, "Sixteen");
        intStringMap.put(17, "Seventeen");
        intStringMap.put(18, "Eighteen");
        intStringMap.put(19, "Nineteen");
        intStringMap.put(20, "Twenty"); //Handle base 10 from 20 to 90
        intStringMap.put(30, "Thirty");
        intStringMap.put(40, "Forty");
        intStringMap.put(50, "Fifty");
        intStringMap.put(60, "Sixty");
        intStringMap.put(70, "Seventy");
        intStringMap.put(80, "Eigthy");
        intStringMap.put(90, "Ninety");

        if (number <= 20) {
            return intStringMap.get(number);
        }

        if(number > 20 && number <= 99){
            int singleDigit = number % 10; //will give me single digit of given number
            int doubleDigit = number - (number % 10); //will give me base 10 value to determine first word
            return intStringMap.get(doubleDigit) + " " + intStringMap.get(singleDigit);
        }

        return "Number not between 0 and 99";
    }

    public boolean isAnagram(String phrase1, String phrase2) {
        //Sort the 2 character arrays of the strings and then compare them element for element!
        //Example: abc vs cab
        char[] char1 = phrase1.toCharArray();
        char[] char2 = phrase2.toCharArray();
        Arrays.sort(char1);
        Arrays.sort(char2);
        return Arrays.equals(char1, char2);
    }

    //checks whether an int is prime or not.
    boolean isPrime(int n) {
        //check if n is a multiple of 2
        if (n % 2 == 0) return false;

        //if not, then just check the odds
        for(int i = 3; i * i <= n; i += 2) { //This is important, we are incrementing by each odd number (starting from 3, incrementing by 2) up to the square root of n
            //this means you only go through half the factors of n, thus increasing performance rather than going thru all of n's factors!
            if(n % i == 0)
                return false;
        }
        return true;
    }

    //Practice Interface
    public interface myInterface{
        void printOut();
    }
}
