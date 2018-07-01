package com.moremoregreen.socialnetwork;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {
    private TextView userName, userProfName, userStatus, userCountry, userGender, userRelation, userDOB;
    private CircleImageView userProfileImage;
    private Button SendFriendRequestButton , DeclineFriendRequestButton;

    private DatabaseReference FriendRequestRef, UsersRef, FriendsRef;
    private FirebaseAuth mAuth;
    private String senderUserId , receiverUserId , CURRENT_STATE, saveCurrentDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        IntializeFields();

        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
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
                            .into(userProfileImage);

                    userName.setText("暱稱：@" + myUsername);
                    userProfName.setText("姓名：" + myProfileName);
                    userStatus.setText("狀態：" + myProfileStatus);
                    userDOB.setText("生日：" + myDOB);
                    userCountry.setText("國家：" + myCountry);
                    userGender.setText("性別：" + myGender);
                    userRelation.setText("社交關係：" + myRelationStatus);

                    MaintananceofButtons();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
        DeclineFriendRequestButton.setEnabled(false);

        if(!senderUserId.equals(receiverUserId)){
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendFriendRequestButton.setEnabled(false);
                    Toast.makeText(PersonProfileActivity.this, "請求已送出", Toast.LENGTH_SHORT).show();

                    if(CURRENT_STATE.equals("not_friends")) {
                        SendFriendRequestToaPerson();
                    }
                    if(CURRENT_STATE.equals("request_sent")) {
                        CancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received")){
                        AcceptFriendrequest();
                    }
                    if(CURRENT_STATE.equals("friends")){
                        UnFriendsAnExistingFriend();
                    }
                }
            });
        }else {
            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);  //自己不能加自己好友，所以隱藏button
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void UnFriendsAnExistingFriend() {
        FriendsRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            SendFriendRequestButton.setEnabled(true);
                                            CURRENT_STATE = "not_friends";
                                            SendFriendRequestButton.setText("發送好友邀請");
                                            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            DeclineFriendRequestButton.setEnabled(false);
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendrequest() {
        //取得日期
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());
        FriendsRef.child(senderUserId).child(receiverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsRef.child(receiverUserId).child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FriendRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    FriendRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    SendFriendRequestButton.setEnabled(true);
                                                                                    CURRENT_STATE = "friends";
                                                                                    SendFriendRequestButton.setText("解除好友關係");
                                                                                    DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                                    DeclineFriendRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            SendFriendRequestButton.setEnabled(true);
                                            CURRENT_STATE = "not_friends";
                                            SendFriendRequestButton.setText("發送好友邀請");
                                            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            DeclineFriendRequestButton.setEnabled(false);
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintananceofButtons() {
        FriendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserId)){
                            String request_type = dataSnapshot.child(receiverUserId).child("request_type")
                                    .getValue().toString();
                            if(request_type.equals("sent")){
                                CURRENT_STATE = "request_sent";
                                SendFriendRequestButton.setText("取消好友邀請");
                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestButton.setEnabled(false);
                            }else if (request_type.equals("received")){
                                CURRENT_STATE = "request_received";
                                SendFriendRequestButton.setText("接受好友邀請");
                                DeclineFriendRequestButton.setVisibility(View.VISIBLE);
                                DeclineFriendRequestButton.setEnabled(true);
                                DeclineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelFriendRequest();
                                    }
                                });
                            }
                        }
                        else {
                            FriendsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiverUserId)){
                                                CURRENT_STATE = "friends";
                                                SendFriendRequestButton.setText("解除好友關係");
                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendFriendRequestToaPerson() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            SendFriendRequestButton.setEnabled(true);
                                            CURRENT_STATE = "request_sent";
                                            SendFriendRequestButton.setText("取消好友邀請");
                                            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            DeclineFriendRequestButton.setEnabled(false);
                                        }
                                    });
                        }
                    }
                });
    }

    private void IntializeFields() {
        userName = findViewById(R.id.person_full_name);
        userProfName = findViewById(R.id.person_username);
        userStatus = findViewById(R.id.person_profile_status);
        userCountry = findViewById(R.id.person_country);
        userGender = findViewById(R.id.person_gender);
        userRelation = findViewById(R.id.person_relationship);
        userDOB = findViewById(R.id.person_dob);
        userProfileImage = findViewById(R.id.person_profile_pic);
        SendFriendRequestButton = findViewById(R.id.person_send_friend_requset_button);
        DeclineFriendRequestButton = findViewById(R.id.person_decline_friend_requset);

        CURRENT_STATE = "not_friends";

    }
}
