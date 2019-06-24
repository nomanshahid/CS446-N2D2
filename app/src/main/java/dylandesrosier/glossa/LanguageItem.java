package dylandesrosier.glossa;

public class LanguageItem {
    private String languageName;
    private int languageImage;

    public LanguageItem(String languageName, int languageImage){
        this.languageName = languageName;
        this.languageImage = languageImage;
    }

    public String getLanguageName(){
        return this.languageName;
    }

    public int getLanguageImage(){
        return this.languageImage;
    }
}
