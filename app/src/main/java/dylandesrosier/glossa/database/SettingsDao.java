package dylandesrosier.glossa.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
