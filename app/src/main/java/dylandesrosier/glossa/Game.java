package dylandesrosier.glossa;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;
import android.os.*;
import android.view.View;
import android.widget.*;
import android.view.Window;
import android.view.WindowManager;
import java.util.*;

import dylandesrosier.glossa.database.AppDatabase;
import dylandesrosier.glossa.database.KoreanLetter;

public class Game extends AppCompatActivity {
    private final int maxLetters = 5;
    private int currLevel = 1;
    private int wrongCount = 0;
    private Letter mainLetter = null;
    private Button mainButton = null;
    private String languageSelection;

    private AppDatabase appDb;

    private Language language;

    ArrayList<Letter> gameLetters = new ArrayList<>();

    private TextView levelTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        appDb = AppDatabase.getInstance(this);

        // Set the language label to the selected language
        language = (Language) getIntent().getSerializableExtra("language");
        languageSelection = getIntent().getStringExtra("language_selection");

        ((TextView)findViewById(R.id.game_language_label)).setText(languageSelection);
        ((TextView)findViewById(R.id.max_level)).setText("/" + maxLetters);

        // initialize level text view
        levelTextView = findViewById(R.id.curr_level);

        // Set the status bar to be transparent and lock app to portrait
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // initialize game variables
        gameLetters = language.getGameLetters(maxLetters);

        // Initialize game stage
        generateStage();
    }

    private void generateStage() {
        TextView characterView = findViewById(R.id.game_character_text);

        // retrieve buttons and set its designs
        final Button option1 = findViewById(R.id.btn_option_1);
        final Button option2 = findViewById(R.id.btn_option_2);
        final Button option3 = findViewById(R.id.btn_option_3);
        setupButton(option1);
        setupButton(option2);
        setupButton(option3);

        // initialize buttons and letters array list
        ArrayList<Button> buttons = new ArrayList<>(Arrays.asList(option1, option2, option3));
        ArrayList<Letter> letters = new ArrayList<>();

        mainLetter = gameLetters.get(0);
        // pick the correct value and settext in main character window
        Collections.shuffle(gameLetters);

        letters.add(mainLetter);
        characterView.setText(Character.toString(mainLetter.getCharacter()));
        characterView.setTextColor(getColor(R.color.DarkGrey));

        ArrayList<Letter> options = language.getOptionLetters(mainLetter);
        letters.add(options.get(0));
        letters.add(options.get(1));

        // cycle through letters and assign to random button
        for (int i = 0; i < 3; i++) {
            Button b = buttons.get(getRand(buttons.size()));
            Letter l = letters.get(i);
            if (mainLetter == l) {
                mainButton = b;
            }
            b.setText(letters.get(i).getPronunciation());
            b.setTextColor(getColor(R.color.DarkGrey));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    option1.setClickable(false);
                    option2.setClickable(false);
                    option3.setClickable(false);
                    onButtonClick((Button)view);
                }
            });
            buttons.remove(b);
        }
    }

    private void determineNextStage() {
        // start new activity if user completed all max characters otherwise new stage
        if (currLevel > maxLetters || gameLetters.size() <= 0) {
            Intent intent = new Intent(getApplicationContext(), GameOver.class);
            intent.putExtra("language_selection", languageSelection);
            intent.putExtra("language", language);
            intent.putExtra("wrong_count", wrongCount);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } else {
            generateStage();
        }
    }

    public void onButtonClick(Button b) {
        if (b.getText().toString() == mainLetter.getPronunciation()) {
            // correct
            currLevel++;
            gameLetters.remove(mainLetter);
        } else {
            // wrong
            wrongCount++;
            b.setBackgroundResource(R.drawable.game_option_button_incorrect);
        }

        this.updateLetterStats(b);

        mainButton.setBackgroundResource(R.drawable.game_option_button_correct);
        levelTextView.setText(Integer.toString(currLevel - 1));

        // transition delay of 1s to next stage
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                determineNextStage();
            }
        }, 1000);
    }

    private void updateLetterStats(Button b){
        if (this.language instanceof Korean){
            KoreanLetter k = appDb.koreanLetterDao().getLetter(mainLetter.getCharacter());
            if (k == null) {
                k = new KoreanLetter(mainLetter.getCharacter(), 1, 0);
            } else {
                k.num_seen++;
            }

            if (b.getText().toString() == mainLetter.getPronunciation()) {
                k.num_correct++;
            }

            appDb.koreanLetterDao().updateLetter(k);
        }
    }

    private void setupButton(Button b) {
        b.setEnabled(true);
        b.setBackgroundResource(R.drawable.game_option_button);
    }

    private int getRand(int max) {
        return (int) (max * Math.random());
    }
}
