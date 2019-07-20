package dylandesrosier.glossa;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;

public class ConsonantVowelHelper implements LetterCombinationStrategy {

    private int baseUnicode;
    private Context context;

    public ConsonantVowelHelper(Context context, int baseUnicode) {
        this.context = context;
        this.baseUnicode = baseUnicode;
    }

    public Letter getChar(Letter consonant, Letter vowel) {
        // Character
        char c = (char) (((int)consonant.getCharacter() * 21 + (int)vowel.getCharacter()) + baseUnicode);

        // Pronunciation
        String consPronunciation = consonant.getCharacter() == 'ㅇ' ? "" : consonant.getPronunciation().substring(0, 1);
        String p = consPronunciation + vowel.getPronunciation();
        String type = context.getString(R.string.combined_text);

        return new Letter(c, p, type);
    }
}
