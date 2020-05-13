package app.tvs.jobs;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import app.tvs.services.NotificationService;
import app.tvs.services.UpdateTVSeriesService;

public class UpdateTVSeriesJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new Thread(() -> UpdateTVSeriesService.update(getApplicationContext())).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        NotificationService.addNotification("Update stopped.", getApplicationContext(), "C1", "UpdateTVSeries", "Update TVSeries table");
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

}
