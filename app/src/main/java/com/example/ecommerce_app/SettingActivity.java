package com.example.ecommerce_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce_app.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView changephoto, close_btn, save_btn;
    private EditText fullname_edit, userPhone_edit, address_edit;
    private Uri imageuri;
    private String myUrl = "";
    private StorageReference storageProfileReference;
    private StorageTask uploadTask;
    private String checker = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        storageProfileReference = FirebaseStorage.getInstance().getReference().child("Profile pictures ");


        profile_image = findViewById(R.id.setting_profile_image);
        changephoto = findViewById(R.id.profile_change_btn);
        close_btn = findViewById(R.id.close_setting_btn);
        save_btn = findViewById(R.id.update_settings_btn);
        fullname_edit = findViewById(R.id.setting_full_name);
        userPhone_edit = findViewById(R.id.setting_phone_no);
        address_edit = findViewById(R.id.setting_full_Address);

        userInfoDisplay(profile_image, fullname_edit, userPhone_edit, address_edit);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checker.equals("clicked")) {

                    userInfoSaved();
                } else {

                    updateOnlyUserInfo();
                }


            }
        });

        changephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                CropImage.activity(imageuri).setAspectRatio(1, 1).start(SettingActivity.this);
            }
        });


    }

    private void updateOnlyUserInfo() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();

        userMap.put("name", fullname_edit.getText().toString());
        userMap.put("address", address_edit.getText().toString());
        userMap.put("phone", userPhone_edit.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingActivity.this, HomeActivity.class));
        Toast.makeText(SettingActivity.this, "Updated Successfull", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageuri = result.getUri();
            profile_image.setImageURI(imageuri);

        } else {


            Toast.makeText(SettingActivity.this, "Error : Try Again ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingActivity.this, SettingActivity.class));
            finish();
        }

    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(fullname_edit.getText().toString())) {
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(address_edit.getText().toString())) {
            Toast.makeText(this, "Address is mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userPhone_edit.getText().toString())) {
            Toast.makeText(this, "Phone Number is mandatory", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait .. we are updating your Profile");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageuri != null) {
            final StorageReference fileRef = storageProfileReference.child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageuri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();

                    }

                    return fileRef.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();


                        myUrl = downloadUrl.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();

                        userMap.put("name", fullname_edit.getText().toString());
                        userMap.put("address", address_edit.getText().toString());
                        userMap.put("phone", userPhone_edit.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


                        progressDialog.dismiss();
                        startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                        Toast.makeText(SettingActivity.this, "Updated Successfull", Toast.LENGTH_SHORT).show();
                        finish();


                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, "Error :", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(this, "Image IS not Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void userInfoDisplay(final CircleImageView profile_image, final EditText fullname_edit, final EditText userPhone_edit, final EditText address_edit) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    if (snapshot.child("image").exists()) {

                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();


                        Picasso.get().load(image).into(profile_image);
                        fullname_edit.setText(name);
                        userPhone_edit.setText(phone);
                        address_edit.setText(address);


                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}