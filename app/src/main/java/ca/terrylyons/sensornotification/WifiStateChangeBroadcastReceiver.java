package ca.terrylyons.sensornotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WifiStateChangeBroadcastReceiver extends BroadcastReceiver {
    public WifiStateChangeBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if(!intent.getAction().equalsIgnoreCase(Intent.ACTION_AIRPLANE_MODE_CHANGED))
        {
            return;
        }*/

        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() != ConnectivityManager.TYPE_WIFI)
        {
            return;
        }

            //Log.d("WifiReceiver", "Have Wifi Connection");
        //else
            //Log.d("WifiReceiver", "Don't have Wifi Connection");

        Intent service = new Intent(context, SensorService.class);
        context.startService(service);
    }
}
