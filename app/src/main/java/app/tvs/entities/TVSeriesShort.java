package app.tvs.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "TVSeriesShort")
public class TVSeriesShort {

    @NonNull
    @PrimaryKey
    private String id;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String yearStart;

    @ColumnInfo
    private String yearEnd;

    public TVSeriesShort(@NonNull String id, String name, String yearStart, String yearEnd) {
        this.id = id;
        this.name = name;
        this.yearStart = yearStart;
        this.yearEnd = yearEnd;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYearStart() {
        return yearStart;
    }

    public String getYearEnd() {
        return yearEnd;
    }
}
