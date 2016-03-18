package ca.terrylyons.sensornotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
    public BootBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
        {
            return;
        }

        Intent service = new Intent(context, SensorService.class);
        context.startService(service);
    }
}
