package dylandesrosier.glossa.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface KoreanLetterDao {
    @Query("SELECT * FROM koreanletter")
    List<KoreanLetter> getLetters();

    @Query("SELECT * FROM koreanletter WHERE letter = :letter")
    KoreanLetter getLetter(Character letter);

    @Query("DELETE FROM koreanletter")
    void deleteAllLetters();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateLetter(KoreanLetter letter);

    @Delete
    void deleteLetter(KoreanLetter letter);
}
