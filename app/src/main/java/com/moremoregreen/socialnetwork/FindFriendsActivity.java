package com.moremoregreen.socialnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class FindFriendsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mToolbar = findViewById(R.id.find_friends_bar_layout);
        setSupportActionBar(mToolbar);//剛打完會error，要去import加上 .support.v7變成android.support.v7.widget.Toolbar;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //這邊是要弄個向左的箭頭(←)讓使用者可以視覺上可以點擊返回MainAct
        getSupportActionBar().setTitle("尋找朋友");

    }
}
