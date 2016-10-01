package com.misha.mor.letsteamapp.letsteamapp;

import java.util.HashMap;

/**
 * Created by Misha on 9/23/2016.
 */
public interface ActivityEventStateListener {

    void registerRoomStateListener();
    void unregisterRoomStateListener();

    void getEvents();

    void notifyListener();

}
