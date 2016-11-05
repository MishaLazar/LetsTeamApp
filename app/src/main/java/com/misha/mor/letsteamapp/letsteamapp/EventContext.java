package com.misha.mor.letsteamapp.letsteamapp;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EventContext extends AppCompatActivity {

    EditText editTextContext;
    EditText editTextName;
    EditText editTextLocation;

    Button btn_openEventChat;
    Button btn_showEeventLocation;
    String userID;
    String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_context);


        //intent info
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        eventID = intent.getStringExtra("eventID");


        //initialize views
        initViews();
    }

    public void initViews(){
        editTextContext = (EditText)findViewById(R.id.etxtContext);
        editTextContext.setText("this is a demo data \n this is demo data \n this is demo data", TextView.BufferType.EDITABLE);

        editTextName = (EditText)findViewById(R.id.etxtCreatorName);
        editTextName.setText("Mor and Misha", TextView.BufferType.EDITABLE);

        editTextLocation = (EditText)findViewById(R.id.etxtLocation);
        editTextLocation.setText("Afeka", TextView.BufferType.EDITABLE);


        btn_openEventChat = (Button)findViewById(R.id.btnOpenChat);
        if(btn_openEventChat != null){

            btn_openEventChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(EventContext.this, ChatRoom.class);
                    intent.putExtra("eventID",eventID);
                    intent.putExtra("userID",userID);
                    startActivity(intent);
                }
            });

        }

        btn_showEeventLocation = (Button)findViewById(R.id.btnShowEventLocation);
        if(btn_showEeventLocation != null){

            btn_showEeventLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: get real location to put in intent
                    Intent intent = new Intent(EventContext.this, MapsActivity.class);
                    intent.putExtra("eventLocation","");
                    startActivity(intent);

                }
            });

        }




    }
}