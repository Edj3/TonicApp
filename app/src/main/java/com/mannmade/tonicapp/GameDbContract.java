package com.mannmade.tonicapp;

import android.provider.BaseColumns;

/**
 * Created by Mannb3ast on 11/25/2015.
 */
public final class GameDbContract{

    // Empty Constructor to prevent someone from accidentally instantiating the contract class
    public GameDbContract() {}

    /* Inner class that defines the table contents */
    public static abstract class GameEntry implements BaseColumns {
        public static final String TABLE_NAME = "gametable";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_JOB = "job";
        public static final String COLUMN_NAME_ABILITY = "ability";
    }
}