package dylandesrosier.glossa;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Alphabet extends AppCompatActivity {
    private Language language;
    private String languageSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alphabet);

        languageSelection = getIntent().getStringExtra("language_selection");

        TextView titleText = findViewById(R.id.languageText);
        titleText.setText(languageSelection);

        switch (languageSelection){
            case "Korean":
                language = new Korean();
            default:
                language = new Korean();
        }

        LinearLayout alphabetLayout = findViewById(R.id.alphabetLayout);

        for (Character letter : language.getLetters().keySet()) {
            TextView tv = new TextView(this);
            tv.setText("letter: " + letter + " Pronunciation: " + language.getLetters().get(letter).getPronunciation());
            alphabetLayout.addView(tv);
        }

        // Set the status bar to be transparent and lock app to portrait
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
