package app.tvs.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;

import app.tvs.services.UpdateTVSeriesShort;

public class UpdateTVSeriesShortJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new Thread(() -> UpdateTVSeriesShort.update(getApplicationContext())).start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
