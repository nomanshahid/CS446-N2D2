package dylandesrosier.glossa;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
        language = (Language) getIntent().getSerializableExtra("language");

        TextView titleText = findViewById(R.id.languageText);
        titleText.setText(languageSelection);

        // Add letters to linear layout
        LinearLayout alphabetLayout = findViewById(R.id.alphabetLayout);
        for (final Character letter : language.getLetters().keySet()) {
            View alphabetRowLayout = LayoutInflater.from(this).inflate(R.layout.alphabet_row, null, false);

            TextView letterTextView =  alphabetRowLayout.findViewById(R.id.letterTextView);
            TextView pronunciationTextView =  alphabetRowLayout.findViewById(R.id.pronunciationTextView);
            TextView categoryTextView =  alphabetRowLayout.findViewById(R.id.categoryTextView);

            letterTextView.setText(Character.toString(letter));
            pronunciationTextView.setText(language.getLetters().get(letter).getPronunciation());
            categoryTextView.setText(language.getLetters().get(letter).getCategory());
            alphabetRowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), Pronunciation.class);
                    intent.putExtra("letter", Character.toString(letter));
                    intent.putExtra("pronunciation", language.getLetters().get(letter).getPronunciation());
                    startActivity(intent);
                }
            });
            alphabetLayout.addView(alphabetRowLayout);
        }

        // Set the status bar to be transparent and lock app to portrait
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
