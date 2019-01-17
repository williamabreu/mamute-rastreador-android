package br.ufla.lcd.mamuterastreador;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/* @link https://developer.android.com/reference/android/location/Location */

public class GPS extends AppCompatActivity {
    private final SimpleDateFormat dateFormat;
    private final LocationManager locationManager;
    private final LocationListener listenerGPS;

    private String time;
    private String date;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private float curse;

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public float getCurse() {
        return curse;
    }

    public GPS() {
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
    }
}
