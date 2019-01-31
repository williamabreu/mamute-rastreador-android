package br.ufla.lcd.mamuterastreador;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

/* https://www.hivemq.com/blog/mqtt-client-library-enyclopedia-paho-android-service */

public class MQTT {
    private final String clientId;
    private final MqttAndroidClient mqttClient;
    private final MqttConnectOptions options;

    public MQTT(Context context) {
        clientId = MqttClient.generateClientId();
        options = new MqttConnectOptions();
        mqttClient = new MqttAndroidClient(context, "tcp://turing.lcd.ufla.br:6064", clientId);
        options.setUserName("MMHdDK8sUsVWe7zBEahZwX5E");
        options.setPassword("rfvnWGHbQNFF7M92NjQmxz99".toCharArray());
    }

    public void connect() throws MqttException {
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
    }

    public void publish(String topic, String data) throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload;
        encodedPayload = data.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        mqttClient.publish(topic, message);
    }
}

