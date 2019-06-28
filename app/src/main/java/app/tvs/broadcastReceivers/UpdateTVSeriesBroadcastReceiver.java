package app.tvs.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.tvs.services.UpdateTVSeriesService;

public class UpdateTVSeriesBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, UpdateTVSeriesService.class));
    }
}
