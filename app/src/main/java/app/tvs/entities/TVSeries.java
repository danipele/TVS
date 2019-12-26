package app.tvs.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

import java.util.List;

import app.tvs.Global;

@Entity(tableName = "TVSeries")
public class TVSeries {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String IMDBLink;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private int startYear;

    @ColumnInfo
    private int endYear;

    @ColumnInfo
    private int nrSeasons;

    @ColumnInfo
    private int nrEpisodes;

    @ColumnInfo
    private int startYearSeen;

    @ColumnInfo
    private int endYearSeen;

    @ColumnInfo
    private int seasonsSeen;

    @ColumnInfo
    private int episodesSeen;

    @ColumnInfo
    private double IMDBRating;

    @ColumnInfo
    private Global.STATES state;

    @ColumnInfo
    private Global.SEENSTATES seenState;

    @ColumnInfo
    private Bitmap bitmapImage;

    @ColumnInfo
    private long lastTimeUpdated;

    @ColumnInfo
    private long lastTimeEpisodeSeen;

    public TVSeries(String IMDBLink, String name, int startYear, int endYear, int nrSeasons, int nrEpisodes, int startYearSeen, int endYearSeen, int seasonsSeen, int episodesSeen, double IMDBRating, Global.STATES state, Global.SEENSTATES seenState, Bitmap bitmapImage, long lastTimeUpdated, long lastTimeEpisodeSeen) {
        this.IMDBLink = IMDBLink;
        this.name = name;
        this.startYear = startYear;
        this.endYear = endYear;
        this.nrSeasons = nrSeasons;
        this.nrEpisodes = nrEpisodes;
        this.startYearSeen = startYearSeen;
        this.endYearSeen = endYearSeen;
        this.seasonsSeen = seasonsSeen;
        this.episodesSeen = episodesSeen;
        this.IMDBRating = IMDBRating;
        this.state = state;
        this.seenState = seenState;
        this.bitmapImage = bitmapImage;
        this.lastTimeUpdated = lastTimeUpdated;
        this.lastTimeEpisodeSeen = lastTimeEpisodeSeen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIMDBLink() {
        return IMDBLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getNrSeasons() {
        return nrSeasons;
    }

    public void setNrSeasons(int nrSeasons) {
        this.nrSeasons = nrSeasons;
    }

    public int getNrEpisodes() {
        return nrEpisodes;
    }

    public void setNrEpisodes(int nrEpisodes) {
        this.nrEpisodes = nrEpisodes;
    }

    public void removeEpisodes(){
        this.nrEpisodes--;
    }

    public int getStartYearSeen() {
        return startYearSeen;
    }

    public void setStartYearSeen(int startYearSeen) {
        this.startYearSeen = startYearSeen;
    }

    public int getEndYearSeen() {
        return endYearSeen;
    }

    public void setEndYearSeen(int endYearSeen) {
        this.endYearSeen = endYearSeen;
    }

    public int getSeasonsSeen() {
        return seasonsSeen;
    }

    public void setSeasonsSeen() {
        this.seasonsSeen++;
    }

    public void removeSeasonsSeen(int deleted) {
        this.seasonsSeen-=deleted;
    }

    public int getEpisodesSeen() {
        return episodesSeen;
    }

    public void setEpisodesSeen() {
        this.episodesSeen++;
    }

    public void removeEpisodesSeen(int deleted) {
        this.episodesSeen-=deleted;
    }

    public double getIMDBRating() {
        return IMDBRating;
    }

    public void setIMDBRating(double IMDBRating) {
        this.IMDBRating = IMDBRating;
    }

    public Global.STATES getState() {
        return state;
    }

    public void setState(Global.STATES state) {
        this.state = state;
    }

    public long getLastTimeUpdated() {
        return lastTimeUpdated;
    }

    public void setLastTimeUpdated(long lastTimeUpdated) {
        this.lastTimeUpdated = lastTimeUpdated;
    }

    public long getLastTimeEpisodeSeen() {
        return lastTimeEpisodeSeen;
    }

    public void setLastTimeEpisodeSeen(long lastTimeEpisodeSeen) {
        this.lastTimeEpisodeSeen = lastTimeEpisodeSeen;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }

    public void updateYearsSeen() {
        List<Season> seasonsList = Global.database.dao().getSeasonsWithIdTVSeries(id);
        if(seasonsList.size() > 0) {
            int startYearMin = Integer.MAX_VALUE;
            int endYearMax = 0;
            for (Season season : seasonsList) {
                if (season.getStartYear() < startYearMin)
                    startYearMin = season.getStartYear();
                if (season.getEndYear() > endYearMax)
                    endYearMax = season.getEndYear();
            }
            this.startYearSeen = startYearMin;
            this.endYearSeen = endYearMax;
        }
        else {
            this.startYearSeen = 0;
            this.endYearSeen = 0;
        }
    }

    public Global.SEENSTATES getSeenState() {
        return seenState;
    }

    public void setSeenState() {
        if(nrEpisodes == episodesSeen) {
            seenState = Global.SEENSTATES.STOP;
        }
        else {
            if(seasonsSeen == 0) {
                seenState = Global.SEENSTATES.PAUSE;
            }
            else {
                Season lastSeason = Global.database.dao().getLastSeasonOfTVSeriesWithId(id);
                if (lastSeason.getNrEpisodes() == lastSeason.getNrEpisodesSeen())
                    seenState = Global.SEENSTATES.PAUSE;
                else
                    seenState = Global.SEENSTATES.PLAY;
            }
        }
    }
}
