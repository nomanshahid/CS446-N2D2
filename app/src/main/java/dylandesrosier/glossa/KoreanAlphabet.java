package dylandesrosier.glossa;

import java.util.HashMap;

public class KoreanAlphabet {
    private class Letter {
        private Character character;
        private String pronounciation;
        private String category;

        public Letter(Character character, String pronunciation, String category){
            this.character = character;
            this.pronounciation = pronunciation;
            this.category = category;
        }

        public Character getCharacter() {
            return character;
        }

        public String getPronounciation() {
            return pronounciation;
        }

        public String getCategory() {
            return category;
        }
    }

    private HashMap<Character, Letter> letters;

    public KoreanAlphabet(){
        letters.put('ㄱ', new Letter('ㄱ', "ga", "Consonant"));
        letters.put('ㄴ', new Letter('ㄴ', "na", "Consonant"));
        letters.put('ㄷ', new Letter('ㄷ', "da", "Consonant"));
        letters.put('ㄹ', new Letter('ㄹ', "la", "Consonant"));

        letters.put('ㅁ', new Letter('ㅁ', "la", "Consonant"));
        letters.put('ㅂ', new Letter('ㅂ', "la", "Consonant"));
        letters.put('ㅅ', new Letter('ㅅ', "la", "Consonant"));
        letters.put('ㅇ', new Letter('ㅇ', "la", "Consonant"));
        letters.put('ㅈ', new Letter('ㅈ', "la", "Consonant"));
        letters.put('ㅊ', new Letter('ㅊ', "la", "Consonant"));
        letters.put('ㅋ', new Letter('ㅋ', "la", "Consonant"));
        letters.put('ㅌ', new Letter('ㅌ', "la", "Consonant"));
        letters.put('ㅍ', new Letter('ㅍ', "la", "Consonant"));
        letters.put('ㅎ', new Letter('ㅎ', "la", "Consonant"));
    }
}
