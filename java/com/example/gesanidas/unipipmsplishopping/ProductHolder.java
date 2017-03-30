package com.example.gesanidas.unipipmsplishopping;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public class ProductHolder extends RecyclerView.ViewHolder
{
    public ImageView image;
    public ProductHolder(View itemView)
    {
        super(itemView);
        image = (ImageView)itemView.findViewById(R.id.productImage);

    }

}