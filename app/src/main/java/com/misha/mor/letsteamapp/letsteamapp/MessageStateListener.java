package com.misha.mor.letsteamapp.letsteamapp;

import com.firebase.client.DataSnapshot;


/**
 * Created by Misha on 9/19/2016.
 */
public interface MessageStateListener {

    void registerMessageListener(String roomID);

    void notifyMessageListener(DataSnapshot snapshot);

    void notifyQueryMessageListener(DataSnapshot snapshot);


}
