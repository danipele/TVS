package app.tvs.activities;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import app.tvs.Global;
import app.tvs.db.Database;
import app.tvs.jobs.UpdateTVSeriesJobService;
import app.tvs.jobs.UpdateTVSeriesShortJobService;
import app.tvseries.R;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Global.database = Room.databaseBuilder(this, Database.class, getString(R.string.DbName)).allowMainThreadQueries().fallbackToDestructiveMigration().build();

        scheduleJob(UpdateTVSeriesJobService.class, 86400000, getString(R.string.updateTVSeriesJobName), 20);
        scheduleJob(UpdateTVSeriesShortJobService.class, 604800000, getString(R.string.updateTVSeriesShortJobName), 21);

        new Handler().postDelayed(this::startApp,700);

    }

    private void scheduleJob(Class<?> cls, long time, String preference, int jobId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        JobScheduler jobScheduler = (JobScheduler)getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);

        if (!preferences.getBoolean(preference, false)) {
            ComponentName componentName = new ComponentName(this, cls);
            JobInfo jobInfo = new JobInfo.Builder(jobId, componentName).setPeriodic(time).setPersisted(true).build();
            jobScheduler.schedule(jobInfo);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(preference, true);
            editor.apply();
        }
    }

    private void startApp() {
        Intent intent = new Intent(this, TVSeriesActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.nothing, R.anim.nothing);
        finish();
    }

}
