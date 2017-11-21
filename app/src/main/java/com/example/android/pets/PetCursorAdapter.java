package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by sejal on 04-11-2017.
 */

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor cursor){
        super(context,cursor,0);

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name= (TextView) view.findViewById(R.id.name);
        TextView breed= (TextView) view.findViewById(R.id.summary);
        String nName=cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String nbreed=cursor.getString(cursor.getColumnIndexOrThrow("breed"));
        if (TextUtils.isEmpty(nbreed)) {
            nbreed = context.getString(R.string.unknown_breed);
        }
        name.setText(nName);
        breed.setText(nbreed);

    }
}
