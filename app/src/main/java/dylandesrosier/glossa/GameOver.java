package dylandesrosier.glossa;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

public class GameOver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // display wrong count
        int wrongCount = getIntent().getIntExtra("wrong_count", 0);
        TextView wrongText = findViewById(R.id.wrong_count);
        wrongText.setText(Integer.toString(wrongCount));

        // remove plural form for questions
        if (wrongCount == 1) {
            TextView staticText = findViewById(R.id.static_game_over_text2);
            staticText.setText("QUESTION WRONG");
        }


        // attach click handlers to buttons
        ImageButton replayBtn = findViewById(R.id.btn_replay);
        ImageButton homeBtn = findViewById(R.id.btn_home);
        replayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReplayClick();
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHomeClick();
            }
        });

        // Set the status bar to be transparent and lock app to portrait
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void onReplayClick() {
        Intent intent = new Intent(getApplicationContext(), Game.class);
        intent.putExtra("language_selection", getIntent().getStringExtra("language_selection"));
        intent.putExtra("language", getIntent().getSerializableExtra("language"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void onHomeClick() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
