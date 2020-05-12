package app.tvs.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;

import app.tvs.services.UpdateTVSeriesService;

public class UpdateTVSeriesJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new Thread(() -> UpdateTVSeriesService.update(getApplicationContext())).start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
