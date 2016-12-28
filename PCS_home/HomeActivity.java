package com.example.bonboru93.pcs_sms_home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.mcnlab.lib.smscommunicate.CommandHandler;
import org.mcnlab.lib.smscommunicate.Recorder;
import org.mcnlab.lib.smscommunicate.UserDefined;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.example.bonboru93.pcs_sms_home.ArduinoInfo.temperature;

public class HomeActivity extends AppCompatActivity {

    public static HomeActivity homeActivity;

    public static String ArduinoAddress = "192.168.1.4";
    public static int ArduinoPort = 33340;
    public static LocationManager locationManager;
    public static Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeActivity = this;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        location = new Location(LocationManager.GPS_PROVIDER);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                HomeActivity.location = location;
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {
            }
        });

        new Thread(new ArduinoInfo()).start();

    }


    public void addNewUser(View view) {
        String newUser = ((EditText)this.findViewById(R.id.editText)).getText().toString();
        ValidRemote.addValidRemote(newUser);
        ((TextView)this.findViewById(R.id.textView2)).append(newUser + "\n");
    }


    public void setArduinoAddress(View view) {
        final View arduinoAddress = this.getLayoutInflater().inflate(R.layout.arduino_address, null);
        new AlertDialog.Builder(this)
                .setTitle("Set Arduino Address")
                .setView(arduinoAddress)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArduinoAddress = ((EditText)arduinoAddress.findViewById(R.id.editText11)).getText().toString();
                        ArduinoPort = Integer.parseInt(((EditText)arduinoAddress.findViewById(R.id.editText12)).getText().toString());
                        Toast.makeText(HomeActivity.homeActivity.getApplicationContext(), "Arduino address updated", Toast.LENGTH_SHORT).show();
                    }
        }).show();
    }

    public void receiveTrans(final String body) {

        new Thread(new Runnable() {

            DatagramSocket socket = null;
            DatagramPacket packet;
            byte[] buf;

            @Override
            public void run() {
                try {
                    buf = body.getBytes();
                    packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(ArduinoAddress), ArduinoPort);
                    if (socket == null)
                        socket = new DatagramSocket();
                    socket.send(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Toast.makeText(this, "Trans deliverd to Arduino", Toast.LENGTH_SHORT).show();
    }

    public void receiveReport(String address)
    {

        SmsManager smsManager = SmsManager.getDefault();
        String msgText = "";

        msgText = "s(\"temperature\":" + Double.toString(temperature) + ")";
        smsManager.sendTextMessage(address, null, msgText, null, null);

        msgText = "s(\"humidity\":" + Double.toString(ArduinoInfo.humidity) + ")";
        smsManager.sendTextMessage(address, null, msgText, null, null);

        msgText = "s(\"dust\":" + Double.toString(ArduinoInfo.dust) + ")";
        smsManager.sendTextMessage(address, null, msgText, null, null);

        msgText = "s(\"brightness\":" + Double.toString(ArduinoInfo.brightness) + ")";
        smsManager.sendTextMessage(address, null, msgText, null, null);

        msgText = "s(\"door\":" + Double.toString(ArduinoInfo.door) + ")";
        smsManager.sendTextMessage(address, null, msgText, null, null);

        msgText = "s(\"la\":" + String.format("%.3f", location.getLatitude()) + ", \"lo\":" + String.format("%.3f", location.getLongitude()) + ")";
        smsManager.sendTextMessage(address, null, msgText, null, null);

        msgText = "d(\"cooler\":" + Integer.toString(ArduinoInfo.cooler) + ")";
        smsManager.sendTextMessage(address, null, msgText, null, null);

        msgText = "d(\"heater\":" + Integer.toString(ArduinoInfo.heater) + ")";
        smsManager.sendTextMessage(address, null, msgText, null, null);

        msgText = "d(\"humidifier\":" + Integer.toString(ArduinoInfo.humidifier) + ")";
        smsManager.sendTextMessage(address, null, msgText, null, null);

        msgText = "d(\"dehumidifier\":" + Integer.toString(ArduinoInfo.dehumidifier) + ")";
        smsManager.sendTextMessage(address, null, msgText, null, null);

        msgText = "d(\"light\":" + Integer.toString(ArduinoInfo.light) + ")";
        smsManager.sendTextMessage(address, null, msgText, null, null);

        msgText = "d(\"aircleaner\":" + Integer.toString(ArduinoInfo.aircleaner) + ")";
        smsManager.sendTextMessage(address, null, msgText, null, null);

        Toast.makeText(this, "Report sent", Toast.LENGTH_SHORT).show();
    }
}
