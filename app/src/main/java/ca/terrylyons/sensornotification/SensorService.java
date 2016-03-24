package ca.terrylyons.sensornotification;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.preference.PreferenceManager;

public class SensorService extends Service {
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

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String url = settings.getString("server_url", "http://localhost");
        int frequency = Integer.parseInt(settings.getString("sync_frequency", "30"));

        WebClient client = new WebClient(url);
        client.GetStatus(0);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
