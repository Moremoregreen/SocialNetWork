package com.moremoregreen.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreatAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        UserEmail = findViewById(R.id.register_email);
        UserPassword = findViewById(R.id.register_password);
        UserConfirmPassword = findViewById(R.id.register_confirm_password);
        CreatAccountButton = findViewById(R.id.register_creat_account);
        loadingBar = new ProgressDialog(this);

        CreatAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatNewAccount();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            SendUserToMainActivity();
        }
    }
    private void SendUserToMainActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, MainActivity.class);

        //ClearTast->摧毀目標Activity和它之上的所有Acitvity,重新創建目標Activity
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
    private void CreatNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write your Email...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please write your Password...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(this, "Please confirm your Password...", Toast.LENGTH_SHORT).show();
        }else if(!password.equals(confirmPassword)){
            Toast.makeText(this, "Your password don't match with your confirm password...", Toast.LENGTH_SHORT).show();
        }else {
            //彈出一個對話框告知使用者目前正在創建帳戶避免使用者會以為程式當掉, 而把程式強制關閉。
            //這個就是ProgressDialog的用途。
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please Wait, while we are creating your new Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                SendUserToSetupActivity();//做到這邊時發生OutOfMemery，去Manifests+largeHeap=true
                                Toast.makeText(RegisterActivity.this, "You are authenticated successfully!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss(); // 卸載所建立的myDialog物件
                            }else {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error Occured:" + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }

        }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}

