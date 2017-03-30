package com.example.gesanidas.unipipmsplishopping;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.example.gesanidas.unipipmsplishopping.data.CartContract;

public class CartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{

    private RecyclerView recyclerView;
    private static  CartAdapter cartAdapter;
    private static final int CART_LOADER_ID = 44;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        //this block of code implements the recyclerview, adapter ,layoutmanager and loaders
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview_cart);
        linearLayoutManager=new LinearLayoutManager(this);
        cartAdapter=new CartAdapter(CartActivity.this);
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        getSupportLoaderManager().initLoader(CART_LOADER_ID, null, this);

        //this is attached to the recycler view to provide swipe functionality
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir)
            {

                int id = (int) viewHolder.itemView.getTag();
                String stringId = Integer.toString(id);
                Uri uri = CartContract.CartEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                getContentResolver().delete(uri, null, null);
                //the loader is restarted to show the new database without the deleted item
                getSupportLoaderManager().restartLoader(CART_LOADER_ID, null, CartActivity.this);

            }
        }).attachToRecyclerView(recyclerView);

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        //we created an async task laoder that uses cursors
        return new AsyncTaskLoader<Cursor>(this)
        {
            Cursor cart=null;


            @Override
            public void onStartLoading()
            {
                if (cart != null)
                {
                    deliverResult(cart);
                }
                else
                {
                    forceLoad();
                }
            }
            @Override
            public Cursor loadInBackground()
            {


                try
                {
                    //we query the database to retrieve all items in it
                    cart=getContentResolver().query(CartContract.CartEntry.CONTENT_URI,null,null,null,null);
                    return cart;

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return null;
                }
            }





            public void deliverResult(Cursor data)
            {
                cart = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if (data != null)
        {
            cartAdapter.swapCursor(data);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        cartAdapter.swapCursor(null);

    }


}
