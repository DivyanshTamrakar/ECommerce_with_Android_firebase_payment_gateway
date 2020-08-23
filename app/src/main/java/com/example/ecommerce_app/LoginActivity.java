package com.example.ecommerce_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce_app.Model.Users;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;
import com.example.ecommerce_app.Prevalent.*;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText username, login_pass;
    private Button login;
    private TextView forget_pass, AdminLink, NotAdminLink;
    private ProgressDialog loader;
    private FirebaseDatabase database;
    private DatabaseReference Rootref;
    private CheckBox rememeberme;
    private String parentdbname = "Users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.usrename);
        login_pass = findViewById(R.id.password);
        login = findViewById(R.id.signbtn);
        forget_pass = findViewById(R.id.forget_pass);
        loader = new ProgressDialog(this);
        rememeberme = findViewById(R.id.remember);
        AdminLink = findViewById(R.id.admin);
        NotAdminLink = findViewById(R.id.not_admin);

        Paper.init(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "Forget Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentdbname = "Admins";

            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentdbname = "Users";


            }
        });

    }

    private void LoginUser() {

        String phone = username.getText().toString();
        String pass = login_pass.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Phone number Field required", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Password is must", Toast.LENGTH_SHORT).show();
        } else {


            loader.setTitle("Login...");
            loader.setMessage("Please wait...We are checking the credential");
            loader.setCanceledOnTouchOutside(false);
            loader.show();

            AllowAccess(phone, pass);


        }


    }

    private void AllowAccess(final String phone, final String pass) {

        if (rememeberme.isChecked()) {

            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, pass);
        }

        System.out.println("Phone " + phone);
        System.out.println("Phone " + pass);


        database = FirebaseDatabase.getInstance();
        Rootref = database.getReference();
        Rootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.child(parentdbname).child(phone).exists()) {

                    Users userdata = dataSnapshot.child(parentdbname).child(phone).getValue(Users.class);

                    if (userdata.getPhone().equals(phone)) {

                        if (userdata.getPassword().equals(pass)) {

                            if (parentdbname.equals("Admins")) {

                                Toast.makeText(LoginActivity.this, " Admin Login Sucessfull", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                                startActivity(new Intent(LoginActivity.this, AdminCategoryActivity.class));


                            } else if (parentdbname.equals("Users")) {

                                Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                loader.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = userdata;
                                startActivity(intent);


                            } else {
                                Toast.makeText(LoginActivity.this, "Nothing happend", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(LoginActivity.this, "Password Doesn't Match", Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                        }


                    }


                } else {
                    Toast.makeText(LoginActivity.this, "You don't have an account with number:" + phone, Toast.LENGTH_SHORT).show();
                    loader.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(LoginActivity.this, "NGOsjkgasxjgs", Toast.LENGTH_SHORT).show();

            }
        });


    }
}