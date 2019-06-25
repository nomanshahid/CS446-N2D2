package dylandesrosier.glossa;

import android.content.Context;

public class Korean extends Language {

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
        letters.put('ㅏ', new Letter('ㅏ', "a", this.context.getString(R.string.vowel_text)));
        letters.put('ㅑ', new Letter('ㅑ', "ya", this.context.getString(R.string.vowel_text)));
        letters.put('ㅓ', new Letter('ㅓ', "uh", this.context.getString(R.string.vowel_text)));
        letters.put('ㅕ', new Letter('ㅕ', "yu", this.context.getString(R.string.vowel_text)));
        letters.put('ㅗ', new Letter('ㅗ', "o", this.context.getString(R.string.vowel_text)));
        letters.put('ㅛ', new Letter('ㅛ', "yo", this.context.getString(R.string.vowel_text)));
        letters.put('ㅜ', new Letter('ㅜ', "oo", this.context.getString(R.string.vowel_text)));
        letters.put('ㅠ', new Letter('ㅠ', "yoo", this.context.getString(R.string.vowel_text)));
        letters.put('ㅡ', new Letter('ㅡ', "eu", this.context.getString(R.string.vowel_text)));
        letters.put('ㅣ', new Letter('ㅣ', "i", this.context.getString(R.string.vowel_text)));
    }
}
