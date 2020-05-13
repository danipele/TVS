package app.tvs.jobs;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;

import app.tvs.services.NotificationService;
import app.tvs.services.UpdateTVSeriesShortService;
import app.tvseries.R;

public class UpdateTVSeriesShortJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new Thread(() -> UpdateTVSeriesShortService.update(getApplicationContext())).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Context context = getApplicationContext();
        NotificationService.addNotification(context.getString(R.string.updateStopped), context, context.getString(R.string.updateTVSeriesShortChannel), context.getString(R.string.updateTVSeriesShortName), context.getString(R.string.updateTVSeriesShortDescription));
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

}
