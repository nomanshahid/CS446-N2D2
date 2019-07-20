package dylandesrosier.glossa;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;

public class Korean extends Language {

    private int internalLevel = 0;

    public Korean(Context context){
        super(context);
        letters.put('ㄱ', new Letter('ㄱ', "ga", this.context.getString(R.string.consonant_text)));
        letters.put('ㄴ', new Letter('ㄴ', "na", this.context.getString(R.string.consonant_text)));
        letters.put('ㄷ', new Letter('ㄷ', "da", this.context.getString(R.string.consonant_text)));
        letters.put('ㄹ', new Letter('ㄹ', "la", this.context.getString(R.string.consonant_text)));
        letters.put('ㅁ', new Letter('ㅁ', "ma", this.context.getString(R.string.consonant_text)));
        letters.put('ㅂ', new Letter('ㅂ', "ba", this.context.getString(R.string.consonant_text)));
        letters.put('ㅅ', new Letter('ㅅ', "sa", this.context.getString(R.string.consonant_text)));
        letters.put('ㅇ', new Letter('ㅇ', "ah", this.context.getString(R.string.consonant_text)));
        letters.put('ㅈ', new Letter('ㅈ', "ja", this.context.getString(R.string.consonant_text)));
        letters.put('ㅊ', new Letter('ㅊ', "cha", this.context.getString(R.string.consonant_text)));
        letters.put('ㅋ', new Letter('ㅋ', "ka", this.context.getString(R.string.consonant_text)));
        letters.put('ㅌ', new Letter('ㅌ', "ta", this.context.getString(R.string.consonant_text)));
        letters.put('ㅍ', new Letter('ㅍ', "pa", this.context.getString(R.string.consonant_text)));
        letters.put('ㅎ', new Letter('ㅎ', "ha", this.context.getString(R.string.consonant_text)));
        letters.put('ㄲ', new Letter('ㄲ', "ha", this.context.getString(R.string.consonant_text)));
        letters.put('ㄸ', new Letter('ㄸ', "ha", this.context.getString(R.string.consonant_text)));
        letters.put('ㅆ', new Letter('ㅃ', "ha", this.context.getString(R.string.consonant_text)));
        letters.put('ㅃ', new Letter('ㅆ', "ha", this.context.getString(R.string.consonant_text)));
        letters.put('ㅉ', new Letter('ㅉ', "ha", this.context.getString(R.string.consonant_text)));

        letters.put('ㅏ', new Letter('ㅏ', "ah", this.context.getString(R.string.vowel_text)));
        letters.put('ㅑ', new Letter('ㅑ', "ya", this.context.getString(R.string.vowel_text)));
        letters.put('ㅓ', new Letter('ㅓ', "uh", this.context.getString(R.string.vowel_text)));
        letters.put('ㅕ', new Letter('ㅕ', "yuh", this.context.getString(R.string.vowel_text)));
        letters.put('ㅗ', new Letter('ㅗ', "oh", this.context.getString(R.string.vowel_text)));
        letters.put('ㅛ', new Letter('ㅛ', "yo", this.context.getString(R.string.vowel_text)));
        letters.put('ㅜ', new Letter('ㅜ', "oo", this.context.getString(R.string.vowel_text)));
        letters.put('ㅠ', new Letter('ㅠ', "yoo", this.context.getString(R.string.vowel_text)));
        letters.put('ㅡ', new Letter('ㅡ', "eu", this.context.getString(R.string.vowel_text)));
        letters.put('ㅣ', new Letter('ㅣ', "ee", this.context.getString(R.string.vowel_text)));
        letters.put('ㅐ', new Letter('ㅐ', "eh", this.context.getString(R.string.vowel_text)));
        letters.put('ㅒ', new Letter('ㅒ', "yeh", this.context.getString(R.string.vowel_text)));
        letters.put('ㅔ', new Letter('ㅔ', "eh", this.context.getString(R.string.vowel_text)));
        letters.put('ㅖ', new Letter('ㅖ', "yeh", this.context.getString(R.string.vowel_text)));
        letters.put('ㅘ', new Letter('ㅘ', "wah", this.context.getString(R.string.vowel_text)));
        letters.put('ㅙ', new Letter('ㅙ', "weh", this.context.getString(R.string.vowel_text)));
        letters.put('ㅚ', new Letter('ㅚ', "weh", this.context.getString(R.string.vowel_text)));
        letters.put('ㅝ', new Letter('ㅝ', "wuh", this.context.getString(R.string.vowel_text)));
        letters.put('ㅞ', new Letter('ㅞ', "weh", this.context.getString(R.string.vowel_text)));
        letters.put('ㅟ', new Letter('ㅟ', "we", this.context.getString(R.string.vowel_text)));
        letters.put('ㅢ', new Letter('ㅢ', "eh", this.context.getString(R.string.vowel_text)));

        combinationStrategy = new ConsonantVowelHelper(context, 44032);
        consonants = filterMap(this.context.getString(R.string.consonant_text));
        vowels = filterMap(this.context.getString(R.string.vowel_text));
    }

    private ArrayList<Letter> filterMap(String category) {
        ArrayList<Letter> ret = new ArrayList<>();
        for (Letter l: letters.values()) {
            if (l.getCategory().equals(category)) {
                ret.add(l);
            }
        }
        return ret;
    }

    @Override
    public ArrayList<Letter> getGameLetters(int limit) {
        if (gameLetters.size() < limit) {
            gameLetters.clear();
            internalLevel++;
            generateGameLetters();
        }
        int startIdx = gameLetters.size() - 1;
        int endIdx = gameLetters.size() - limit;

        ArrayList<Letter> retLetters = new ArrayList<>();
        for (int i = startIdx; i >= endIdx; i--) {
            retLetters.add(gameLetters.get(i));
            retLetters.remove(i);
        }
        return retLetters;
    }

    @Override
    public ArrayList<Letter> getAllGameLetters() {
        return (ArrayList<Letter>) gameLetters.clone();
    }

    private void generateGameLetters() {
        if (internalLevel == 0) {
            gameLetters = new ArrayList<>(letters.values());
            gameLetters = (ArrayList<Letter>) gameLetters.subList(0, 5);
        } else {
            for (Letter c: consonants) {
                for (Letter v: vowels) {
                    gameLetters.add(combinationStrategy.getChar(c, v));
                }
            }
        }
        Collections.shuffle(gameLetters);
    }
}
