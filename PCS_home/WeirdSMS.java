package com.example.bonboru93.pcs_sms_home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import org.json.JSONException;
import org.json.JSONObject;

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
            if (!ValidRemote.isValidRemote(address)) return;

            String body = smsMessage.getMessageBody();

            if (body.charAt(0) == 'r') {
                HomeActivity.homeActivity.receiveReport(address);
                return;
            }

            body = body.replace("(", "{").replace(")", "}");
            try {
                if (body.charAt(0) == 't')
                    body = (new JSONObject(body.substring(1)).put("type", "Trans")).toString();
                else if (body.charAt(0) == 'p')
                    body = (new JSONObject(body.substring(1)).put("type", "Policy")).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HomeActivity.homeActivity.receiveTrans(body);
        }
    }
}
