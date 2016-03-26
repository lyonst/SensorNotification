package ca.terrylyons.sensornotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
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
            executeService(context, true);
            return;
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String ssid = settings.getString("wifissid", "");

        String ssid2 = connectionInfo.getSSID();
        if (ssid2.startsWith("\""))
        {
            ssid2 = ssid2.substring(1);
        }
        if (ssid2.endsWith("\"")) {
            ssid2 = ssid2.substring(0, ssid2.length() - 1);
        }
        executeService(context, !ssid.equals(ssid2));
    }

    private void executeService(Context context, boolean stop)
    {
        Intent service = new Intent(context, SensorService.class);
        service.putExtra("stop", stop);
        context.startService(service);
    }
}
