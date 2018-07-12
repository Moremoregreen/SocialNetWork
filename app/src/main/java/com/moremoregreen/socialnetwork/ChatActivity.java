package com.moremoregreen.socialnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;

public class ChatActivity extends AppCompatActivity {
    private Toolbar ChattoolBar;
    private ImageButton SendMessageButton, SendImgagefileButton;
    private EditText userMessageInput;
    private RecyclerView userMessageList;

    private String messageReceiverID, messageReceiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("userName").toString();

        IntializeFields();

    }

    private void IntializeFields() {
        ChattoolBar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(ChattoolBar);

        SendMessageButton = findViewById(R.id.send_message_button);
        SendImgagefileButton = findViewById(R.id.send_image_file_button);
        userMessageInput = findViewById(R.id.input_message);

    }
}
