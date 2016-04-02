package ca.terrylyons.sensornotification;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Terry2 on 02/04/2016.
 */
public class ScheduleJob {
    public void schedule(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        int frequency = Integer.parseInt(settings.getString("sync_frequency", "30"));

        ComponentName component = new ComponentName(context, SensorService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, component).setPeriodic(frequency * 60000).setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED).build();
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    public void cancel(Context context) {
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(1);
    }

}
