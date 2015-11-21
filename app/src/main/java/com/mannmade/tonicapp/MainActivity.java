package com.mannmade.tonicapp;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    //needs to be global to access within onCheckedListener and function that takes in parameter to switch order of names
    boolean lastNameFirst = false;

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

    //Function to check if internet connection is available for the user to download the list
    protected boolean checkConnectivity(){
        ConnectivityManager userConnection = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connectInfo = userConnection.getActiveNetworkInfo();

        return (connectInfo != null && connectInfo.isConnectedOrConnecting());
    }

    protected void launchMessage(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_tonic_launcher)
                        .setContentTitle("You started my application!")
                        .setContentText("My God... this is incredible!");
    // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

    // The stack builder object will contain an artificial back stack for the
    // started Activity.
    // This ensures that navigating backward from the Activity leads out of
    // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
    // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pending = PendingIntent.getActivity(this.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pending);
        // Cancel the notification after its selected
        mBuilder.mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
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
            startService();
            launchMessage();
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
}
