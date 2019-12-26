package app.tvs.activities;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import app.tvs.Global;
import app.tvs.db.Database;
import app.tvseries.R;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Global.database = Room.databaseBuilder(this, Database.class, getString(R.string.DbName)).allowMainThreadQueries().fallbackToDestructiveMigration().build();

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

}
