package dylandesrosier.glossa;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dylandesrosier.glossa.database.AppDatabase;

public class MainActivity extends AppCompatActivity {
    private ArrayList<LanguageItem> languageList;
    private LanguageSpinnerAdapter languageSpinnerAdapter;
    private String languageSelection;
    private AppDatabase appDb;
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appDb = AppDatabase.getInstance(this);
        createNotificationChannel();
        configureQuizNotification();
        initLanguageList();
        Spinner languageSpinner = findViewById(R.id.languageSpinner);
        languageSpinnerAdapter = new LanguageSpinnerAdapter(this, languageList);
        languageSpinner.setAdapter(languageSpinnerAdapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LanguageItem clickedItem = (LanguageItem) adapterView.getItemAtPosition(i);
                languageSelection = clickedItem.getLanguageName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Add click listeners for buttons
        ImageButton play = findViewById(R.id.playButton);
        ImageButton alpha = findViewById(R.id.alphabetButton);
        ImageButton settings = findViewById(R.id.settingsButton);
        ImageButton account = findViewById(R.id.accountButton);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGame();
            }
        });
        alpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAlphabet();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettings();
            }
        });
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAccount();
            }
        });

        // Set the status bar to be transparent and lock app to portrait
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /* Helper to show quiz notification at a specific time */
    public void configureQuizNotification() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String notifType = getNotifType();

        switch (notifType) {
            case "NONE":
                break;
            case "TIME": // Time-based notifications
                Settings.TimeRange timeRange = getTime();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeRange.startTime.substring(0, 2)));
                calendar.set(Calendar.MINUTE, Integer.parseInt(timeRange.startTime.substring(4)));

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                break;
            case "LOCATION": // Location-based notifications
                createGeofence();
                break;
            default:
        }
    }

    /* Standard code from https://developer.android.com/training/notify-user/build-notification.html */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Quiz channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void openAlphabet() {
        Intent intent = new Intent(getApplicationContext(), Alphabet.class);
        intent.putExtra("language_selection", languageSelection);
        startActivity(intent);
    }

    public void openGame() {
        Intent intent = new Intent(getApplicationContext(), Game.class);
        intent.putExtra("language_selection", languageSelection);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void openAccount() {
        Intent intent = new Intent(getApplicationContext(), Account.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(getApplicationContext(), Settings.class);
        startActivity(intent);
    }

    private void initLanguageList() {
        languageList = new ArrayList<>();
        languageList.add(new LanguageItem(this.getResources().getString(R.string.korean_text), R.drawable.south_korea_flag));
        languageList.add(new LanguageItem(this.getResources().getString(R.string.bengali_text), R.drawable.bangladesh_flag));
        languageList.add(new LanguageItem(this.getResources().getString(R.string.english_text), R.drawable.uk_flag));
    }

    /* https://developer.android.com/training/location/geofencing */
    private void createGeofence() {
        if (getLocation() == null) return;
        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("1")
                .setCircularRegion(
                        getLocation().getLatitude(),
                        getLocation().getLongitude(),
                        500
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());


        mGeofencingClient = LocationServices.getGeofencingClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Geofences added
                            Log.v("MainActivity", "Successfully added geofence");
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to add geofences
                            Log.e("MainActivity", "Error adding geofence");
                        }
                    });
        }

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    // Helpers for retrieving info from DB
    private String getNotifType() {
        List<dylandesrosier.glossa.database.Settings> settingsList = appDb.settingsDao().getSettings();

        if (settingsList.size() > 0) {
            dylandesrosier.glossa.database.Settings curSettings = settingsList.get(0);
            return curSettings.notif_type;
        } else {
            return "NONE";
        }
    }

    private Settings.TimeRange getTime() {
        List<dylandesrosier.glossa.database.Settings> settingsList = appDb.settingsDao().getSettings();
        Settings.TimeRange timeRange = new Settings.TimeRange();
        if (settingsList.size() > 0) {
            dylandesrosier.glossa.database.Settings curSettings = settingsList.get(0);
            timeRange.startTime = curSettings.start_time;
            timeRange.endTime = curSettings.end_time;
        }

        return timeRange;
    }

    private GeoPoint getLocation() {
        List<dylandesrosier.glossa.database.Settings> settingsList = appDb.settingsDao().getSettings();

        if (settingsList.size() > 0) {
            dylandesrosier.glossa.database.Settings curSettings = settingsList.get(0);
            if (curSettings.latitude != null && curSettings.longitude != null) {
                GeoPoint p = new GeoPoint(curSettings.latitude, curSettings.longitude);
                return p;
            }
        }

        return null;
    }
}
