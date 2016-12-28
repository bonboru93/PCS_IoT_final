package com.example.a506r05922142.pcs_sms_phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

/**
 * Created by 506R05922142$ on 2016/12/16.
 */

public class WeirdSMS extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Object [] pdus= (Object[]) intent.getExtras().get("pdus");
        for(Object pdu:pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);

            String address = smsMessage.getDisplayOriginatingAddress();
            if (!address.equals(PhoneActivity.HomeNumber)) return;

            String body = smsMessage.getMessageBody();
            if (body.charAt(0) == 's')
                PhoneActivity.phoneActivity.receiveSonsor(body.substring(1));
            else if (body.charAt(0) == 'd')
                PhoneActivity.phoneActivity.receiveDevice(body.substring(1));
        }
    }
}