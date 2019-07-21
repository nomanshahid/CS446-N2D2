package dylandesrosier.glossa.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "KoreanLetter")
public class KoreanLetter {
    @PrimaryKey
    @ColumnInfo(name = "letter")
    public Character letter;

    @ColumnInfo(name ="name_seen")
    public int num_seen;

    @ColumnInfo(name ="name_correct")
    public int num_correct;

    public KoreanLetter(Character letter, int num_seen, int num_correct) {
        this.letter = letter;
        this.num_seen = num_seen;
        this.num_correct = num_correct;
    }
}
