package ca.terrylyons.sensornotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

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
    public boolean hasStatusChanged(Context context, SensorStatus status)
    {
        SensorStatus previousStatus = getCurrentValues(context, status.Id);
        boolean changed = false;

        if (previousStatus.State != status.State) {
            changed = true;

            if (previousStatus.State == 1)
            {
                status.State = 3;
            }
        }
        else {
            if (previousStatus.State == 0 && previousStatus.TimeStamp.before(status.TimeStamp)) {
                status.State = 3;
                changed = true;
            }
        }

        if (changed || previousStatus.TimeStamp.before(status.TimeStamp)) {
            updateStatus(context, status);
        }

        if (changed)
        {
            doNotification(context, status);
        }
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
    }

    private String createStatusString(Context context, SensorStatus status)
    {
        String statusString = context.getString(status.Id == 0 ? R.string.washer : R.string.dryer);
        statusString += " ";

        switch (status.State)
        {
            case 0:
                statusString += context.getString(R.string.notRunning);
                break;
            case 1:
                statusString += context.getString(R.string.running);
                break;
            case 2:
                statusString += context.getString(R.string.done);
                break;
        }

        return statusString;
    }
}
