package ca.terrylyons.sensornotification;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Terry2 on 24/03/2016.
 */
public class SensorStatus implements Serializable {
    public int Id;
    public boolean State;
    public Date TimeStamp;

    public SensorStatus()
    {}
}
