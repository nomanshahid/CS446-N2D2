package dylandesrosier.glossa;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import dylandesrosier.glossa.database.AppDatabase;
import dylandesrosier.glossa.database.KoreanLetter;

public class Account extends AppCompatActivity {
    private AppDatabase appDb;
    private Language language;
    private String languageSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        language = (Language) getIntent().getSerializableExtra("language");
        languageSelection = getIntent().getStringExtra("language_selection");

        Log.i("math", languageSelection);

        ImageButton shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareStats();
            }
        });

        // Set the status bar to be transparent and lock app to portrait
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        TextView statTitle = findViewById(R.id.statTitle);
        statTitle.setText(String.format("%s Statistics", languageSelection));

        appDb = AppDatabase.getInstance(this);
        List<KoreanLetter> letters = appDb.koreanLetterDao().getLetters();
        LinearLayout statLayout = findViewById(R.id.statLayout);
        for(final KoreanLetter letter : letters) {
            View statRowLayout = LayoutInflater.from(this).inflate(R.layout.stat_row, null, false);

            TextView letterTextView = statRowLayout.findViewById(R.id.statLetter);
            TextView accuracyText = statRowLayout.findViewById(R.id.accuracyText);

            letterTextView.setText(letter.letter.toString());
            float acc = letter.num_correct / (float) letter.num_seen;
            accuracyText.setText(String.format("%d%%", Math.round(acc * 100)));

            statLayout.addView(statRowLayout);
        }

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void ShareStats(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        HashMap<Character, Letter> languageLetters = language.getLetters();
        int totalSize = languageLetters.size();
        List<KoreanLetter> learningLetters = appDb.koreanLetterDao().getLetters();
        int amtLearned = 0;
        for(final KoreanLetter letter : learningLetters) {
            if (letter.num_correct / (float) letter.num_seen > 0.5) {
               amtLearned++;
            }
        }
        String shareBody = String.format("I've learned %d%% of the %s alphabet so far! You can learn it too by trying out Glossa.",
                Math.round((amtLearned / (float) totalSize) * 100), languageSelection);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out my Glossa progress!");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share your progress"));
    }
}
