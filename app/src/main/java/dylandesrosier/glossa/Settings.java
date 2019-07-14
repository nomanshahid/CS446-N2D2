package dylandesrosier.glossa;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.icu.util.Calendar;
import android.os.Build;
import android.preference.PreferenceManager;
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
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import dylandesrosier.glossa.database.AppDatabase;

public class Settings extends AppCompatActivity {
    AppDatabase appDb;
    MapView map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_settings);

        // TODO: Check permissions

        appDb = AppDatabase.getInstance(this);

        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Naveed", "Location", new GeoPoint(43.480732, -80.529185))); // Lat/Lon decimal degrees

        //the overlay
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, this);
        mOverlay.setFocusItemsOnTap(true);

        // map events
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {

                // write your code here
                String longitude = Double
                        .toString(((double) p.getLongitude()));
                String latitude = Double
                        .toString(((double) p.getLatitude()));
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Longitude: "
                                + longitude + " Latitude: " + latitude, Toast.LENGTH_SHORT);
                toast.show();

                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                // write your code here
                return false;
            }
        };
        MapEventsOverlay OverlayEvents = new MapEventsOverlay(this, mReceive);

        map = findViewById(R.id.map);
        map.setClipToOutline(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(43.480732, -80.529185);
        mapController.setCenter(startPoint);
        map.getOverlays().add(OverlayEvents);
        map.getOverlays().add(mOverlay);

        Switch timeSwitch = findViewById(R.id.timeSwitch);
        Switch locationSwitch = findViewById(R.id.locationSwitch);
        TextView startTimeText = findViewById(R.id.startTimeText);
        TextView endTimeText = findViewById(R.id.endTimeText);

        timeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = ((Switch) v).isChecked();
                TextView startTimeText = findViewById(R.id.startTimeText);
                TextView endTimeText = findViewById(R.id.endTimeText);
                Switch locationSwitch = findViewById(R.id.locationSwitch);
                if (enabled) {
                    // TODO: Disable location and enable time
                    locationSwitch.setChecked(false);
                    startTimeText.setEnabled(true);
                    endTimeText.setEnabled(true);
                    enableTimeNotif();
                } else {
                    // TODO: Disable time and enable location
                    startTimeText.setEnabled(false);
                    endTimeText.setEnabled(false);
                }
            }
        });

        locationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = ((Switch) v).isChecked();
                Switch timeSwitch = findViewById(R.id.timeSwitch);
                if (enabled) {
                    // TODO: Disable time and enable location
                    timeSwitch.setChecked(false);
                } else {
                    // TODO: Disable location and enable time
                }
            }
        });


        // Open time picker on focus and click
        startTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView startTimeText = findViewById(R.id.startTimeText);
                TimePickerDialog tp = createTimePicker(startTimeText);
                tp.show();
            }
        });

        startTimeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    TextView startTimeText = findViewById(R.id.startTimeText);
                    TimePickerDialog tp = createTimePicker(startTimeText);
                    tp.show();
                }
            }
        });

        endTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView endTimeText = findViewById(R.id.endTimeText);
                TimePickerDialog tp = createTimePicker(endTimeText);
                tp.show();
            }
        });

        endTimeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    TextView endTimeText = findViewById(R.id.endTimeText);
                    TimePickerDialog tp = createTimePicker(endTimeText);
                    tp.show();
                }
            }
        });


        // Set the status bar to be transparent and lock app to portrait
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private TimePickerDialog createTimePicker(final TextView tv){
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

    private void enableTimeNotif(){
        List<dylandesrosier.glossa.database.Settings> curSettings = appDb.settingsDao().getSettings();
        if (curSettings.size() > 0){
            // TODO: Update settings
        } else {
            // TODO: Create settings entry
            dylandesrosier.glossa.database.Settings newSettings = new dylandesrosier.glossa.database.Settings(true, null, null, false);
            appDb.settingsDao().insertSettings(newSettings);
        }
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}
