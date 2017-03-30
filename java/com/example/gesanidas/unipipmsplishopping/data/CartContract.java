package com.example.gesanidas.unipipmsplishopping.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class CartContract
{

    // this code contains some useful static variables to be utilised by the content provider
    public static final String AUTHORITY="com.example.gesanidas.unipipmsplishopping";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+AUTHORITY);
    public static final String PATH_CART="cart";

    public static final class CartEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CART).build();
        public static final String TABLE_NAME = "cart";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_PHOTO_URL = "photoUrl";
        public static final String COLUMN_RELEASEDATE = "release_date";

    }
}
