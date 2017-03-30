package com.example.gesanidas.unipipmsplishopping.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;



public class CartContentProvider extends ContentProvider
{
    private CartDbHelper cartDbHelper;
    public static final int CART =100;
    public static final int CART_WITH_ID =101;
    private static final UriMatcher sUriMatcher=buildUriMatcher();

    public static UriMatcher buildUriMatcher()
    {
        // this is a uri matcher to distinguish between two kinds of uris
        UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CartContract.AUTHORITY,CartContract.PATH_CART, CART);
        uriMatcher.addURI(CartContract.AUTHORITY, CartContract.PATH_CART + "/#", CART_WITH_ID);
        return uriMatcher;
    }


    @Override
    public boolean onCreate()
    {
        Context context = getContext();
        cartDbHelper = new CartDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        final SQLiteDatabase db = cartDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match)
        {
            case CART:
                retCursor =  db.query(CartContract.CartEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CART_WITH_ID:
                retCursor =  db.query(CartContract.CartEntry.TABLE_NAME,
                        projection,
                        CartContract.CartEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        final SQLiteDatabase db = cartDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match)
        {
            case CART:

                long id = db.insert(CartContract.CartEntry.TABLE_NAME, null, values);
                if ( id > 0 )
                {
                    returnUri = ContentUris.withAppendedId(CartContract.CartEntry.CONTENT_URI, id);
                }
                else
                {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = cartDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int productsDeleted;

        switch (match)
        {
            case CART_WITH_ID:

                String id = uri.getPathSegments().get(1);
                productsDeleted = db.delete(CartContract.CartEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (productsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return productsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }
}