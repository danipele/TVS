package app.tvs.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;

import app.tvs.services.UpdateTVSeriesShortService;

public class UpdateTVSeriesShortJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new Thread(() -> UpdateTVSeriesShortService.update(getApplicationContext())).start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
