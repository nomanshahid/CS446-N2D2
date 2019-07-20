package dylandesrosier.glossa;

import java.util.*;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Language implements Serializable {
    protected HashMap<Character, Letter> letters;

    protected LetterCombinationStrategy combinationStrategy;

    protected ArrayList<Letter> consonants;
    protected ArrayList<Letter> vowels;
    protected ArrayList<Letter> gameLetters;
    protected ArrayList<Letter> allGameLetters;

    public Language () {
        letters = new HashMap<>();
    }

    // getters
    public HashMap<Character, Letter> getLetters() {
        return letters;
    }

    public ArrayList<Letter> getGameLetters(int limit) { return new ArrayList<>(); };
    public ArrayList<Letter> getAllGameLetters() { return new ArrayList<>(); };
}
