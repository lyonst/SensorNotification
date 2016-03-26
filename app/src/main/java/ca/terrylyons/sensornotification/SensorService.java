package ca.terrylyons.sensornotification;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class SensorService extends Service {
    private Handler _handler;

    public SensorService() {
        _handler = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean stop = intent.getBooleanExtra("stop", false);

        if (stop)
        {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (_handler == null) {
            _handler = new Handler();
            _handler.post(runnableCode);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (_handler != null) {
            _handler.removeCallbacks(runnableCode);
            _handler = null;
        }

        super.onDestroy();
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String url = settings.getString("server_url", "");
            int frequency = Integer.parseInt(settings.getString("sync_frequency", "30"));

            WebClient client = new WebClient(getApplicationContext(), url);
            client.CheckStatus(0);
            client.CheckStatus(1);

            if (frequency != -1) {
                _handler.postDelayed(runnableCode, frequency * 60000);
            }
        }
    };
}
