package app.tvs.db;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import app.tvs.entities.Episode;
import app.tvs.entities.Season;
import app.tvs.entities.TVSeries;
import app.tvs.entities.TVSeriesShort;

@android.arch.persistence.room.Dao
public interface Dao {

    //TVSeries

    @Insert
    void addTVSeries(TVSeries tvseries);

    @Delete
    void deleteTVSeries(TVSeries tvSeries);

    @Query("select count(*) from TVSeries")
    int getNrOfTVSeries();

    @Query("select * from TVSeries")
    List<TVSeries> getTVSeries();

    @Query("select * from TVSeries where state = 2")
    List<TVSeries> getFinishedTVSeries();

    @Query("select * from TVSeries order by name asc")
    List<TVSeries> getTVSeriesSortedAscByName();

    @Query("select * from TVSeries where name like :toContain order by name asc")
    List<TVSeries> getTVSeriesSortedAscByNameThatContains(String toContain);

    @Query("select * from TVSeries order by name desc")
    List<TVSeries> getTVSeriesSortedDescByName();

    @Query("select * from TVSeries order by id desc")
    List<TVSeries> getTVSeriesDesc();

    @Query("select * from TVSeries order by IMDBRating asc")
    List<TVSeries> getTVSeriesSortedAscByIMDb();

    @Query("select * from TVSeries order by IMDBRating desc")
    List<TVSeries> getTVSeriesSortedDescByIMDb();

    @Query("select * from TVSeries order by seasonsSeen asc")
    List<TVSeries> getTVSeriesSortedAscBySeasonsSeen();

    @Query("select * from TVSeries order by seasonsSeen desc")
    List<TVSeries> getTVSeriesSortedDescBySeasonsSeen();

    @Query("select * from TVSeries order by episodesSeen asc")
    List<TVSeries> getTVSeriesSortedAscByEpisodesSeen();

    @Query("select * from TVSeries order by episodesSeen desc")
    List<TVSeries> getTVSeriesSortedDescByEpisodesSeen();

    @Query("select * from TVSeries order by state asc")
    List<TVSeries> getTVSeriesSortedByState();

    @Query("select * from TVSeries order by seenState asc")
    List<TVSeries> getTVSeriesSortedBySeenState();

    @Query("select * from TVSeries order by lastTimeEpisodeSeen desc")
    List<TVSeries> getTVSeriesSortedByLastTimeEpisodeSeen();

    @Query("select * from TVSeries order by lastTimeUpdated desc")
    List<TVSeries> getTVSeriesSortedByLastTimeUpdated();

    @Update
    void updateTVSeries(TVSeries tvSeries);

    @Update
    void updateTVSeriesList(List<TVSeries> tvSeries);

    @Query("select * from TVSeries where id = :position")
    TVSeries getTVSeriesWithId(int position);

    @Query("select TVSeries.* from TVSeries inner join Seasons where Seasons.id = :idSeason and TVSeries.id = Seasons.idTVSeries")
    TVSeries getTVSeriesBySeasonId(int idSeason);

    @Query("select * from TVSeries where IMDBLink = :url")
    TVSeries getTVSeriesWithUrl(String url);

    //Seasons

    @Insert
    void addSeason(Season season);

    @Delete
    void deleteSeason(Season season);

    @Query("select count(*) from Seasons")
    int getNrOfSeasons();

    @Query("select * from Seasons where idTVSeries = :idTVSeries")
    List<Season> getSeasonsWithIdTVSeries(int idTVSeries);

    @Query("select * from Seasons where idTVSeries = :idTVSeries order by `index` asc")
    List<Season> getSeasonsWithIdTVSeriesAscByIndex(int idTVSeries);

    @Update
    void updateSeason(Season season);

    @Update
    void updateSeasonsList(List<Season> seasons);

    @Query("select * from Seasons where id = :id")
    Season getSeasonById(int id);

    @Query("select `index` from Seasons where idTVSeries = :idTVSeries")
    List<Integer> getSeasonsIndexForTVSeries(int idTVSeries);

    @Query("select TVSeries.name from TVSeries inner join Seasons where Seasons.id = :idSeason AND TVSeries.id = Seasons.idTVSeries")
    String getNameOfTVSeriesForEpisodeWithIdSeason(int idSeason);

    @Query("select TVSeries.IMDBLink from TVSeries inner join Seasons where Seasons.id = :idSeason AND TVSeries.id = Seasons.idTVSeries")
    String getIMDBLinkOfTVSeriesForSeason(int idSeason);

    @Query("select * from Seasons where idTVSeries = :idTVSeries order by `index` desc limit 1")
    Season getLastSeasonOfTVSeriesWithId(int idTVSeries);

    @Query("select * from Seasons where `index` = :index AND idTVSeries = :idTVSeries")
    Season getSeasonForTVSeriesWithIndex(int idTVSeries, int index);

    //Episodes

    @Insert
    void addEpisodes(Episode episode);

    @Delete
    void deleteEpisode(Episode episode);

    @Query("select count(*) from Episodes")
    int getNrOFEpisodes();

    @Query("select * from episodes")
    List<Episode> getEpisodes();

    @Query("select count(*) from Episodes inner join Seasons where Seasons.idTVSeries = :idTVSeries and Episodes.idSeason = Seasons.id")
    int getNrEpisodesForTVSeries(int idTVSeries);

    @Update
    void updateEpisodesList(List<Episode> episodes);

    @Query("select `index` from Episodes where idSeason = :idSeason")
    List<Integer> getEpisodesIndexForIdSeason(int idSeason);

    @Query("select * from Episodes where idSeason = :idSeason order by `index` asc")
    List<Episode> getEpisodesWithIdSeasonAscByIndex(int idSeason);

    @Query("select * from Episodes where idSeason = :idSeason")
    List<Episode> getEpisodesWithIdSeason(int idSeason);

    @Query("select Episodes.timeAdded from Episodes inner join Seasons where Seasons.idTVSeries = :idTVSeries and Episodes.idSeason = Seasons.id order by Episodes.timeAdded desc limit 1")
    Long getLastTimeAddedEpisodeForTVSeriesWithId(int idTVSeries);

    //TVSeriesShort

    @Insert
    void addTVSeriesShort(TVSeriesShort tvSeriesShort);

    @Update
    void updateTVSeriesShort(TVSeriesShort tvSeriesShort);

    @Query("select count(*) from TVSeriesShort where id = :id")
    int getTVSeriesShortWithId(String id);

    @Query("select * from TVSeriesShort where name like :search order by name")
    List<TVSeriesShort> getTVSeriesShortLike(String search);

    @Query("select * from TVSeriesShort order by yearStart desc limit :limit")
    List<TVSeriesShort> getTVSeriesShortWithLimit(int limit);

    @Query("select * from Episodes where idSeason = :seasonId AND `index` = :index")
    Episode getEpisodeForSeasonWithIndex(int seasonId, int index);

}
