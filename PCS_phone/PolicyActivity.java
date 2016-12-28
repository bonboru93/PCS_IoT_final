package com.example.a506r05922142.pcs_sms_phone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class PolicyActivity extends AppCompatActivity {

    private static String cooler_low = "20";
    private static String cooler_high = "25";
    private static String heater_low = "10";
    private static String heater_high = "18";
    private static String humidifier_low = "40";
    private static String humidifier_high = "60";
    private static String dehumidifier_low = "70";
    private static String dehumidifier_high = "80";
    private static String aircleaner_low = "10";
    private static String aircleaner_high = "50";
    private static String light_low = "50";
    private static String light_high = "200";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        ((EditText)this.findViewById(R.id.cooler_low)).setText(cooler_low);
        ((EditText)this.findViewById(R.id.cooler_high)).setText(cooler_high);
        ((EditText)this.findViewById(R.id.heater_low)).setText(heater_low);
        ((EditText)this.findViewById(R.id.heater_high)).setText(heater_high);
        ((EditText)this.findViewById(R.id.humidifier_low)).setText(humidifier_low);
        ((EditText)this.findViewById(R.id.humidifier_high)).setText(humidifier_high);
        ((EditText)this.findViewById(R.id.dehumidifier_low)).setText(dehumidifier_low);
        ((EditText)this.findViewById(R.id.dehumidifier_high)).setText(dehumidifier_high);
        ((EditText)this.findViewById(R.id.aircleaner_low)).setText(aircleaner_low);
        ((EditText)this.findViewById(R.id.aircleaner_high)).setText(aircleaner_high);
        ((EditText)this.findViewById(R.id.light_low)).setText(light_low);
        ((EditText)this.findViewById(R.id.light_high)).setText(light_high);

    }

    public void sendPolicy(View view) {

        cooler_low = ((EditText)this.findViewById(R.id.cooler_low)).getText().toString();
        cooler_high = ((EditText)this.findViewById(R.id.cooler_high)).getText().toString();
        heater_low = ((EditText)this.findViewById(R.id.heater_low)).getText().toString();
        heater_high = ((EditText)this.findViewById(R.id.heater_high)).getText().toString();
        humidifier_low = ((EditText)this.findViewById(R.id.humidifier_low)).getText().toString();
        humidifier_high = ((EditText)this.findViewById(R.id.humidifier_high)).getText().toString();
        dehumidifier_low = ((EditText)this.findViewById(R.id.dehumidifier_low)).getText().toString();
        dehumidifier_high = ((EditText)this.findViewById(R.id.dehumidifier_high)).getText().toString();
        aircleaner_low = ((EditText)this.findViewById(R.id.aircleaner_low)).getText().toString();
        aircleaner_high = ((EditText)this.findViewById(R.id.aircleaner_high)).getText().toString();
        light_low = ((EditText)this.findViewById(R.id.light_low)).getText().toString();
        light_high = ((EditText)this.findViewById(R.id.light_high)).getText().toString();


        if (view.getId() == R.id.set_via_sms) {

            SmsManager smsManager = SmsManager.getDefault();
            String msgText = "";

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("c_l", cooler_low);
                jsonObject.put("c_h", cooler_high);
                msgText = "p" + jsonObject.toString().replace("{", "(").replace("}", ")");
                smsManager.sendTextMessage(PhoneActivity.HomeNumber, null, msgText, null, null);

                jsonObject = new JSONObject();
                jsonObject.put("h_l", heater_low);
                jsonObject.put("h_h", heater_high);
                msgText = "p" + jsonObject.toString().replace("{", "(").replace("}", ")");
                smsManager.sendTextMessage(PhoneActivity.HomeNumber, null, msgText, null, null);

                jsonObject = new JSONObject();
                jsonObject.put("hu_l", humidifier_low);
                jsonObject.put("hu_h", humidifier_high);
                msgText = "p" + jsonObject.toString().replace("{", "(").replace("}", ")");
                smsManager.sendTextMessage(PhoneActivity.HomeNumber, null, msgText, null, null);

                jsonObject = new JSONObject();
                jsonObject.put("dh_l", dehumidifier_low);
                jsonObject.put("dh_h", dehumidifier_high);
                msgText = "p" + jsonObject.toString().replace("{", "(").replace("}", ")");
                smsManager.sendTextMessage(PhoneActivity.HomeNumber, null, msgText, null, null);

                jsonObject = new JSONObject();
                jsonObject.put("ac_l", aircleaner_low);
                jsonObject.put("ac_h", aircleaner_high);
                msgText = "p" + jsonObject.toString().replace("{", "(").replace("}", ")");
                smsManager.sendTextMessage(PhoneActivity.HomeNumber, null, msgText, null, null);

                jsonObject = new JSONObject();
                jsonObject.put("l_l", light_low);
                jsonObject.put("l_h", light_high);
                msgText = "p" + jsonObject.toString().replace("{", "(").replace("}", ")");
                smsManager.sendTextMessage(PhoneActivity.HomeNumber, null, msgText, null, null);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else

        if (view.getId() == R.id.set_via_aws) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("c_l", cooler_low);
                jsonObject.put("c_h", cooler_high);
                jsonObject.put("h_l", heater_low);
                jsonObject.put("h_h", heater_high);
                jsonObject.put("hu_l", humidifier_low);
                jsonObject.put("hu_h", humidifier_high);
                jsonObject.put("dh_l", dehumidifier_low);
                jsonObject.put("dh_h", dehumidifier_high);
                jsonObject.put("ac_l", aircleaner_low);
                jsonObject.put("ac_h", aircleaner_high);
                jsonObject.put("l_l", light_low);
                jsonObject.put("l_h", light_high);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent("PCS_AWS_pub");
            intent.putExtra("type", "Policy");
            intent.putExtra("content", jsonObject.toString());
            sendBroadcast(intent);
        }

        Toast.makeText(this, "policy sent", Toast.LENGTH_SHORT).show();

        this.finish();
    }
}
