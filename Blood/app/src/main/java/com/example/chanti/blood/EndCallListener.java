package com.example.chanti.blood;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by chanti on 29-Mar-16.
 */
public class EndCallListener extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if(TelephonyManager.CALL_STATE_RINGING == state) {
            Log.d( "RINGING, number: ", incomingNumber);
        }
        if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
        }
        if(TelephonyManager.CALL_STATE_IDLE == state) {
            //when this state occurs, and your flag is set, restart your app
        }
    }
}
