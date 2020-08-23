package com.example.ecommerce_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText username, user_pass, user_phone;
    private Button create_account_btn;
    private TextView Already;
    private ProgressDialog loadingbar;
    private FirebaseDatabase database;
    private DatabaseReference Rootref;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        triggerandlistenerregister();

    }

    public void triggerandlistenerregister() {

        username = findViewById(R.id.usrename_register);
        user_pass = findViewById(R.id.password_register);
        user_phone = findViewById(R.id.Phone_number_register);
        create_account_btn = findViewById(R.id.create_account_register);
        Already = findViewById(R.id.Already_register);
        loadingbar = new ProgressDialog(this);

        user_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, "Clicked_phone_btn", Toast.LENGTH_SHORT).show();
            }
        });

        user_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, "Clicked_pass_btn", Toast.LENGTH_SHORT).show();
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, "Clicked_username _btn", Toast.LENGTH_SHORT).show();
            }
        });

        create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAcount();
            }
        });


        Already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, "Clicked_Alredy _btn", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void CreateAcount() {

        String name = username.getText().toString();
        String phone = user_phone.getText().toString();
        String pass = user_pass.getText().toString();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(phone) && TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "You must fill all the details", Toast.LENGTH_SHORT).show();
        } else {

            loadingbar.setTitle("Creating Account");
            loadingbar.setMessage("Please wait...");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();


            ValidatePhoneNumber(name, phone, pass);

        }


    }

    private void ValidatePhoneNumber(final String name, final String phone, final String pass) {

        database = FirebaseDatabase.getInstance();
        Rootref = database.getReference();

        Rootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.child("Users").child(phone).exists())) {

                    HashMap<String, Object> userMap = new HashMap<>();

                    userMap.put("name", name);
                    userMap.put("phone", phone);
                    userMap.put("password", pass);

                    Rootref.child("Users").child(phone).updateChildren(userMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(RegisterActivity.this, "YOur Account is Created ", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    } else {
                                        loadingbar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Failed to create Account ", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });

                } else {
                    Toast.makeText(RegisterActivity.this, "This" + phone + "Already exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Try with another phone number", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {


            }
        });


    }


}