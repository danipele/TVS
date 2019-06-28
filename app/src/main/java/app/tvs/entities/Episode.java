package app.tvs.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

@Entity(tableName = "Episodes", foreignKeys = @ForeignKey(entity = Season.class, parentColumns = "id", childColumns = "idSeason", onDelete = ForeignKey.CASCADE), indices = @Index(value = "idSeason", name = "SeasonIndex"))
public class Episode {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private int index;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String releaseDate;

    @ColumnInfo
    private String duration;

    @ColumnInfo
    private String description;

    @ColumnInfo
    private double IMDBRating;

    @ColumnInfo
    private Bitmap bitmapImage;

    @ColumnInfo
    private int idSeason;

    @ColumnInfo
    private long timeAdded;

    public Episode(int index, String name, String releaseDate, String duration, String description, double IMDBRating, Bitmap bitmapImage, int idSeason, long timeAdded) {
        this.index = index;
        this.name = name;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.description = description;
        this.IMDBRating = IMDBRating;
        this.bitmapImage = bitmapImage;
        this.idSeason = idSeason;
        this.timeAdded = timeAdded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getIMDBRating() {
        return IMDBRating;
    }

    public void setIMDBRating(double IMDBRating) {
        this.IMDBRating = IMDBRating;
    }

    public int getIdSeason() {
        return idSeason;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public long getTimeAdded() {
        return timeAdded;
    }
}
