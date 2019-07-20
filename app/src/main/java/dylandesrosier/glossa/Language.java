package dylandesrosier.glossa;

import android.content.Context;
import java.util.*;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Language implements Serializable {
    public class Word {
        private String word;
        private String pronunciation;
        private String definition;

        public Word(String word, String pronunciation, String definition){
            this.word = word;
            this.pronunciation = pronunciation;
            this.definition = definition;
        }

        public String getWord() {
            return word;
        }

        public String getPronunciation() {
            return pronunciation;
        }

        public String getDefinition() {
            return definition;
        }
    }

    protected HashMap<Character, Letter> letters;
    protected HashMap<String, Word> words;
    protected Context context;

    protected LetterCombinationStrategy combinationStrategy;

    protected ArrayList<Letter> consonants;
    protected ArrayList<Letter> vowels;
    protected ArrayList<Letter> gameLetters;

    public Language (Context context) {
        context = context;
        letters = new HashMap<>();
        words = new HashMap<>();
    }

    // getters
    public HashMap<Character, Letter> getLetters() {
        return letters;
    }

    public HashMap<String, Word> getWords() {
        return words;
    }

    public Letter getLetter(Character letter){
        return letters.get(letter);
    }

    public Word getWord(String word){
        return words.get(word);
    }

    public ArrayList<Letter> getGameLetters(int limit) { return new ArrayList<>(); };
    public ArrayList<Letter> getAllGameLetters() { return new ArrayList<>(); };
}
