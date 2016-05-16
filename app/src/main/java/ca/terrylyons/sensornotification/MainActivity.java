package ca.terrylyons.sensornotification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.activity_main);

        TextView washerStatus = (TextView)findViewById(R.id.washerStatus);
        TextView dryerStatus = (TextView)findViewById(R.id.dryerStatus);

        SensorPersistence persistence = new SensorPersistence();
        setStatus(washerStatus, persistence.getStatus(this, 0).State);
        setStatus(dryerStatus, persistence.getStatus(this, 1).State);

        registerReceiver(broadcastReceiver, new IntentFilter(CheckStatus.BROADCAST_ACTION));

        Intent service = new Intent(this, SensorService.class);
        service.putExtra("stop", false);
        this.startService(service);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_settings, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings)
        {
            Intent intent = new Intent();
            intent.setClass(this, Settings.class);
            startActivityForResult(intent, 0);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setStatus(TextView view, int status)
    {
        switch (status)
        {
            case 0:
                view.setText(R.string.notRunning);
                break;
            case 1:
                view.setText(R.string.running);
                break;
            case 2:
                view.setText(R.string.done);
                break;
        }
    }

    public void onWasherReset(View view) {
        resetSensor(0);
    }

    public void onDryerReset(View view) {
        resetSensor(1);
    }

    private void resetSensor(int id)
    {
        SensorStatus status = new SensorStatus();
        status.Id = id;
        status.State = 0;
        status.TimeStamp = new Date();

        SensorPersistence persistence = new SensorPersistence();
        persistence.setStatus(this, status);

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);

        setStatus((TextView)findViewById(id == 0 ? R.id.washerStatus : R.id.dryerStatus), 0);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra("id", 0);
            int state = intent.getIntExtra("state", 0);

            setStatus((TextView)findViewById(id == 0 ? R.id.washerStatus : R.id.dryerStatus), state);
        }
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Intent service = new Intent(this, SensorService.class);
        service.putExtra("stop", true);
        this.startService(service);

        service = new Intent(this, SensorService.class);
        service.putExtra("stop", false);
        this.startService(service);
    }
}
