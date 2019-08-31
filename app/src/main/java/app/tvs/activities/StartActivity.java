package app.tvs.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import app.tvs.Global;
import app.tvs.broadcastReceivers.UpdateTVSeriesShortBroadcastReceiver;
import app.tvs.db.Database;
import app.tvseries.R;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Global.database = Room.databaseBuilder(this, Database.class, getString(R.string.DbName)).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        //Global.database.clearAllTables();
        setScheduledTasks();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startApp();
            }
        },700);

    }

    private void startApp() {
        Intent intent = new Intent(this, TVSeriesActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.nothing, R.anim.nothing);
        finish();
    }

    private void setScheduledTasks() {
        Calendar calendarTVSeriesShort = Calendar.getInstance();
        calendarTVSeriesShort.setTimeInMillis(new Date().getTime());
        if(calendarTVSeriesShort.get(Calendar.HOUR_OF_DAY) >= 8) {
            calendarTVSeriesShort.add(Calendar.DATE, 1);
        }
        calendarTVSeriesShort.set(Calendar.HOUR_OF_DAY, 8);
        calendarTVSeriesShort.set(Calendar.MINUTE, 0);
        calendarTVSeriesShort.set(Calendar.SECOND, 0);
        ((AlarmManager) Objects.requireNonNull(getSystemService(Context.ALARM_SERVICE))).set(AlarmManager.RTC, calendarTVSeriesShort.getTimeInMillis(), PendingIntent.getBroadcast(this, 1, new Intent(this, UpdateTVSeriesShortBroadcastReceiver.class), 0));


    }
}
