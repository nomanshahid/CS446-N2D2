package dylandesrosier.glossa;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import dylandesrosier.glossa.database.AppDatabase;

import static org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay.backgroundColor;
import static org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay.fontColor;
import static org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay.fontSizeDp;

public class Settings extends AppCompatActivity {
    AppDatabase appDb;
    MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_settings);

        // TODO: Check permissions

        appDb = AppDatabase.getInstance(this);
        map = findViewById(R.id.map);

        SetupSettingsListeners();
        SetupMap();

        // Set the status bar to be transparent and lock app to portrait
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void SetupSettingsListeners() {
        final Switch timeSwitch = findViewById(R.id.timeSwitch);
        final Switch locationSwitch = findViewById(R.id.locationSwitch);
        final TextView startTimeText = findViewById(R.id.startTimeText);
        final TextView endTimeText = findViewById(R.id.endTimeText);

        // TODO: Load settings from db

        timeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = ((Switch) v).isChecked();

                if (enabled) {
                    // TODO: Disable location and enable time
                    locationSwitch.setChecked(false);
                    startTimeText.setEnabled(true);
                    endTimeText.setEnabled(true);
                } else {
                    // TODO: Disable time
                    startTimeText.setEnabled(false);
                    endTimeText.setEnabled(false);
                }
            }
        });

        locationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = ((Switch) v).isChecked();
                if (enabled) {
                    // TODO: Disable time and enable location
                    // TODO: If no location set when enabling, show toast
                    timeSwitch.setChecked(false);
                } else {
                    // TODO: Disable location and enable time
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

    private void SetupMap() {
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
        mapController.setCenter(getLocation());
    }

    private TimePickerDialog createTimePicker(final TextView tv) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        TimePickerDialog timePicker;
        timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                tv.setText(((selectedHour > 12) ? selectedHour % 12 : selectedHour) + ":" + (selectedMinute < 10 ? ("0" + selectedMinute) : selectedMinute) + " " + ((selectedHour >= 12) ? "PM" : "AM"));
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

        } else {
            newSettings = new dylandesrosier.glossa.database.Settings("NONE", null, null, p.getLongitude(), p.getLatitude());
        }

        appDb.settingsDao().insertSettings(newSettings);
    }

    private GeoPoint getLocation(){
        List<dylandesrosier.glossa.database.Settings> settingsList = appDb.settingsDao().getSettings();
        GeoPoint p;
        if (settingsList.size() > 0) {
            dylandesrosier.glossa.database.Settings curSettings = settingsList.get(0);
            if (curSettings.latitude != null && curSettings.longitude != null){
                p = new GeoPoint(curSettings.latitude, curSettings.longitude);
                setMarker(p);
                return p;
            }
        }

        // TODO: return user's current location
        p = new GeoPoint(43.480732, -80.529182);
        return p;
    }

    private void setMarker(GeoPoint p){
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



    // Start and end time is in the format HH:MM (HH is 24 hour format)
    private void saveTime(String startTime, String endTime) {
        List<dylandesrosier.glossa.database.Settings> settingsList = appDb.settingsDao().getSettings();
        dylandesrosier.glossa.database.Settings newSettings;
        if (settingsList.size() > 0) {
            newSettings = settingsList.get(0);
            newSettings.start_time = startTime;
            newSettings.end_time = endTime;

        } else {
            newSettings = new dylandesrosier.glossa.database.Settings("NONE", startTime, endTime, null, null);
        }

        appDb.settingsDao().insertSettings(newSettings);
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
