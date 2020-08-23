package com.example.ecommerce_app.ViewHolder;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.Interface.itemClickListener;
import com.example.ecommerce_app.R;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_app.R;
import com.rey.material.widget.Spinner;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtproduct_name, txtproduct_desc, txtproduct_pri;
    public ImageView imgview;
    public itemClickListener listner;


    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);


        txtproduct_desc = itemView.findViewById(R.id.product_desc);
        imgview = itemView.findViewById(R.id.product_image);
        txtproduct_pri = itemView.findViewById(R.id.product_price);
        txtproduct_name = itemView.findViewById(R.id.product_name);
    }

    public void setItemCLickListener(itemClickListener listner) {
        this.listner = listner;
    }


    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }
}
