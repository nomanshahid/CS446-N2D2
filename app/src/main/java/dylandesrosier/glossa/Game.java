package dylandesrosier.glossa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.content.pm.ActivityInfo;
import android.os.*;
import android.view.View;
import android.widget.*;
import android.view.Window;
import android.view.WindowManager;
import java.util.*;

public class Game extends AppCompatActivity {
    private final int maxLevel = 10;
    private int currLevel = 1;
    private Letter mainLetter = null;
    private Button mainButton = null;
    private String languageSelection;

    ArrayList<Letter> values = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Korean korean = new Korean();
        ArrayList<Letter> allValues = new ArrayList<>(korean.getLetters().values());
        for (int i = 0; i < 10; i++) {
            int randomIdx = getRand(allValues.size());
            values.add(allValues.get(randomIdx));
            allValues.remove(randomIdx);
        }

        languageSelection = getIntent().getStringExtra("language_selection");
        TextView languageLabel = findViewById(R.id.game_language_label);
        languageLabel.setText(languageSelection);

        // Set the status bar to be transparent and lock app to portrait
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        generateStage();
    }

    private void generateStage() {
        TextView characterView = findViewById(R.id.game_character_text);
        final Button option1 = findViewById(R.id.btn_option_1);
        final Button option2 = findViewById(R.id.btn_option_2);
        final Button option3 = findViewById(R.id.btn_option_3);
        setupButton(option1);
        setupButton(option2);
        setupButton(option3);

        ArrayList<Button> buttons = new ArrayList<>(Arrays.asList(option1, option2, option3));
        ArrayList<Letter> letters = new ArrayList<>();

        mainLetter = values.get(getRand(values.size()));
        letters.add(mainLetter);
        values.remove(mainLetter);

        Letter randLetter1 = values.get(getRand(values.size()));
        values.remove(randLetter1);
        Letter randLetter2 = values.get(getRand(values.size()));
        values.add(randLetter1);

        letters.add(randLetter1);
        letters.add(randLetter2);

        characterView.setText(Character.toString(mainLetter.getCharacter()));
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
                    onButtonClick((Button)view);
                    option1.setEnabled(false);
                    option2.setEnabled(false);
                    option3.setEnabled(false);
                }
            });
            buttons.remove(b);
        }
    }

    private void determineNextStage() {
        if (currLevel > maxLevel || values.size() <= 0) {
            Intent intent = new Intent(getApplicationContext(), GameOver.class);
            intent.putExtra("language_selection", languageSelection);
            startActivity(intent);
        } else {
            currLevel++;
            generateStage();
        }
    }

    public void onButtonClick(Button b) {
        if (b.getText().toString() == mainLetter.getPronunciation()) {
            currLevel++;
        } else {
            values.add(mainLetter);
            b.setBackgroundResource(R.drawable.game_option_button_incorrect);
        }
        mainButton.setBackgroundResource(R.drawable.game_option_button_correct);
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
