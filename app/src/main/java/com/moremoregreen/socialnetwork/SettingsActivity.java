package com.moremoregreen.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    private Button updateAccountSettingButton;
    private CircleImageView userProfImage;
    private DatabaseReference SettingsUserRef;
    private StorageReference UserProfileImageRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private ProgressDialog loadingBar;

    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile Images");

        mToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("帳戶設定");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        userName = findViewById(R.id.settings_username);
        userProfName = findViewById(R.id.settings_profile_full_name);
        userStatus = findViewById(R.id.settings_status);
        userCountry = findViewById(R.id.settings_country);
        userGender = findViewById(R.id.settings_gender);
        userRelation = findViewById(R.id.settings_relationgship_status);
        userDOB = findViewById(R.id.settings_dob);

        updateAccountSettingButton = findViewById(R.id.update_account_settings_buttons);
        userProfImage = findViewById(R.id.settings_profile_image);
        loadingBar = new ProgressDialog(this);

        SettingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationStatus = dataSnapshot.child("relationshipstatus").getValue().toString();

                    Picasso.get().load(myProfileImage)
                            .placeholder(R.drawable.profile)
                            .into(userProfImage);

                    userName.setText(myUsername);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDOB.setText(myDOB);
                    userCountry.setText(myCountry);
                    userGender.setText(myGender);
                    userRelation.setText(myRelationStatus);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateAccountSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAccountInfo();
            }
        });

        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

    }

    // 4-14 V20 Android7.0 會先選圖後讓user選擇圖片來源，造成再選一次圖，過於冗長
    //***** 重要 *****
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data!= null){
            Uri ImageUri = data.getData();
            //這邊要去build.gradle,manifests加library(ArthurHub)
            //https://github.com/ArthurHub/Android-Image-Cropper
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){

                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please Wait for updating your profile image!");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                Uri resultUri = result.getUri();
                StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");

                //儲存corpImage到fireStorage,接下來要存到fireBaseDataBase
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this,
                                    "Profile Image Stored Successfully to Firebase Storage!"
                                    , Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            SettingsUserRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent selfIntent = new Intent(SettingsActivity.this,SettingsActivity.class);
                                                startActivity(selfIntent);

                                                Toast.makeText(SettingsActivity.this,
                                                        "Profile Image Stored to Firebase Database Successfully!",
                                                        Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }else {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SettingsActivity.this, "Error occured:"+ message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "Error Occured: Image can be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }



    private void validateAccountInfo() {
        String username = userName.getText().toString();
        String profilename = userProfName.getText().toString();
        String status = userStatus.getText().toString();
        String dob = userDOB.getText().toString();
        String country = userCountry.getText().toString();
        String gender = userGender.getText().toString();
        String relationship = userRelation.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "請輸入暱稱", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(profilename)){
            Toast.makeText(this, "請輸入姓名", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(status)){
            Toast.makeText(this, "請輸入狀態", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(dob)){
            Toast.makeText(this, "請輸入生日", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(country)){
            Toast.makeText(this, "請輸入國家", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(gender)){
            Toast.makeText(this, "請輸入性別", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(relationship)){
            Toast.makeText(this, "請輸入人際關係", Toast.LENGTH_SHORT).show();
        }else {
            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("Please Wait for updating your profile image!");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            UpdateAccountInfo(username, profilename, status, dob, country, gender, relationship);
        }


    }

    private void UpdateAccountInfo(String username, String profilename, String status
            , String dob, String country, String gender, String relationship) {
        HashMap userMap = new HashMap();
        userMap.put("username", username);
        userMap.put("fullname", profilename);
        userMap.put("status", status);
        userMap.put("dob", dob);
        userMap.put("country", country);
        userMap.put("gender", gender);
        userMap.put("relationshipstatus", relationship);

        SettingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    SendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "帳戶資訊已儲存", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }else {
                    String message = task.getException().getMessage();
                    Toast.makeText(SettingsActivity.this, "帳戶資訊儲存失敗", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Log.d("帳戶資訊儲存失敗: ",  message);
                }
            }
        });

    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}
