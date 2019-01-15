package br.ufla.lcd.mamuterastreador;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class TrackerService extends IntentService {
    private MQTT broker;
    private GPS gps;

    public TrackerService() {
        super("nome");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
    /*
    {
        "time": "17:22:47",
        "date": "18/06/18",
        "latitude": -21.889733333333332,
        "longitude": -44.978406666666665,
        "curse": 270.0,
        "altitude": 909.1,
        "speed": "220.0",
        "rpm": "1720.5"
    }
    */
}
