package com.example.a506r05922142.pcs_sms_phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by 506R05922142$ on 2016/12/18.
 */

public class PCSsub extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getExtras().getString("type");
        String content = intent.getExtras().getString("content");
        if (type.equals("Sensor"))
            PhoneActivity.phoneActivity.receiveSonsor(content);
        else if (type.equals("Device"))
            PhoneActivity.phoneActivity.receiveDevice(content);
    }
}