package dylandesrosier.glossa;

import java.util.HashMap;

public class Korean extends Language {

    public Korean(){
        super();
        letters.put('ㄱ', new Letter('ㄱ', "ga", "Consonant"));
        letters.put('ㄴ', new Letter('ㄴ', "na", "Consonant"));
        letters.put('ㄷ', new Letter('ㄷ', "da", "Consonant"));
        letters.put('ㄹ', new Letter('ㄹ', "la", "Consonant"));
        letters.put('ㅁ', new Letter('ㅁ', "ma", "Consonant"));
        letters.put('ㅂ', new Letter('ㅂ', "ba", "Consonant"));
        letters.put('ㅅ', new Letter('ㅅ', "sa", "Consonant"));
        letters.put('ㅇ', new Letter('ㅇ', "ng", "Consonant"));
        letters.put('ㅈ', new Letter('ㅈ', "ja", "Consonant"));
        letters.put('ㅊ', new Letter('ㅊ', "cha", "Consonant"));
        letters.put('ㅋ', new Letter('ㅋ', "ka", "Consonant"));
        letters.put('ㅌ', new Letter('ㅌ', "ta", "Consonant"));
        letters.put('ㅍ', new Letter('ㅍ', "pa", "Consonant"));
        letters.put('ㅎ', new Letter('ㅎ', "ha", "Consonant"));
    }
}
