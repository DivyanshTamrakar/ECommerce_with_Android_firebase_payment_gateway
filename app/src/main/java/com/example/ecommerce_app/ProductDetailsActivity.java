package com.example.ecommerce_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecommerce_app.Model.Products;
import com.example.ecommerce_app.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView product_image;
    private ElegantNumberButton number_btn;
    private TextView product_price, product_name, product_description;
    private Button add_cart, buy_now;
    private String product_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        product_id = getIntent().getStringExtra("pid");


        add_cart = findViewById(R.id.add_Cart_btn);
        product_image = findViewById(R.id.product_image_detail);
        number_btn = findViewById(R.id.number_btn);
        product_price = findViewById(R.id.product_price_details);
        product_name = findViewById(R.id.product_name_details);
        product_description = findViewById(R.id.product_desc_details);
        add_cart = findViewById(R.id.add_Cart_btn);
        buy_now = findViewById(R.id.Buy_now);


        GetProductDetails(product_id);

        add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Add_to_cart();

            }
        });


    }

    private void Add_to_cart() {

        String saveCurrentTime, saveCurrentDate;

        Calendar calfordate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calfordate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentTime = currentTime.format(calfordate.getTime());

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("CartList");

        final HashMap<String, Object> cartmap = new HashMap<>();
        cartmap.put("pid", product_id);
        cartmap.put("pname", product_name.getText().toString());
        cartmap.put("price", product_price.getText().toString());
        cartmap.put("date", saveCurrentDate);
        cartmap.put("time", saveCurrentTime);
        cartmap.put("quantity", number_btn.getNumber());
        cartmap.put("discount", "");

        cartRef.child("UserView").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(product_id)
                .updateChildren(cartmap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            cartRef.child("AdminView").child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Products").child(product_id)
                                    .updateChildren(cartmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ProductDetailsActivity.this, "Added To Cart", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(ProductDetailsActivity.this, HomeActivity.class));

                                }
                            });
                        }

                    }
                });


    }

    private void GetProductDetails(String product_id) {

        DatabaseReference product_ref = FirebaseDatabase.getInstance().getReference().child("Product");

        product_ref.child(product_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Products products = snapshot.getValue(Products.class);
                    System.out.println("productID " + products.getProduct_price());
                    product_name.setText(products.getProduct_name());
                    product_description.setText(products.getProduct_description());
                    product_price.setText("₹ " + products.getProduct_price());
                    Picasso.get().load(products.getImage()).placeholder(R.drawable.product_placeholder).into(product_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}