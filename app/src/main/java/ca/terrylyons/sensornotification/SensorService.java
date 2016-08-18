package ca.terrylyons.sensornotification;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class SensorService extends Service {
    private final Handler _handler = new Handler();

    public SensorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean stop = false;

        if (intent != null) {
            intent.getBooleanExtra("stop", false);
        }

        if (stop)
        {
            Log.d("onStartCommand", "Stopping");
            stopSelf();
            return START_NOT_STICKY;
        }

        _handler.removeCallbacks(runnableCode);
        _handler.post(runnableCode);
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
        }

        super.onDestroy();
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String url = settings.getString("server_url", "");
            int frequency = Integer.parseInt(settings.getString("sync_frequency", "30"));

            if (isWiFiAvailable()) {
                WebClient client = new WebClient(getApplicationContext(), url);
                client.SetTime();
                client.CheckStatus(0);
                client.CheckStatus(1);
            }

            if (frequency != -1) {
                _handler.postDelayed(runnableCode, frequency * 60000);
            }
        }
    };

    private boolean isWiFiAvailable()
    {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String ssid = settings.getString("wifissid", "");

        String ssid2 = connectionInfo.getSSID();
        if (ssid2.startsWith("\""))
        {
            ssid2 = ssid2.substring(1);
        }
        if (ssid2.endsWith("\"")) {
            ssid2 = ssid2.substring(0, ssid2.length() - 1);
        }

        return ssid.equals(ssid2);
    }

}
