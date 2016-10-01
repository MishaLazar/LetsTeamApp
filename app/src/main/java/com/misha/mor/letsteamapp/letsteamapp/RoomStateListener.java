package com.misha.mor.letsteamapp.letsteamapp;

/**
 * Created by Misha on 9/10/2016.
 */
import com.firebase.client.DataSnapshot;

import java.util.HashMap;

public interface RoomStateListener {

    void registerStateListener();

    void EventsNotifyListener(DataSnapshot snapshot);

    void getEventState();

}