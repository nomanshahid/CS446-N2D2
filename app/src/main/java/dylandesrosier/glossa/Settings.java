package dylandesrosier.glossa;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch time_switch = findViewById(R.id.timeSwitch);
        Switch location_switch = findViewById(R.id.locationSwitch);

        time_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = ((Switch) v).isChecked();
                if (enabled) {
                    // TODO: Disable location and enable time
                    Switch location_switch = findViewById(R.id.locationSwitch);
                    location_switch.setChecked(false);
                } else {
                    // TODO: Disable time and enable location
                }
            }
        });

        location_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = ((Switch) v).isChecked();
                if (enabled) {
                    // TODO: Disable time and enable location
                    Switch time_switch = findViewById(R.id.timeSwitch);
                    time_switch.setChecked(false);
                } else {
                    // TODO: Disable location and enable time
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

    // TODO: Add input validation
}
