package ca.terrylyons.sensornotification;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * Created by Terry2 on 25/03/2016.
 */
public class SensorPersistence {
    public SensorStatus getStatus(Context context, int id)
    {
        FileInputStream file = null;
        ObjectInputStream stream = null;

        SensorStatus status = new SensorStatus();
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

    public void setStatus(Context context, SensorStatus status)
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
