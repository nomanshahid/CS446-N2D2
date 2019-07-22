package dylandesrosier.glossa;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Collections;

/* Helper class to display quiz notification at specific time */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String currLang = "Korean"; //intent.getStringExtra("language_selection");
        Language langModel = new Korean();// (Language) intent.getSerializableExtra("language");;
        Character currChar = langModel.getFirstGameLetter().getCharacter();
        ArrayList<Letter> currOptions = langModel.getOptionLetters(langModel.getFirstGameLetter());
        currOptions.add(langModel.getFirstGameLetter());
        Collections.shuffle(currOptions);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent quizIntent = new Intent(context, Game.class);
        quizIntent.putExtra("language_selection", currLang);
        quizIntent.putExtra("language", langModel);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,quizIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        String notifMsg = String.format("What sound does '%s' make in %s?", currChar, currLang);
        for (int i = 0; i < currOptions.size(); i++) {
            notifMsg += String.format("\n%d. %s", i+1, currOptions.get(i).getPronunciation());
        }
        notifMsg += "\nTap to answer in the Glossa app.";

        int notificationId = 001;
        Notification notification = new NotificationCompat.Builder(context, context.getString(R.string.CHANNEL_ID))
                .setSmallIcon(R.drawable.ic_translate_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.south_korea_flag))
                .setContentIntent(pendingIntent)
                .setContentTitle("Quiz Time!")
                .setContentText(String.format("What sound does '%s' make? Expand to view options.", currChar))
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notifMsg))
                .setAutoCancel(true)
                .build();
        notificationManager.notify(notificationId, notification);

    }

}
