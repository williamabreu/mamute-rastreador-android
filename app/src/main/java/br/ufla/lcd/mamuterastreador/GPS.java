package br.ufla.lcd.mamuterastreador;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.content.ContextCompat.getSystemService;

/* @link https://developer.android.com/reference/android/location/Location */

public class GPS extends AppCompatActivity {
    private final SimpleDateFormat dateFormat;
    private final LocationManager locationManager;
    private final LocationListener listenerGPS;

    private String time;
    private String date;
    private Double latitude;
    private Double longitude;
    private Double curse;
    private Double altitude;
    private Double speed;

    public GPS() {
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                time = dateFormat.format(new Date());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                altitude = location.getAltitude();
                speed = location.getSpeed() * 3.6;
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
