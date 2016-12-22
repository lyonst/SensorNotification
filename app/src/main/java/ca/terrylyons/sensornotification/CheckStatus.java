package ca.terrylyons.sensornotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * Created by Terry2 on 24/03/2016.
 */
public class CheckStatus {
    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "ca.terrylyons.sensornotification.displayupdate";

    public boolean hasStatusChanged(Context context, SensorStatus status)
    {
        Log.d("CheckStatus.hasChanged", String.format("Checking status for %d", status.Id) );
        SensorStatus previousStatus = getCurrentValues(context, status.Id);
        boolean changed = false;

        if (previousStatus.State != status.State || previousStatus.TimeStamp.before(status.TimeStamp)) {
            updateStatus(context, status);
            doNotification(context, status);
            doBroadcastUpdate(context, status);
        }

        Log.d("CheckStatus.hasChanged", String.format("Done checking status for %d", status.Id) );
        return changed;
    }

    private SensorStatus getCurrentValues(Context context, int id)
    {
        SensorPersistence persistence = new SensorPersistence();
        return persistence.getStatus(context, id);
    }

    private void updateStatus(Context context, SensorStatus status)
    {
        SensorPersistence persistence = new SensorPersistence();
        persistence.setStatus(context, status);
    }

    private void doNotification(Context context, SensorStatus status) {
        Log.d("CheckStatus.doNotify", String.format("Doing notification for %d", status.Id) );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(status.Id == 0 ? R.drawable.washing_in_cold_water : R.drawable.dry_normal);
        builder.setContentTitle(context.getString(R.string.sensorNotification));
        builder.setContentText(createStatusString(context, status));
        builder.setVibrate(new long[] {500, 500});

        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(status.Id, builder.build());

        Log.d("CheckStatus.doNotify", String.format("Done notification for %d", status.Id));
    }

    private void doBroadcastUpdate(Context context, SensorStatus status) {
        Log.d("CheckStatus.broadcast", String.format("Broadcasting update for %d", status.Id) );

        Intent updateIntent = new Intent(BROADCAST_ACTION);
        updateIntent.putExtra("id", status.Id);
        updateIntent.putExtra("state", status.State);

        context.sendBroadcast(updateIntent);

        Log.d("CheckStatus.broadcast", String.format("Done broadcasting update for %d", status.Id));
    }

    private String createStatusString(Context context, SensorStatus status)
    {
        String statusString = context.getString(status.Id == 0 ? R.string.washer : R.string.dryer);
        statusString += " ";
        statusString += status.State ? "Running" : "Done";

        return statusString;
    }
}
