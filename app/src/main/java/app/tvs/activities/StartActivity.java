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
import app.tvseries.R;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Global.database = Room.databaseBuilder(this, Database.class, getString(R.string.DbName)).allowMainThreadQueries().fallbackToDestructiveMigration().build();

        new Handler().postDelayed(this::startApp,700);

    }

    private void startApp() {
        Intent intent = new Intent(this, TVSeriesActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.nothing, R.anim.nothing);
        finish();
    }

}
