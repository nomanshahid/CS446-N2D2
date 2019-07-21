package dylandesrosier.glossa.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SettingsDao {
    @Query("SELECT * FROM settings")
    List<Settings> getSettings();

    @Query("DELETE FROM settings")
    void deleteAllSettings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSettings(Settings settings);

    // Returns the number of rows updated
    @Update
    int updateSettings(Settings settings);

    @Delete
    void deleteSettings(Settings settings);
}
