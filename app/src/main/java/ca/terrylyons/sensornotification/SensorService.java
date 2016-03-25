package ca.terrylyons.sensornotification;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.preference.PreferenceManager;

public class SensorService extends Service {
    private Handler _handler;

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_info_black_24dp);
        builder.setContentTitle("test title");
        builder.setContentText("test text");

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());

        _handler = new Handler();
        _handler.post(runnableCode);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (_handler != null) {
            _handler.removeCallbacks(runnableCode);
        }
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String url = settings.getString("server_url", "");
            int frequency = Integer.parseInt(settings.getString("sync_frequency", "30"));

            WebClient client = new WebClient(getApplicationContext(), url);
            client.GetStatus(0);
            client.GetStatus(1);

            _handler.postDelayed(runnableCode, frequency * 60000);
        }
    };
}
