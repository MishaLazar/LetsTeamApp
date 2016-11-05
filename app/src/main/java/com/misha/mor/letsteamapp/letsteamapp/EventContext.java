package com.misha.mor.letsteamapp.letsteamapp;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class EventContext extends AppCompatActivity {

    TextView editTextContext;
    TextView editTextName;
    TextView editTextLocation;

    ImageButton btn_openEventChat;
    ImageButton btn_showEeventLocation;
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
        editTextContext = (TextView)findViewById(R.id.etxtContext);
        editTextContext.setText("this is a demo data \n this is demo data \n this is demo data", TextView.BufferType.EDITABLE);

        editTextName = (TextView)findViewById(R.id.etxtCreatorName);
        editTextName.setText("Mor and Misha", TextView.BufferType.EDITABLE);

        editTextLocation = (TextView)findViewById(R.id.etxtLocation);
        editTextLocation.setText("ספיר 7, הרצליה", TextView.BufferType.EDITABLE);


        btn_openEventChat = (ImageButton)findViewById(R.id.btnOpenChat);
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

        btn_showEeventLocation = (ImageButton)findViewById(R.id.btnShowEventLocation);
        if(btn_showEeventLocation != null){

            btn_showEeventLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: get real location to put in intent
                    String strEventLocation = editTextLocation.getText().toString();
                    Intent intent = new Intent(EventContext.this, MapsActivity.class);
                    intent.putExtra("eventLocation",strEventLocation);
                    startActivity(intent);

                }
            });

        }




    }
}