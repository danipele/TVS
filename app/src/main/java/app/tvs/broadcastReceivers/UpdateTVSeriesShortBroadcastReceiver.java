package app.tvs.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.tvs.services.UpdateTVSeriesShortService;

public class UpdateTVSeriesShortBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, final Intent intent) {
        context.startService(new Intent(context, UpdateTVSeriesShortService.class));
    }
}
