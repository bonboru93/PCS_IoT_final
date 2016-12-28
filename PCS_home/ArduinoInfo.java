package com.example.bonboru93.pcs_sms_home;

import android.text.format.Time;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by 506R05922142$ on 2016/12/13.
 */

public class ArduinoInfo implements Runnable{

    public static double temperature;
    public static double humidity;
    public static double dust;
    public static int brightness;
    public static int door;

    public static int cooler = 0;
    public static int heater = 0;
    public static int humidifier = 0;
    public static int dehumidifier = 0;
    public static int light = 0;
    public static int aircleaner = 0;

    public static int c_l = 0;
    public static int c_h = 0;
    public static int h_l = 0;
    public static int h_h = 0;
    public static int hu_l = 0;
    public static int hu_h = 0;
    public static int dh_l = 0;
    public static int dh_h = 0;
    public static int ac_l = 0;
    public static int ac_h = 0;
    public static int l_l = 0;
    public static int l_h = 0;

    @Override
    public void run() {

        DatagramSocket socket = null;
        DatagramPacket packet;
        byte[] buf;

        while (true) {
            buf = new byte[128];
            packet = new DatagramPacket(buf, buf.length);
            try {
                if (socket == null)
                    socket = new DatagramSocket(22233);
                socket.receive(packet);
            } catch (SocketException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(new String(packet.getData()));
                if (jsonObject.getString("type").equals("Sensor")){
                    ArduinoInfo.temperature = jsonObject.getDouble("temperature");
                    ArduinoInfo.humidity = jsonObject.getDouble("humidity");
                    ArduinoInfo.dust = jsonObject.getDouble("dust");
                    ArduinoInfo.brightness = jsonObject.getInt("brightness");
                    ArduinoInfo.door = jsonObject.getInt("door");
                    HomeActivity.homeActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textField = (TextView)HomeActivity.homeActivity.findViewById(R.id.textView5);
                            textField.setText("");
                            textField.append("T:   " + ArduinoInfo.temperature + " °C\n");
                            textField.append("H:   " + ArduinoInfo.humidity + " %\n");
                            textField.append("P:   " + ArduinoInfo.dust +  " ug/m3\n");
                            textField.append("B:   " +  ArduinoInfo.brightness +  " Lux\n");
                            if (Integer.toString(ArduinoInfo.door).equals("0"))
                                textField.append("D:  closed");
                            else
                                textField.append("D:  open");
                            Time t = new Time();
                            t.setToNow();
                            ((TextView)HomeActivity.homeActivity.findViewById(R.id.textView3)).setText("DB Timestamp: " + Integer.toString(t.hour)+":"+Integer.toString(t.minute)+":"+Integer.toString(t.second));
                            Toast.makeText(HomeActivity.homeActivity.getApplicationContext(), "Sensor Info received", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if (jsonObject.getString("type").equals("Device")){
                    ArduinoInfo.cooler = jsonObject.getInt("cooler");
                    ArduinoInfo.heater = jsonObject.getInt("heater");
                    ArduinoInfo.humidifier = jsonObject.getInt("humidifier");
                    ArduinoInfo.dehumidifier = jsonObject.getInt("dehumidifier");
                    ArduinoInfo.light = jsonObject.getInt("light");
                    ArduinoInfo.aircleaner = jsonObject.getInt("aircleaner");
                    HomeActivity.homeActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textField = (TextView)HomeActivity.homeActivity.findViewById(R.id.textView6);
                            textField.setText("");
                            if (ArduinoInfo.cooler == 1)
                                textField.append("Cooler:   ON\n");
                            else
                                textField.append("Cooler:   OFF\n");
                            if (ArduinoInfo.heater == 1)
                                textField.append("Heater:   ON\n");
                            else
                                textField.append("Heater:   OFF\n");
                            if (ArduinoInfo.humidifier == 1)
                                textField.append("Humidi:   ON\n");
                            else
                                textField.append("Humidi:   OFF\n");
                            if (ArduinoInfo.dehumidifier == 1)
                                textField.append("Dehumi:   ON\n");
                            else
                                textField.append("Dehumi:   OFF\n");
                            if (ArduinoInfo.aircleaner == 1)
                                textField.append("Aircleaner:   ON\n");
                            else
                                textField.append("Aircleaner:   OFF\n");
                            if (ArduinoInfo.light == 1)
                                textField.append("Light:   ON\n");
                            else
                                textField.append("Light:   OFF\n");

                            Time t = new Time();
                            t.setToNow();
                            ((TextView)HomeActivity.homeActivity.findViewById(R.id.textView3)).setText("DB Timestamp: " + Integer.toString(t.hour)+":"+Integer.toString(t.minute)+":"+Integer.toString(t.second));
                            Toast.makeText(HomeActivity.homeActivity.getApplicationContext(), "Arduino Info received", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
