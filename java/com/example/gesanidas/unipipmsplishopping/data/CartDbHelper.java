package com.example.gesanidas.unipipmsplishopping.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class CartDbHelper extends SQLiteOpenHelper

{

    //this code creates the local database and the table in it
    private static final String DATABASE_NAME="cart.db";
    private static final int DATABASE_VERSION=1;



    public CartDbHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String SQL_CREATE_CART_TABLE="CREATE TABLE "+ CartContract.CartEntry.TABLE_NAME+"("+
                CartContract.CartEntry._ID+" INTEGER PRIMARY KEY  AUTOINCREMENT,  "+
                CartContract.CartEntry.COLUMN_TITLE+" TEXT NOT NULL,"+
                CartContract.CartEntry.COLUMN_PRICE+" TEXT NOT NULL ,"+
                CartContract.CartEntry.COLUMN_PHOTO_URL+" TEXT NOT NULL ,"+
                CartContract.CartEntry.COLUMN_RELEASEDATE+" TEXT NOT NULL "+");";
        db.execSQL(SQL_CREATE_CART_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+  CartContract.CartEntry.TABLE_NAME);
        onCreate(db);
    }
}
