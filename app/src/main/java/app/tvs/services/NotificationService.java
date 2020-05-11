package app.tvs.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import app.tvs.activities.StartActivity;
import app.tvs.activities.TVSeriesActivity;
import app.tvseries.R;

class NotificationService {

    static void addNotification(String message, Context context, String channelId, CharSequence name, String description) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(description);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.tvs_icon)
                .setColor(context.getColor(R.color.colorPrimary))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(description)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setAutoCancel(true);

        Intent intentStartActivity = new Intent(context, StartActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(TVSeriesActivity.class);
        stackBuilder.addNextIntent(intentStartActivity);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(resultPendingIntent);
        notificationManager.notify(1, notification.build());
    }

}
