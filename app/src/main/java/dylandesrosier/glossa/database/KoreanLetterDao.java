package dylandesrosier.glossa.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
