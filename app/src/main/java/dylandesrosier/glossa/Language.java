package dylandesrosier.glossa;

import java.util.HashMap;

public class Language {
    public class Letter {
        private Character character;
        private String pronunciation;
        private String category;

        public Letter(Character character, String pronunciation, String category){
            this.character = character;
            this.pronunciation = pronunciation;
            this.category = category;
        }

        public Character getCharacter() {
            return character;
        }

        public String getPronunciation() {
            return pronunciation;
        }

        public String getCategory() {
            return category;
        }
    }

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

    public Language () {
        letters = new HashMap<>();
        words = new HashMap<>();
    }

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
}
