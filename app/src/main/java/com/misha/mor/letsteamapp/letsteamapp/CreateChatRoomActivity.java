package com.misha.mor.letsteamapp.letsteamapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class CreateChatRoomActivity extends AppCompatActivity {

    TextView room_DisplayName;
    TextView room_Tag1;
    Button room_Create;
    Button room_Reset;
    Switch room_isActive;

    //DB
    FireBaseDAL fdb;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_room);
        fdb = FireBaseDAL.getFireBaseDALInstance();

        userID="-KQqc02cWiHQWkaktOUp";
        initViews();
    }

    private void initViews(){
    }
}
