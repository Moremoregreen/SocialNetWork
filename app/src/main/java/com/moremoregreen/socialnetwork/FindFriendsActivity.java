package com.moremoregreen.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.nio.FloatBuffer;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton SearchButton;
    private EditText SearchInputText;
    private RecyclerView SearchResultList;
    private DatabaseReference allUsersDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = findViewById(R.id.find_friends_bar_layout);
        setSupportActionBar(mToolbar);//剛打完會error，要去import加上 .support.v7變成android.support.v7.widget.Toolbar;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //這邊是要弄個向左的箭頭(←)讓使用者可以視覺上可以點擊返回MainAct
        getSupportActionBar().setTitle("尋找朋友");


        SearchResultList = findViewById(R.id.search_result_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));

        SearchButton = findViewById(R.id.search_people_friends_button);
        SearchInputText = findViewById(R.id.search_box_input);
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput =  SearchInputText.getText().toString();
                SearchPeopleAndFriends(searchBoxInput);
            }
        });

    }

    private void SearchPeopleAndFriends(String searchBoxInput) {
        Toast.makeText(this, "尋找中...", Toast.LENGTH_LONG).show();
        Query searchPeopleandFriendsQuery = allUsersDatabaseRef.orderByChild("fullname")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");
        
        //這邊先去建一個FindFriends class
        FirebaseRecyclerAdapter<FindFriedns, FindFriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindFriedns, FindFriendsViewHolder>(
                        FindFriedns.class,
                        R.layout.all_users_display_layout,
                        FindFriendsViewHolder.class,
                        searchPeopleandFriendsQuery

        ) {
            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull FindFriedns model) {

            }

            @Override
            protected void populateViewHolder(FindFriendsViewHolder viewHolder, FindFriedns model, final int position) {
                viewHolder.setFullname(model.getFullname());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setProfileimage(model.getProfileimage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        Intent profileIntent = new Intent(FindFriendsActivity.this,PersonProfileActivity.class);
                        profileIntent.putExtra("visit_user_id" , visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        SearchResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FindFriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setProfileimage(String profileimage){
            CircleImageView myImage = mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }
        public void setFullname(String fullname){
            TextView myName = mView.findViewById(R.id.all_users_profile_full_name);
            myName.setText(fullname);
        }
        public void setStatus(String status){
            TextView myStatus = mView.findViewById(R.id.all_users_status);
            myStatus.setText(status);
        }
    }
}
