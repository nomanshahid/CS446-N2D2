package dylandesrosier.glossa;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/* Helper class to display quiz notification at specific time */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent quizIntent = new Intent(context, MainActivity.class); // TODO: this is where we go to the quiz question

        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,quizIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        String testOptions = "What sound does 'Oh' make in Korean?\n1. Ou\n2. Ah\n3. Ee\n4. Ay\nTap to answer in the Glossa app.";

        int notificationId = 001;
        Notification notification = new NotificationCompat.Builder(context, context.getString(R.string.CHANNEL_ID))
                .setSmallIcon(R.drawable.ic_translate_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.south_korea_flag))
                .setContentIntent(pendingIntent)
                .setContentTitle("Quiz Time!")
                .setContentText("What sound does 'Oh' make? Tap to view options.")
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(testOptions))
                .setAutoCancel(true)
                .build();
        notificationManager.notify(notificationId, notification);

    }

}
