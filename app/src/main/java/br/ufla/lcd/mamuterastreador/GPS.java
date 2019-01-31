package br.ufla.lcd.mamuterastreador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* @link https://developer.android.com/reference/android/location/Location */

public class GPS extends AppCompatActivity {
    private final SimpleDateFormat dateFormat;
    private final LocationManager locationManager;
    private final LocationListener listenerGPS;
    private final MQTT mqttBroker;

    private String time;
    private String date;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private float curse;

    @Override
    public String toString() {
        try {
            JSONObject json = new JSONObject();
            json.put("time", time);
            json.put("date", date);
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("curse", curse);
            json.put("altitude", altitude);
            json.put("speed", speed);
            return json.toString();
        }
        catch (JSONException e) {
            return "";
        }
    }

    @SuppressLint("MissingPermission")
    public GPS(MQTT mqtt) {
        mqttBroker = mqtt;
        dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String[] datetime = dateFormat.format(new Date()).split(" ");
                time = datetime[0];
                date = datetime[1];
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                altitude = location.getAltitude();
                speed = location.getSpeed() * 3.6;
                curse = location.getBearing();

                try {
                    mqttBroker.publish("ERT-6789", this.toString());
                }
                catch (MqttException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listenerGPS);
    }
}
