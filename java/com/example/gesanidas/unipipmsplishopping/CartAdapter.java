package com.example.gesanidas.unipipmsplishopping;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;



public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartAdapterViewHolder>
{
    private Cursor cursor;
    private Context mContext;



    //we pass the context to the adapter constructor
    public CartAdapter(Context context)
    {

        this.mContext=context;
    }



    @Override
    public CartAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context=parent.getContext();
        int layoutIdForListItem = R.layout.cart_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new CartAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapterViewHolder holder, int position)
    {
        //we move the cursor that contains the queries we run in the database to the current position of the adapter
        cursor.moveToPosition(position);
        String title=cursor.getString(1);
        String price=cursor.getString(2);
        String url=cursor.getString(3);
        int id=cursor.getInt(0);

        holder.titleTextView.setText(title);
        holder.priceTextView.setText(price+" €");
        Glide.with(mContext).load(url).into(holder.imageView);    //we use glide to display the picture from the firebase storage
        holder.itemView.setTag(id); //we save the id of the product in the current position



    }

    @Override
    public int getItemCount()
    {
        if (null==cursor)
            return 0;
        else
            return cursor.getCount();
    }

    void swapCursor(Cursor newCursor)
    {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public class CartAdapterViewHolder extends RecyclerView.ViewHolder
    {
        //this popualtes the views of each item in the recycler view
        public final TextView titleTextView,priceTextView;
        public final ImageView imageView;


        public CartAdapterViewHolder(View view)
        {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.title);
            priceTextView=(TextView) view.findViewById(R.id.price);
            imageView=(ImageView)view.findViewById(R.id.imageView3);
        }




    }
}