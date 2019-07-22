package dylandesrosier.glossa;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Helper class to display quiz notification at specific location */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e("GeofenceBroadcastReceiver", "Error in geofence receiver");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            showNotification(context);
            Log.i("GeofenceBroadcastReceiver", geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e("GeofenceBroadcastReceiver", "Invalid Type");
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited";
            default:
                return "Unknown Transition";
        }
    }

    private void showNotification(Context context) {
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

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, quizIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
