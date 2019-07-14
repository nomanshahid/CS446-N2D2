package dylandesrosier.glossa;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private ArrayList<LanguageItem> languageList;
    private LanguageSpinnerAdapter languageSpinnerAdapter;
    private String languageSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        configureQuizNotification();
        initLanguageList();
        Spinner languageSpinner = findViewById(R.id.languageSpinner);
        languageSpinnerAdapter = new LanguageSpinnerAdapter(this, languageList);
        languageSpinner.setAdapter(languageSpinnerAdapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LanguageItem clickedItem = (LanguageItem)adapterView.getItemAtPosition(i);
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
        ImageButton account= findViewById(R.id.accountButton);

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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 15);

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
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

    private void initLanguageList(){
        languageList = new ArrayList<>();
        languageList.add(new LanguageItem(this.getResources().getString(R.string.korean_text), R.drawable.south_korea_flag));
        languageList.add(new LanguageItem(this.getResources().getString(R.string.bengali_text), R.drawable.bangladesh_flag));
        languageList.add(new LanguageItem(this.getResources().getString(R.string.english_text), R.drawable.uk_flag));
    }
}
