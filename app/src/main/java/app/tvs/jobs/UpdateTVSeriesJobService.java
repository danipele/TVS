package app.tvs.jobs;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;

import app.tvs.services.NotificationService;
import app.tvs.services.UpdateTVSeriesService;
import app.tvseries.R;

public class UpdateTVSeriesJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new Thread(() -> UpdateTVSeriesService.update(getApplicationContext())).start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Context context = getApplicationContext();
        NotificationService.addNotification(context.getString(R.string.updateStopped), context, context.getString(R.string.updateTVSeriesChannel), context.getString(R.string.updateTVSeriesJobName), context.getString(R.string.updateTVSeriesDescription));
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

}
