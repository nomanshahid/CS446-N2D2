package dylandesrosier.glossa;

import java.io.Serializable;

public class Letter implements Serializable {
    private Character character;
    private String pronunciation;
    private String category;
    private int index;

    public Letter(Character character, String pronunciation, int index, String category){
        this.character = character;
        this.pronunciation = pronunciation;
        this.category = category;
        this.index = index;
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

    public int getIndex() {
        return index;
    }
}
