package com.example.android.pets.data;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.android.pets.EditorActivity;
import com.example.android.pets.R;
import com.example.android.pets.data.PetContract.PetEntry;

import static android.support.v4.app.ActivityCompat.startActivity;


/**
 * Created by sejal on 03-11-2017.
 */

public class PetProvider extends ContentProvider {
    private PetDbhelper mDbhelper;
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private static final UriMatcher suriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PETS = 100;
    private static final int PET_ID = 101;

    static {
        suriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        suriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);

    }

    @Override
    public boolean onCreate() {
        mDbhelper = new PetDbhelper(getContext());
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase database = mDbhelper.getReadableDatabase();
        Cursor cursor;
        int match = suriMatcher.match(uri);
        switch (match) {
            case PETS:
                cursor = database.query(PetContract.PetEntry.TABLENAME, strings, s, strings1, null, null, s1);
                break;
            case PET_ID:
                s = PetContract.PetEntry._ID + "=?";
                strings1 = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetContract.PetEntry.TABLENAME, strings, s, strings1, null, null, s1);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = suriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = suriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues contentValues) {
        String name = contentValues.getAsString(PetEntry.COLUMN_PET_NAME);
        if (name == null || name.length()==0) {
            Toast.makeText(getContext(), R.string.Pet_requires_a_name, Toast.LENGTH_LONG).show();
            return null;
        }

        // Check that the gender is valid
        Integer gender = contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetEntry.isValidGender(gender)) {
            Toast.makeText(getContext(), R.string.Pet_requires_a_gender, Toast.LENGTH_LONG).show();
            return null; }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer weight = contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight <=0) {
            Toast.makeText(getContext(), R.string.Pet_requires_a_weight, Toast.LENGTH_LONG).show();
            return null;
        }

        SQLiteDatabase database = mDbhelper.getWritableDatabase();
        long id = database.insert(PetContract.PetEntry.TABLENAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase database = mDbhelper.getWritableDatabase();
        int match = suriMatcher.match(uri);
        int rowUpdated;
        switch (match) {
            case PETS:
                 rowUpdated= database.delete(PetEntry.TABLENAME,s,strings);
                   break;
            case PET_ID:
                s=PetEntry._ID+"=?";
                strings=new String[]{String.valueOf(ContentUris.parseId(uri))};
                 rowUpdated= database.delete(PetEntry.TABLENAME,s,strings);
                   break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }
        if (rowUpdated!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int match = suriMatcher.match(uri);
        switch (match) {
            case PETS:
          return updatePet(uri,contentValues,s,strings);

            case PET_ID:
           s=PetEntry._ID+"=?";
                strings=new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,contentValues,s,strings);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    private int updatePet(Uri uri, ContentValues contentValues, String s, String[] strings) {
        if (contentValues.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = contentValues.getAsString(PetEntry.COLUMN_PET_NAME);
            if (name == null|| name.length()==0) {
                Toast.makeText(getContext(), R.string.Pet_requires_a_name, Toast.LENGTH_LONG).show();
                return 0;
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (contentValues.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetEntry.isValidGender(gender)) {
                Toast.makeText(getContext(), R.string.Pet_requires_a_gender, Toast.LENGTH_LONG).show();
                return 0;
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (contentValues.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight <= 0) {
                Toast.makeText(getContext(), R.string.Pet_requires_a_weight, Toast.LENGTH_LONG).show();
                return 0;
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database=mDbhelper.getWritableDatabase();
        //return database.update(PetEntry.TABLENAME,contentValues,s,strings);

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(PetEntry.TABLENAME, contentValues, s, strings);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
                  }

        // Return the number of rows updated
        return rowsUpdated;

    }
}
