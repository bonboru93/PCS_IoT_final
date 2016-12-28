package com.example.a506r05922142.pcs_sms_phone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.renderscript.Double2;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class PhoneActivity extends AppCompatActivity {

    public static PhoneActivity phoneActivity;
    public static final String HomeNumber = "15555215556";
    public static boolean CmdPath = true;

    public static double temperature;
    public static double humidity;
    public static double dust;
    public static int brightness;
    public static int door;
    public static double la;
    public static double lo;

    public static int cooler;
    public static int heater;
    public static int humidifier;
    public static int dehumidifier;
    public static int light;
    public static int aircleaner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        phoneActivity = this;
    }

    public void sendRequest(View view) {

        String msgText = "r";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(HomeNumber, null, msgText, null, null);

        Toast.makeText(this, "Request sent", Toast.LENGTH_SHORT).show();
    }

    public void sendSwitch(View view) {

        String msgText = "";

        switch (view.getId()) {
            case R.id.cooler_open:
                msgText = "\"cooler\":\"1\"";
                break;
            case R.id.cooler_close:
                msgText = "\"cooler\":\"0\"";
                break;
            case R.id.heater_open:
                msgText = "\"heater\":\"1\"";
                break;
            case R.id.heater_close:
                msgText = "\"heater\":\"0\"";
                break;
            case R.id.humidifier_open:
                msgText = "\"humidifier\":\"1\"";
                break;
            case R.id.humidifier_close:
                msgText = "\"humidifier\":\"0\"";
                break;
            case R.id.dehumidi_open:
                msgText = "\"dehumidifier\":\"1\"";
                break;
            case R.id.dehumidi_close:
                msgText = "\"dehumidifier\":\"0\"";
                break;
            case R.id.light_open:
                msgText = "\"light\":\"1\"";
                break;
            case R.id.light_close:
                msgText = "\"light\":\"0\"";
                break;
            case R.id.aircleaner_open:
                msgText = "\"aircleaner\":\"1\"";
                break;
            case R.id.aircleaner_close:
                msgText = "\"aircleaner\":\"0\"";
                break;
        }

        if (CmdPath) {
            msgText = "t(" + msgText + ")";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(HomeNumber, null, msgText, null, null);
        } else {
            msgText = "{" + msgText + "}";
            Intent intent = new Intent("PCS_AWS_pub");
            intent.putExtra("type", "Trans");
            intent.putExtra("content", msgText);
            sendBroadcast(intent);
        }

        Toast.makeText(this, "Trans sent", Toast.LENGTH_SHORT).show();
    }

    public void receiveSonsor(String body) {

        body = body.replace("(", "{").replace(")", "}");
        JSONObject jsonObject;
        TextView textField = (TextView)this.findViewById(R.id.textView20);

        try {
            jsonObject = new JSONObject(body);
            if (!jsonObject.isNull("temperature")) temperature = jsonObject.getDouble("temperature");
            if (!jsonObject.isNull("humidity")) humidity = jsonObject.getDouble("humidity");
            if (!jsonObject.isNull("dust")) dust = jsonObject.getDouble("dust");
            if (!jsonObject.isNull("brightness")) brightness = jsonObject.getInt("brightness");
            if (!jsonObject.isNull("door")) door = jsonObject.getInt("door");
            if (!jsonObject.isNull("la")) {
                la = jsonObject.getDouble("la");
                lo = jsonObject.getDouble("lo");
                Intent intent = new Intent("PCS_MAP");
                intent.putExtra("la1", la);
                intent.putExtra("la2", lo);
                intent.putExtra("content", textField.getText().toString());
                sendBroadcast(intent);
            }

            textField.setText("");
            textField.append("Temperature:   " + Double.toString(temperature) + " °C\n");
            textField.append("Humidity:   " + Double.toString(humidity)+ " %\n");
            textField.append("PM2.5:   " + Double.toString(dust)+ " ug/m3\n");
            textField.append("Brightness:   " +  Integer.toString(brightness)+ " Lux\n");
            if (door == 0)
                textField.append("Door:  closed\n");
            else
                textField.append("Door:  open\n");
            textField.append("Location:   " +  Double.toString(la) + ", " + Double.toString(lo) + "\n");
            Time t = new Time();
            t.setToNow();
            textField.append("Time: " + Integer.toString(t.hour)+":"+Integer.toString(t.minute)+":"+Integer.toString(t.second));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void receiveDevice(String body){

        body = body.replace("(", "{").replace(")", "}");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(body);
            if (!jsonObject.isNull("cooler")) cooler = jsonObject.getInt("cooler");
            if (!jsonObject.isNull("heater")) heater = jsonObject.getInt("heater");
            if (!jsonObject.isNull("humidifier")) humidifier = jsonObject.getInt("humidifier");
            if (!jsonObject.isNull("dehumidifier")) dehumidifier = jsonObject.getInt("dehumidifier");
            if (!jsonObject.isNull("light")) light = jsonObject.getInt("light");
            if (!jsonObject.isNull("aircleaner")) aircleaner = jsonObject.getInt("aircleaner");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (cooler == 0) {
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.cooler_status)).setText(" OFF");
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.cooler_status)).setTextColor(this.getResources().getColor(android.R.color.holo_red_light));
        }
        else {
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.cooler_status)).setText("  ON");
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.cooler_status)).setTextColor(this.getResources().getColor(android.R.color.holo_green_light));
        }
        if (heater == 0) {
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.heater_status)).setText(" OFF");
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.heater_status)).setTextColor(this.getResources().getColor(android.R.color.holo_red_light));
        }
        else {
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.heater_status)).setText("  ON");
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.heater_status)).setTextColor(this.getResources().getColor(android.R.color.holo_green_light));
        }
        if (humidifier == 0) {
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.humidifier_status)).setText(" OFF");
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.humidifier_status)).setTextColor(this.getResources().getColor(android.R.color.holo_red_light));
        }
        else {
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.humidifier_status)).setText("  ON");
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.humidifier_status)).setTextColor(this.getResources().getColor(android.R.color.holo_green_light));
        }
        if (dehumidifier == 0) {
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.dehumidifier_status)).setText(" OFF");
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.dehumidifier_status)).setTextColor(this.getResources().getColor(android.R.color.holo_red_light));
        }
        else {
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.dehumidifier_status)).setText("  ON");
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.dehumidifier_status)).setTextColor(this.getResources().getColor(android.R.color.holo_green_light));
        }
        if (aircleaner == 0) {
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.aircleaner_status)).setText(" OFF");
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.aircleaner_status)).setTextColor(this.getResources().getColor(android.R.color.holo_red_light));
        }
        else {
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.aircleaner_status)).setText("  ON");
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.aircleaner_status)).setTextColor(this.getResources().getColor(android.R.color.holo_green_light));
        }
        if (light == 0) {
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.light_status)).setText(" OFF");
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.light_status)).setTextColor(this.getResources().getColor(android.R.color.holo_red_light));
        }
        else {
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.light_status)).setText("  ON");
            ((TextView) PhoneActivity.phoneActivity.findViewById(R.id.light_status)).setTextColor(this.getResources().getColor(android.R.color.holo_green_light));
        }
    }

    public void setPolicy(View view) {
        startActivity(new Intent().setClass(this, PolicyActivity.class));
    }

    public void changeCmdPath(View view) {
        CmdPath = !CmdPath;
        if (CmdPath)
            ((Button)this.findViewById(R.id.button15)).setText("SEND VIA SMS");
        else
            ((Button)this.findViewById(R.id.button15)).setText("SEND VIA AWS");
    }
}
