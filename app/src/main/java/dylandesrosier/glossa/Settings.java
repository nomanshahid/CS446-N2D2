package dylandesrosier.glossa;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.List;

import dylandesrosier.glossa.database.AppDatabase;

public class Settings extends AppCompatActivity {
    private static class TimeRange {
        String startTime;
        String endTime;

        public TimeRange() {
        }

        public TimeRange(String startTime,
                         String endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    private AppDatabase appDb;
    private MapView map;
    private boolean locationAccepted;
    private boolean internetAccepted;
    private boolean internetStateAccepted;
    private boolean writeStorageAccepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_settings);

        // Check permissions
        String[] permissions = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android.permission.WRITE_EXTERNAL_STORAGE"};
        ActivityCompat.requestPermissions(this, permissions, 200);

        appDb = AppDatabase.getInstance(this);
        map = findViewById(R.id.map);

        // Activity setup
        setupSettingsListeners();
        setupMap();

        // Set the status bar to be transparent and lock app to portrait
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                internetAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                internetStateAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                writeStorageAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!locationAccepted || !internetAccepted || !internetStateAccepted || !writeStorageAccepted)
            finish();
    }

    private void setupSettingsListeners() {
        final Switch timeSwitch = findViewById(R.id.timeSwitch);
        final Switch locationSwitch = findViewById(R.id.locationSwitch);
        final TextView startTimeText = findViewById(R.id.startTimeText);
        final TextView endTimeText = findViewById(R.id.endTimeText);

        String notifType = getNotifType();

        switch (notifType) {
            case "NONE":
                timeSwitch.setChecked(false);
                locationSwitch.setChecked(false);
                startTimeText.setEnabled(false);
                endTimeText.setEnabled(false);
                break;
            case "TIME":
                timeSwitch.setChecked(true);
                locationSwitch.setChecked(false);
                startTimeText.setEnabled(true);
                endTimeText.setEnabled(true);
                break;
            case "LOCATION":
                timeSwitch.setChecked(false);
                locationSwitch.setChecked(true);
                startTimeText.setEnabled(false);
                endTimeText.setEnabled(false);
                break;
            default:
        }

        TimeRange timeRange = getTime();
        startTimeText.setText(timeRange.startTime);
        endTimeText.setText(timeRange.endTime);

        timeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = ((Switch) v).isChecked();

                if (enabled) {
                    locationSwitch.setChecked(false);
                    startTimeText.setEnabled(true);
                    endTimeText.setEnabled(true);
                    saveNotifType("TIME");
                } else {
                    startTimeText.setEnabled(false);
                    endTimeText.setEnabled(false);
                    saveNotifType(locationSwitch.isChecked() ? "LOCATION" : "NONE");
                }
            }
        });

        locationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = ((Switch) v).isChecked();
                if (enabled) {
                    timeSwitch.setChecked(false);
                    startTimeText.setEnabled(false);
                    endTimeText.setEnabled(false);
                    saveNotifType("LOCATION");
                } else {
                    saveNotifType(timeSwitch.isChecked() ? "TIME" : "NONE");
                }
            }
        });


        // Open time picker on click
        startTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tp = createTimePicker(startTimeText);
                tp.show();
            }
        });

        endTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tp = createTimePicker(endTimeText);
                tp.show();
            }
        });
    }

    private void setupMap() {
        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.long_press_map, Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.save_location), Toast.LENGTH_SHORT);
                toast.show();
                setMarker(p);
                saveLocation(p);
                return false;
            }
        };

        MapEventsOverlay OverlayEvents = new MapEventsOverlay(this, mapEventsReceiver);
        map.getOverlays().add(OverlayEvents);

        map.setClipToOutline(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(17.0);

        // Center the map on the saved location, otherwise center it on the current location
        GeoPoint center = getLocation();
        if (center == null){
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            if ( ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                center = new GeoPoint(location.getLatitude(), location.getLongitude());
            }
        } else {
            // Set center to UW
            center = new GeoPoint(43.472286, -80.544861);
        }
        mapController.setCenter(center);
    }

    private TimePickerDialog createTimePicker(final TextView tv) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        TimePickerDialog timePicker;
        timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                int hour = selectedHour;
                String sHour;
                if (hour < 10) {
                    sHour = "0" + hour;
                } else {
                    sHour = String.valueOf(hour);
                }

                int minute = selectedMinute;
                String sMinute;
                if (minute < 10) {
                    sMinute = "0" + minute;
                } else {
                    sMinute = String.valueOf(minute);
                }

                String viewText = sHour + ":" + sMinute;
                tv.setText(viewText);

                if (tv.getId() == R.id.startTimeText) {
                    TextView endTimeText = findViewById(R.id.endTimeText);
                    saveTime(viewText, endTimeText.getText().toString());
                } else {
                    TextView startTimeText = findViewById(R.id.startTimeText);
                    saveTime(startTimeText.getText().toString(), viewText);
                }
            }
        }, hour, minute, false);
        return timePicker;
    }

    private void saveLocation(GeoPoint p) {
        List<dylandesrosier.glossa.database.Settings> settingsList = appDb.settingsDao().getSettings();
        dylandesrosier.glossa.database.Settings newSettings;
        if (settingsList.size() > 0) {
            newSettings = settingsList.get(0);
            newSettings.longitude = p.getLongitude();
            newSettings.latitude = p.getLatitude();
            appDb.settingsDao().updateSettings(newSettings);

        } else {
            newSettings = new dylandesrosier.glossa.database.Settings("NONE", null, null, p.getLongitude(), p.getLatitude());
            appDb.settingsDao().insertSettings(newSettings);
        }
    }

    private GeoPoint getLocation() {
        List<dylandesrosier.glossa.database.Settings> settingsList = appDb.settingsDao().getSettings();

        if (settingsList.size() > 0) {
            dylandesrosier.glossa.database.Settings curSettings = settingsList.get(0);
            if (curSettings.latitude != null && curSettings.longitude != null) {
                GeoPoint p = new GeoPoint(curSettings.latitude, curSettings.longitude);
                setMarker(p);
                return p;
            }
        }

        return null;
    }

    private void setMarker(GeoPoint p) {
        // Check if marker exists and update
        List<Overlay> overlays = map.getOverlays();
        Boolean isMarkerCreated = false;
        for (Overlay ol : overlays) {
            if (ol instanceof Marker) {
                isMarkerCreated = true;
                ((Marker) ol).setPosition(p);
                map.invalidate();
            }
        }

        // Create marker if it doesn't exist
        if (!isMarkerCreated) {
            Marker marker = new Marker(map);
            marker.setPosition(p);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(getResources().getDrawable(R.drawable.notifications_active));
            marker.setTitle(getResources().getString(R.string.learning_location));
            map.getOverlays().add(marker);
            map.invalidate();
        }
    }

    private void saveNotifType(String notifType) {
        List<dylandesrosier.glossa.database.Settings> settingsList = appDb.settingsDao().getSettings();
        dylandesrosier.glossa.database.Settings newSettings;
        if (settingsList.size() > 0) {
            newSettings = settingsList.get(0);
            newSettings.notif_type = notifType;
            appDb.settingsDao().updateSettings(newSettings);

        } else {
            newSettings = new dylandesrosier.glossa.database.Settings(notifType, null, null, null, null);
            appDb.settingsDao().insertSettings(newSettings);
        }
    }

    private String getNotifType() {
        List<dylandesrosier.glossa.database.Settings> settingsList = appDb.settingsDao().getSettings();

        if (settingsList.size() > 0) {
            dylandesrosier.glossa.database.Settings curSettings = settingsList.get(0);
            return curSettings.notif_type;
        } else {
            return "NONE";
        }
    }

    // Start and end time is in the format HH:MM (HH is 24 hour format)
    private void saveTime(String startTime, String endTime) {
        List<dylandesrosier.glossa.database.Settings> settingsList = appDb.settingsDao().getSettings();
        dylandesrosier.glossa.database.Settings newSettings;
        if (settingsList.size() > 0) {
            newSettings = settingsList.get(0);
            newSettings.start_time = startTime;
            newSettings.end_time = endTime;
            appDb.settingsDao().updateSettings(newSettings);
        } else {
            newSettings = new dylandesrosier.glossa.database.Settings("NONE", startTime, endTime, null, null);
            appDb.settingsDao().insertSettings(newSettings);
        }
    }

    private TimeRange getTime() {
        List<dylandesrosier.glossa.database.Settings> settingsList = appDb.settingsDao().getSettings();
        TimeRange timeRange = new TimeRange();
        if (settingsList.size() > 0) {
            dylandesrosier.glossa.database.Settings curSettings = settingsList.get(0);
            timeRange.startTime = curSettings.start_time;
            timeRange.endTime = curSettings.end_time;
        }

        return timeRange;
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}
