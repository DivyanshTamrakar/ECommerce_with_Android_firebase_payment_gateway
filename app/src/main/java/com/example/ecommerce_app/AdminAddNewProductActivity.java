package com.example.ecommerce_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String Category, Pname, Desc, Price, saveCurrentDate, saveCurrentTime;
    public static final int PICK_IMAGE = 1;
    private ImageView product_image;
    private EditText Product_name, Product_desc, Product_price;
    private Button Add_new_Product;
    private ProgressDialog loader;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference mStorageRef;
    private DatabaseReference ProductRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);


        Category = getIntent().getExtras().get("category").toString();


        mStorageRef = FirebaseStorage.getInstance().getReference().child("Product Images");

        ProductRef = FirebaseDatabase.getInstance().getReference().child("Product");


        Toast.makeText(AdminAddNewProductActivity.this, Category, Toast.LENGTH_SHORT).show();


        product_image = findViewById(R.id.select_product_image);
        Product_name = findViewById(R.id.product_name);
        Product_desc = findViewById(R.id.product_description);
        Product_price = findViewById(R.id.product_price);
        Add_new_Product = findViewById(R.id.Add_btn);
        loader = new ProgressDialog(this);

        product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, PICK_IMAGE);

            }

        });

        Add_new_Product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateData();
            }
        });


    }

    private void ValidateData() {

        Pname = Product_name.getText().toString();
        Desc = Product_desc.getText().toString();
        Price = Product_price.getText().toString();

        if (ImageUri == null) {

            Toast.makeText(AdminAddNewProductActivity.this, "Product Image is mandatory", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(Pname)) {

            Toast.makeText(AdminAddNewProductActivity.this, "Product name must be filled", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(Desc)) {

            Toast.makeText(AdminAddNewProductActivity.this, "Product Description must be filled", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(Price)) {

            Toast.makeText(AdminAddNewProductActivity.this, "Product Price must be filled", Toast.LENGTH_SHORT).show();

        } else {

            StoreProductInformation();
        }


    }

    private void StoreProductInformation() {


        loader.setTitle("Adding your Product..");
        loader.setMessage("Please wait... while we are adding your product");
        loader.setCanceledOnTouchOutside(false);
        loader.show();


        Calendar rightNow = Calendar.getInstance();

        SimpleDateFormat current_date = new SimpleDateFormat("yyyy-MM-dd");
        saveCurrentDate = current_date.format(rightNow.getTime());

        SimpleDateFormat current_time = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = current_time.format(rightNow.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference file_path = mStorageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = file_path.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                loader.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product image Upload Successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        downloadImageUrl = file_path.getDownloadUrl().toString();
                        return file_path.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {

                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Got the Product Image sucesfully ", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }

                    }
                });


            }
        });

    }

    private void SaveProductInfoToDatabase() {

        HashMap<String, Object> product_map = new HashMap<>();

        product_map.put("pid", productRandomKey);
        product_map.put("date", saveCurrentDate);
        product_map.put("time", saveCurrentTime);
        product_map.put("product_description", Desc);
        product_map.put("image", downloadImageUrl);
        product_map.put("category", Category);
        product_map.put("product_price", Price);
        product_map.put("product_name", Pname);

        ProductRef.child(productRandomKey).updateChildren(product_map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    loader.dismiss();

                    Toast.makeText(AdminAddNewProductActivity.this, "Product details added to database", Toast.LENGTH_SHORT).show();
                } else {
                    loader.dismiss();
                    String n = task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error " + n, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

            ImageUri = data.getData();
            product_image.setImageURI(ImageUri);
        }
    }

}