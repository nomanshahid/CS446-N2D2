package dylandesrosier.glossa.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Settings")
public class Settings {
        // TODO: Add columns
        @PrimaryKey(autoGenerate = true)
        public int uid;

        @ColumnInfo(name = "enable_time_notif")
        public boolean enable_time_notif;

        @ColumnInfo(name = "notif_start_time")
        public String start_time;

        @ColumnInfo(name = "notif_end_time")
        public String end_time;

        @ColumnInfo(name = "enable_location_notif")
        public boolean enable_location_notif;

        public Settings(boolean enable_time_notif, String start_time, String end_time, boolean enable_location_notif){
            this.enable_time_notif = enable_time_notif;
            this.start_time = start_time;
            this.end_time = end_time;
            this.enable_location_notif = enable_location_notif;
        }
}
