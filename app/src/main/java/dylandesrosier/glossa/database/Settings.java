package dylandesrosier.glossa.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Settings")
public class Settings {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    // Can be NONE, TIME, or LOCATION
    @ColumnInfo(name = "notif_type")
    public String notif_type;

    @ColumnInfo(name = "notif_start_time")
    public String start_time;

    @ColumnInfo(name = "notif_end_time")
    public String end_time;

    @ColumnInfo(name = "longitude")
    public Double longitude;

    @ColumnInfo(name = "latitude")
    public Double latitude;

    public Settings(String notif_type, String start_time, String end_time, Double longitude, Double latitude) {
        this.notif_type = notif_type;
        this.start_time = start_time;
        this.end_time = end_time;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
