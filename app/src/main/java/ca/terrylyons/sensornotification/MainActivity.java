package ca.terrylyons.sensornotification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.activity_main);

        int [] statuses = getCurrentValues();

        TextView washerStatus = (TextView)findViewById(R.id.washerStatus);
        TextView dryerStatus = (TextView)findViewById(R.id.dryerStatus);

        setStatus(washerStatus, statuses[0]);
        setStatus(dryerStatus, statuses[1]);

        Intent service = new Intent(this, SensorService.class);
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

    private int[] getCurrentValues()
    {
        FileInputStream file = null;
        BufferedReader reader = null;

        int[] statuses = new int[2];
        statuses[0] = 0;
        statuses[1] = 0;

        try {
            file = getApplicationContext().openFileInput("sensor.txt");
            reader = new BufferedReader(new InputStreamReader(file));

            String data = reader.readLine();
            String[] values = data.split(",");


            if (values.length > 3)
            {
                statuses[0] = Integer.parseInt(values[0]);
                statuses[1] = Integer.parseInt(values[2]);
            }
        }
        catch (IOException ex)
        {
        }
        finally {
            try
            {
                if (reader != null) {
                    reader.close();
                }
                if (file != null) {
                    file.close();
                }
            }
            catch(IOException ex)
            {
            }
        }

        return statuses;
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
}
