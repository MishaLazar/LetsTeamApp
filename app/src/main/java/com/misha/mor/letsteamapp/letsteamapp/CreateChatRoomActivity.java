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
        MyApp.setLocaleEn(CreateChatRoomActivity.this);
        setContentView(R.layout.activity_create_chat_room);
        fdb = FireBaseDAL.getFireBaseDALInstance();

       /* Intent intent = getIntent();
        userID = intent.getStringExtra("userID");*/
        userID="-KQqc02cWiHQWkaktOUp";
        initViews();
    }

    private void initViews(){

       /* room_Create = (Button)findViewById(R.id.room_Create);
        if(room_Create != null){
            room_Create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!room_DisplayName.getText().toString().equals(R.string.room_DisplayName)) {
                        String displayName = room_DisplayName.getText().toString();
                        String Tag1 = room_Tag1.getText().toString();
                        boolean isActivated = room_isActive.isActivated();
                        Event event = new Event(displayName,Tag1);
                        event.setEvent_isActive(isActivated);
                        //TODO need to make its general and not hard coded
                        event.setEvent_Owner(userID);
                        //fdb.registerEvent(CreateChatRoomActivity.this, event);

                    }
                }
            });
        }*/
        /*room_Reset= (Button)findViewById(R.id.room_reset);
        if(room_Reset != null){
            room_Reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    room_DisplayName.setText(R.string.room_DisplayName);
                    room_Tag1.setText(R.string.room_Tag1);
                    room_isActive.setChecked(false);
                }
            });
        }
        room_DisplayName = (TextView) findViewById(R.id.room_displayName);
        room_Tag1 = (TextView) findViewById(R.id.room_Tag1);
        room_isActive = (Switch) findViewById(R.id.room_IsActive);

*/
    }
}
