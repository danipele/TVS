package app.tvs.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Seasons", foreignKeys = @ForeignKey(entity = TVSeries.class, parentColumns = "id", childColumns = "idTVSeries", onDelete = ForeignKey.CASCADE), indices = @Index(value = "idTVSeries", name = "TVSeriesIndex"))
public class Season {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private int index;

    @ColumnInfo
    private int startYear;

    @ColumnInfo
    private int endYear;

    @ColumnInfo
    private int nrEpisodes;

    @ColumnInfo
    private int nrTotalOfEpisodes;

    @ColumnInfo
    private int nrEpisodesSeen;

    @ColumnInfo
    private int idTVSeries;

    public Season(int index, int startYear, int endYear, int nrEpisodes, int nrTotalOfEpisodes, int nrEpisodesSeen, int idTVSeries) {
        this.index = index;
        this.startYear = startYear;
        this.endYear = endYear;
        this.nrEpisodes = nrEpisodes;
        this.nrTotalOfEpisodes = nrTotalOfEpisodes;
        this.nrEpisodesSeen = nrEpisodesSeen;
        this.idTVSeries = idTVSeries;
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

    public int getStartYear() {
        return startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public int getNrEpisodes() {
        return nrEpisodes;
    }

    public void setNrEpisodes(int nrEpisodes) {
        this.nrEpisodes = nrEpisodes;
    }

    public int getNrTotalOfEpisodes() {
        return nrTotalOfEpisodes;
    }

    public int getNrEpisodesSeen() {
        return nrEpisodesSeen;
    }

    public void setNrEpisodesSeen() {
        this.nrEpisodesSeen++;
    }

    public void removeNrEpisodesSeen(int deleted) {
        this.nrEpisodesSeen-=deleted;
    }

    public int getIdTVSeries() {
        return idTVSeries;
    }

    public void setNrTotalOfEpisodes(int nrTotalOfEpisodes) {
        this.nrTotalOfEpisodes = nrTotalOfEpisodes;
    }
}
