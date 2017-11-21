package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by sejal on 27-10-2017.
 */

public final class PetDbhelper extends SQLiteOpenHelper {
    private final static String db_name="pets.db";
    private final static int db_ver=1;

    public PetDbhelper(Context context){
        super(context,db_name,null,db_ver);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_pets="CREATE TABLE "+ PetContract.PetEntry.TABLENAME+" ("+
                PetContract.PetEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                PetContract.PetEntry.COLUMN_PET_NAME+" TEXT NOT NULL, "+
                PetContract.PetEntry.COLUMN_PET_BREED+" TEXT, "+
                PetContract.PetEntry.COLUMN_PET_GENDER+" INTEGER NOT NULL, "+
                PetContract.PetEntry.COLUMN_PET_WEIGHT+" INTEGER NOT NULL DEFAULT 0 );";
        sqLiteDatabase.execSQL(create_pets);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    }
