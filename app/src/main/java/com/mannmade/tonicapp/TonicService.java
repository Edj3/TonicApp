package com.mannmade.tonicapp;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.UserDictionary;
import android.widget.Toast;

/**
 * EJ Mann made this
 */
public class TonicService extends Service {

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    /** Called when the service is being created. */
    @Override
    public void onCreate() {

    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), R.string.tonic_service_started, Toast.LENGTH_LONG).show();

        //Implementation of Content Resolver to get the first alphabetized word of the users dictionary
        ContentResolver resolver = getContentResolver();
        String[] projection = new String[]{BaseColumns._ID, UserDictionary.Words.WORD};
        Cursor cursor = resolver.query(
                UserDictionary.Words.CONTENT_URI,
                projection,
                null,
                null,
                UserDictionary.Words.WORD +" ASC"
        );

        if (cursor != null) {
            long id = cursor.getLong(0);
            String word = cursor.getString(1);
            //Display Toast message that shows the first element in the user dictionary
            Toast.makeText(getApplicationContext(), ("The first alphabetized element in my dictionary and its ID is" + "(" + id + ")" + " " + word), Toast.LENGTH_LONG).show();
            cursor.close();
        }


        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(10000);
                    stopService();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        return mStartMode;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), R.string.tonic_service_ended, Toast.LENGTH_LONG).show();
    }

    // We want to stop the service within the service class itself, so that's why this is here
    public void stopService() {
        stopService(new Intent(getBaseContext(), TonicService.class));
    }
}