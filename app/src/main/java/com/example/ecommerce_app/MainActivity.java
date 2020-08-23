package com.example.ecommerce_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce_app.Model.Users;
import com.example.ecommerce_app.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference Rootref;

    private ProgressDialog loader;
    private Button join_now, login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        triggerandListeners();


    }

    public void triggerandListeners() {

        join_now = findViewById(R.id.join_now_btn);
        login_btn = findViewById(R.id.main_login_btn);
        loader = new ProgressDialog(this);

        join_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, LoginActivity.class));

            }
        });

        Paper.init(this);


        String UserPhonekey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordkey = Paper.book().read(Prevalent.UserPasswordKey);


        if (UserPhonekey != "" && UserPasswordkey != "") {
            if (!TextUtils.isEmpty(UserPhonekey) && !TextUtils.isEmpty(UserPasswordkey)) {
                AllowAccess(UserPhonekey, UserPasswordkey);


                loader.setTitle("Login...");
                loader.setMessage("Please wait...We are checking the credential");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

            }
        }


    }

    private void AllowAccess(final String phone, final String pass) {


        database = FirebaseDatabase.getInstance();
        Rootref = database.getReference();
        Rootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()) {

                    Users userdata = dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    if (userdata.getPhone().equals(phone)) {

                        if (userdata.getPassword().equals(pass)) {

                            Toast.makeText(MainActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                            loader.dismiss();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = userdata;
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Password Doesn't Match", Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                        }


                    }


                } else {
                    Toast.makeText(MainActivity.this, "You don't have an account with number:" + phone, Toast.LENGTH_SHORT).show();
                    loader.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}