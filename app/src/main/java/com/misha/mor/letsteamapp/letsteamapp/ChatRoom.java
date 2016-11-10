package com.misha.mor.letsteamapp.letsteamapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;


public class ChatRoom extends Activity {

    //class instances
    ChatArrayAdapter chatArrayAdapter;
    FireBaseDAL fdb;
    InnerReceiver innerReceiver;

    //views
    ListView listView;
    EditText chatText;
    Button btn_Send;

    //var
    boolean isReached = false;
    String eventID;
    String myUserID;
    Intent receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MyApp.setLocaleEn(ChatRoom.this);
        setContentView(R.layout.activity_chat_room);

        //register innerReceiver for Broadcast
        innerReceiver = new InnerReceiver(ChatRoom.this);
        receiver = registerReceiver(innerReceiver, new IntentFilter(getString(R.string.BROADCAST_ACTION_POLL)));


        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");
        myUserID = intent.getStringExtra("userID");

        //get\create singleton db reference
        fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setContext(ChatRoom.this);

        //initialize all views
        initViews();

        intAdapter();

        registerForMessage(eventID);


    }
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(innerReceiver, new IntentFilter(getString(R.string.BROADCAST_ACTION_POLL)));
        registerForMessage(eventID);

    }
    @Override
    public void onPause() {
        super.onPause();
        try{
            unregisterReceiver(innerReceiver);
        }catch (RuntimeException exc){

            Log.e("onPause","unregisterReceiver(innerReceiver) " +exc.getMessage());
        }
        unregisterForMessage(eventID);


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            unregisterReceiver(innerReceiver);
        }catch (RuntimeException exc){

            Log.e("onBack","unregisterReceiver(innerReceiver)");
        }

        unregisterForMessage(eventID);


    }
    public void intAdapter(){

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.list_item_chat_message,myUserID);

        listView.setAdapter(chatArrayAdapter);

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        listView.setAdapter(chatArrayAdapter);

        //auto scroll on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

    }
    public void registerForMessage(String roomID){

        fdb.registerMessageListener(roomID);
    }
    public void unregisterForMessage(String roomID){

        fdb.unregisterMessageListener(roomID);
    }

    public void initViews(){

        //list view for messages
        listView = (ListView)findViewById(R.id.room_msg_view);

        //send message button
        btn_Send = (Button)findViewById(R.id.btnSend);

        try{
            btn_Send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendChatMessage();
                }
            });

        }catch (NullPointerException exc){

            String str  = exc.getMessage();
            Log.e(str,null);

        }

        chatText = (EditText)findViewById(R.id.msg);
        try{
            chatText.addTextChangedListener(new TextWatcher(){

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void afterTextChanged(Editable s) {

                    // if chatText has n chars & this is not called yet, add new line
                    if(chatText.getText().length() == R.integer.MAX_NUMBER_CHARS_MESSAGE && !isReached) {

                        chatText.append("\n");

                        isReached = true;
                    }

                    // if chatText has less than n chars & boolean has changed, reset
                    if(chatText.getText().length() < R.integer.MAX_NUMBER_CHARS_MESSAGE && isReached){

                        isReached = false;
                    }
                }
            });
        }catch (NullPointerException exc){
            String str  = exc.getMessage();
            Log.e(str,null);
        }

        try{

            chatText.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                        return sendChatMessage();

                    }

                    return false;

                }

            });
        }catch (NullPointerException exc){

            String str  = exc.getMessage();

            Log.e(str,null);
        }

    }
    private boolean sendChatMessage() {

        ChatMessage message = new ChatMessage(chatText.getText().toString(), eventID,myUserID);

        fdb.sendMessage(message);

        //clear the text view
        chatText.setText("");

        return true;
    }
    class InnerReceiver extends BroadcastReceiver {

        Context context;

        public InnerReceiver() {

        }

        public InnerReceiver(Context context) {
            this.context = context;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            getMessages();

            Log.e("innerReceiver", "MyReceiver: broadcast received");

        }
    }
    private void getMessages(){

        ArrayList<ChatMessage> messages = new ArrayList<>(fdb.getMessagesHashMap().values());

        for (ChatMessage message:messages) {

            chatArrayAdapter.add(message);

        }

        fdb.clearMessageMap();
    }

}