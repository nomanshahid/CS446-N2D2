package dylandesrosier.glossa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Korean extends Language {

    private int internalLevel = 0;

    public Korean(){
        letters.put('ㄱ', new Letter('ㄱ', "ga", 0,"Consonant"));
        letters.put('ㄲ', new Letter('ㄲ', "gga", 1,"Consonant"));
        letters.put('ㄴ', new Letter('ㄴ', "na",2, "Consonant"));
        letters.put('ㄷ', new Letter('ㄷ', "da",3, "Consonant"));
        letters.put('ㄸ', new Letter('ㄸ', "dda",4, "Consonant"));
        letters.put('ㄹ', new Letter('ㄹ', "la", 5,"Consonant"));
        letters.put('ㅁ', new Letter('ㅁ', "ma",6, "Consonant"));
        letters.put('ㅂ', new Letter('ㅂ', "ba",7, "Consonant"));
        letters.put('ㅃ', new Letter('ㅃ', "bba",8, "Consonant"));
        letters.put('ㅅ', new Letter('ㅅ', "sa", 9,"Consonant"));
        letters.put('ㅆ', new Letter('ㅆ', "ssa",10, "Consonant"));
        letters.put('ㅇ', new Letter('ㅇ', "ah",11, "Consonant"));
        letters.put('ㅈ', new Letter('ㅈ', "ja",12, "Consonant"));
        letters.put('ㅉ', new Letter('ㅉ', "jja",13, "Consonant"));
        letters.put('ㅊ', new Letter('ㅊ', "cha",14, "Consonant"));
        letters.put('ㅋ', new Letter('ㅋ', "ka", 15,"Consonant"));
        letters.put('ㅌ', new Letter('ㅌ', "ta",16, "Consonant"));
        letters.put('ㅍ', new Letter('ㅍ', "pa",17, "Consonant"));
        letters.put('ㅎ', new Letter('ㅎ', "ha", 18, "Consonant"));

        letters.put('ㅏ', new Letter('ㅏ', "ah", 0,"Vowel"));
        letters.put('ㅐ', new Letter('ㅐ', "eh", 1,"Vowel"));
        letters.put('ㅑ', new Letter('ㅑ', "ya", 2,"Vowel"));
        letters.put('ㅒ', new Letter('ㅒ', "yeh",3, "Vowel"));
        letters.put('ㅓ', new Letter('ㅓ', "uh",4, "Vowel"));
        letters.put('ㅔ', new Letter('ㅔ', "eh", 5,"Vowel"));
        letters.put('ㅕ', new Letter('ㅕ', "yuh",6, "Vowel"));
        letters.put('ㅖ', new Letter('ㅖ', "yeh",7, "Vowel"));
        letters.put('ㅗ', new Letter('ㅗ', "oh",8, "Vowel"));
        letters.put('ㅘ', new Letter('ㅘ', "wah",9, "Vowel"));
        letters.put('ㅙ', new Letter('ㅙ', "weh", 10,"Vowel"));
        letters.put('ㅚ', new Letter('ㅚ', "weh",11, "Vowel"));
        letters.put('ㅛ', new Letter('ㅛ', "yo",12, "Vowel"));
        letters.put('ㅜ', new Letter('ㅜ', "oo",13, "Vowel"));
        letters.put('ㅝ', new Letter('ㅝ', "wuh",14, "Vowel"));
        letters.put('ㅞ', new Letter('ㅞ', "weh",15, "Vowel"));
        letters.put('ㅟ', new Letter('ㅟ', "we",16, "Vowel"));
        letters.put('ㅠ', new Letter('ㅠ', "yoo",17, "Vowel"));
        letters.put('ㅡ', new Letter('ㅡ', "eu",18, "Vowel"));
        letters.put('ㅢ', new Letter('ㅢ', "eh",19, "Vowel"));
        letters.put('ㅣ', new Letter('ㅣ', "ee",20, "Vowel"));

        combinationStrategy = new ConsonantVowelHelper(44032);
        consonants = filterMap("Consonant");
        vowels = filterMap("Vowel");
        gameLetters = new ArrayList<>();

        generateGameLetters();
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
            gameLetters.remove(i);
        }
        return retLetters;
    }

    @Override
    public ArrayList<Letter> getAllGameLetters() {
        return (ArrayList<Letter>) allGameLetters.clone();
    }

    @Override
    public Letter getFirstGameLetter() {
        return gameLetters.get(gameLetters.size() - 1);
    }

    @Override
    public ArrayList<Letter> getOptionLetters(Letter exclude) {
        allGameLetters.remove(exclude);

        Collections.shuffle(allGameLetters);
        ArrayList<Letter> otherOptions = new ArrayList<>(Arrays.asList(allGameLetters.get(0), allGameLetters.get(1)));

        allGameLetters.add(exclude);
        return otherOptions;
    }

    private void generateGameLetters() {
        if (internalLevel == 0) {
            gameLetters = new ArrayList<>(letters.values());
        } else {
            for (Letter c: consonants) {
                for (Letter v: vowels) {
                    Letter x = combinationStrategy.getChar(c, v);
                    gameLetters.add(x);
                }
            }
        }
        Collections.shuffle(gameLetters);
        allGameLetters = (ArrayList<Letter>) gameLetters.clone();
    }
}
