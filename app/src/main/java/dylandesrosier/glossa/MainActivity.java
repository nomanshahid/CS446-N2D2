package dylandesrosier.glossa;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void openAlphabet() {
        Intent intent = new Intent(getApplicationContext(), Alphabet.class);
        startActivity(intent);
    }

    public void openGame() {
        Intent intent = new Intent(getApplicationContext(), Game.class);
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
}
