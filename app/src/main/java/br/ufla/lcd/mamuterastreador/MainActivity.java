package br.ufla.lcd.mamuterastreador;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // Interface
    private Button b;
    private TextView t;

    // GPS
    private SimpleDateFormat dateFormat;
    private LocationManager locationManager;
    private LocationListener listenerGPS;

    // MQTT
    private String clientId;
    private MqttAndroidClient mqttClient;
    private MqttConnectOptions options;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MQTT
        clientId = MqttClient.generateClientId();
        options = new MqttConnectOptions();
        mqttClient = new MqttAndroidClient(getApplicationContext(), "tcp://turing.lcd.ufla.br:6064", clientId);
        options.setUserName("MMHdDK8sUsVWe7zBEahZwX5E");
        options.setPassword("rfvnWGHbQNFF7M92NjQmxz99".toCharArray());
        try {
            IMqttToken token = mqttClient.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("TAG", "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("TAG", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        // GPS
        dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yy");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String[] datetime = dateFormat.format(new Date()).split(" ");
                String time = datetime[0];
                String date = datetime[1];
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double altitude = location.getAltitude();
                double speed = location.getSpeed() * 3.6;
                float curse = location.getBearing();
                String data;
                try {
                    JSONObject json = new JSONObject();
                    json.put("time", time);
                    json.put("date", date);
                    json.put("latitude", latitude);
                    json.put("longitude", longitude);
                    json.put("curse", curse);
                    json.put("altitude", altitude);
                    json.put("speed", speed);
                    data = json.toString().replace("\\", "");
                }
                catch (JSONException e) {
                    data = "";
                }

                try {
                    byte[] encodedPayload;
                    encodedPayload = data.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    mqttClient.publish("ERT-6789", message);
                } catch (MqttException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                t.setText(data);
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

        setContentView(R.layout.activity_main);
        t = (TextView) findViewById(R.id.textView);
        b = (Button) findViewById(R.id.button);
        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.INTERNET}, 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listenerGPS);
                Toast t = Toast.makeText(getApplicationContext(),"OK",Toast.LENGTH_LONG);
                t.show();
            }
        });
    }
}

