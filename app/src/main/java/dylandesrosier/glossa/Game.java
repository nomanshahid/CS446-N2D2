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

public class Game extends AppCompatActivity {
    private final int maxLevel = 2;
    private int currLevel = 1;
    private int wrongCount = 0;
    private Letter mainLetter = null;
    private Button mainButton = null;
    private String languageSelection;

    ArrayList<Letter> values = new ArrayList<>();
    ArrayList<Letter> allValues = new ArrayList<>();

    private TextView levelTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Korean korean = new Korean(this);
        allValues = new ArrayList<>(korean.getLetters().values());
        Collections.shuffle(allValues);
        for (int i = 0; i < maxLevel; i++) {
            values.add(allValues.get(i));
        }

        // Set the language label to the selected language
        languageSelection = getIntent().getStringExtra("language_selection");
        TextView languageLabel = findViewById(R.id.game_language_label);
        languageLabel.setText(languageSelection);

        // Set the max level label
        TextView maxLabel = findViewById(R.id.max_level);
        maxLabel.setText("/" + maxLevel);

        // initialize level text view
        levelTextView = findViewById(R.id.curr_level);

        // Set the status bar to be transparent and lock app to portrait
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        // pick the correct value and settext in main character window
        Collections.shuffle(values);
        mainLetter = values.get(0);
        letters.add(mainLetter);
        characterView.setText(Character.toString(mainLetter.getCharacter()));


        // pick 2 other random values
        allValues.remove(mainLetter);
        Collections.shuffle(allValues);
        letters.add(allValues.get(0));
        letters.add(allValues.get(1));
        allValues.add(mainLetter);

        // cycle through letters and assign to random button
        for (int i = 0; i < 3; i++) {
            Button b = buttons.get(getRand(buttons.size()));
            Letter l = letters.get(i);
            if (mainLetter == l) {
                mainButton = b;
            }
            b.setText(letters.get(i).getPronunciation());
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
        if (currLevel > maxLevel || values.size() <= 0) {
            Intent intent = new Intent(getApplicationContext(), GameOver.class);
            intent.putExtra("language_selection", languageSelection);
            intent.putExtra("wrong_count", wrongCount);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } else {
            generateStage();
        }
    }

    public void onButtonClick(Button b) {
        if (b.getText().toString() == mainLetter.getPronunciation()) {
            currLevel++;
            values.remove(mainLetter);
        } else {
            wrongCount++;
            b.setBackgroundResource(R.drawable.game_option_button_incorrect);
        }
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

    private void setupButton(Button b) {
        b.setEnabled(true);
        b.setBackgroundResource(R.drawable.game_option_button);
    }

    private int getRand(int max) {
        return (int) (max * Math.random());
    }
}
