package dylandesrosier.glossa;

import java.io.Serializable;

public class ConsonantVowelHelper implements LetterCombinationStrategy, Serializable {

    private int baseUnicode;

    public ConsonantVowelHelper(int baseUnicode) {
        this.baseUnicode = baseUnicode;
    }

    public Letter getChar(Letter consonant, Letter vowel) {
        // Character
        char c = (char) ((consonant.getIndex() * 21 + vowel.getIndex()) * 28 + baseUnicode);

        // Pronunciation
        String consPronunciation = consonant.getCharacter() == 'ㅇ' ? "" : consonant.getPronunciation().substring(0, consonant.getPronunciation().length() - 1);
        String p = consPronunciation + vowel.getPronunciation();
        String type = "combined";

        return new Letter(c, p, 0, type);
    }
}
