package ca.terrylyons.sensornotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

public class WifiStateChangeBroadcastReceiver extends BroadcastReceiver {
    public WifiStateChangeBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo == null || netInfo.getType() != ConnectivityManager.TYPE_WIFI)
        {
            return;
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String ssid = settings.getString("server_url", "");

        if (connectionInfo.getSSID() == ssid) {
            Intent service = new Intent(context, SensorService.class);
            context.startService(service);
        }
    }
}
