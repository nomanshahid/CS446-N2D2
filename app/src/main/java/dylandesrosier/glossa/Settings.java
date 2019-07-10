package dylandesrosier.glossa;

import android.app.TimePickerDialog;
import android.content.pm.ActivityInfo;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch timeSwitch = findViewById(R.id.timeSwitch);
        Switch locationSwitch = findViewById(R.id.locationSwitch);
        TextView startTimeText = findViewById(R.id.startTimeText);
        TextView endTimeText = findViewById(R.id.endTimeText);

        // TODO: Get and populate previous settings

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
}
