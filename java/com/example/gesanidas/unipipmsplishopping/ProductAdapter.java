package com.example.gesanidas.unipipmsplishopping;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

public class ProductAdapter extends FirebaseRecyclerAdapter<Product, ProductHolder>
{
    //this is a high-level abstraction adapter that allows better retrieval from the firebase database
    private Context context;

    public ProductAdapter(Class<Product> modelClass, int modelLayout, Class<ProductHolder> viewHolderClass, DatabaseReference ref, Context context)
    {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(ProductHolder viewHolder, final Product model, int position)
    {

        //the recyler biew only shows pictures of the products. it is implemented here with glide
        Glide.with(context).load(model.getPhotoUrl()).into(viewHolder.image);

        //onclick saves the product to an intent that fires the detail activity of each product
        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(context,DetailActivity.class);
                intent.putExtra("product",model);
                context.startActivity(intent);

            }
        });



    }
}
