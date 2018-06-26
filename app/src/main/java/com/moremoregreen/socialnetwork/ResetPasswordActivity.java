package com.moremoregreen.socialnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;

public class ResetPasswordActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private Button ResetPasswordSendEmailButton;
    private EditText ResetEmailInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mToolbar = findViewById(R.id.forget_passowrd_toolbar);
        setSupportActionBar(mToolbar); //要去上面改import => support.v7
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");

        ResetPasswordSendEmailButton = findViewById(R.id.reset_password_email_button);
        ResetEmailInput = findViewById(R.id.reset_password_EMAIL);
    }
}
