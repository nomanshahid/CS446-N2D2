package dylandesrosier.glossa;

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
