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
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //interface
    private Button b;
    private TextView t;

    //GPS
    private LocationManager locationManager;
    private LocationListener listener_GPS;
    public static String locus;
    //time settings
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

//    MQTT mqtt = new MQTT(getApplicationContext());
    private String clientId;
    private MqttAndroidClient client;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        // ******************* MQTT ***********************************
        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://turing.lcd.ufla.br:6064", clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("MMHdDK8sUsVWe7zBEahZwX5E");
        options.setPassword("rfvnWGHbQNFF7M92NjQmxz99".toCharArray());

        try {
            IMqttToken token = client.connect(options);
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

        // ******************* MQTT ***********************************

        setContentView(R.layout.activity_main);
        t = (TextView) findViewById(R.id.textView);
        b = (Button) findViewById(R.id.button);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener_GPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String Horario = sdf.format(new Date());
                String Latitude = Double.toString(location.getLatitude());
                String Longitude = Double.toString(location.getLongitude());
                String Velocidade = Integer.toString((int) (location.getSpeed() * 3600) / 1000);

                locus = ("M " + Horario + " " + Latitude + " " + Longitude + " " + Velocidade);

                //if(location.getAccuracy()<90) {
                try {
                    SentUPD(locus);
                    locus = locus + "km/h\nEnviado!\n";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //}
                //t.append("\n" + locus);
                t.setText(locus.replace(" ", "\n") + "\nErro: " + location.getAccuracy());

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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener_GPS);
                Toast t = Toast.makeText(getApplicationContext(),"NADA",Toast.LENGTH_LONG);
                t.show();
                Log.e("LOCAL", "posicao: " + locus);

                try {
                    SentUPD("oi oi oi");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void SentUPD(String payload) throws IOException {
        Log.d("SNET UDP", "SET");
//        mqtt.publishMessage("hello porra");
        String topic = "foo/bar";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
}

