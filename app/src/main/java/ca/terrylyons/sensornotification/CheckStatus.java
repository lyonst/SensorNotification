package ca.terrylyons.sensornotification;

import android.content.Context;

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
    public boolean HasStatusChanged(Context context, SensorStatus status)
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

        updateStatus(context, status);
        return changed;
    }

    private SensorStatus getCurrentValues(Context context, int id)
    {
        FileInputStream file = null;
        ObjectInputStream stream = null;

        SensorStatus status = new SensorStatus();
        status = new SensorStatus();
        status.Id = id;
        status.State = 0;
        status.TimeStamp = new Date();

        try {
            file = context.getApplicationContext().openFileInput("sensor" + id + ".txt");

            stream = new ObjectInputStream(file);
            status = (SensorStatus)stream.readObject();

        }
        catch (IOException ex) {
        } catch (ClassNotFoundException ex) {
        }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (file != null) {
                    file.close();
                }
            } catch (IOException ex) {
            }
        }

        return status;
    }

    private void updateStatus(Context context, SensorStatus status)
    {
        FileOutputStream file = null;
        ObjectOutputStream stream = null;

        try {
            file = context.getApplicationContext().openFileOutput("sensor" + status.Id + ".txt", Context.MODE_PRIVATE);

            stream = new ObjectOutputStream(file);
            stream.writeObject(status);
        }
        catch (IOException ex) {
        }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (file != null) {
                    file.close();
                }
            } catch (IOException ex) {
            }
        }
    }
}
