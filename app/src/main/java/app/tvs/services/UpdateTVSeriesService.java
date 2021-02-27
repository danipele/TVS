package app.tvs.services;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.tvs.Global;
import app.tvs.db.Database;
import app.tvs.entities.Episode;
import app.tvs.entities.Season;
import app.tvs.entities.TVSeries;
import app.tvseries.R;

public class UpdateTVSeriesService {

    public static int updateIMDBRating(Context context, TVSeries tvSeries) throws Exception{
        Database database = Global.database = Room.databaseBuilder(context, Database.class, context.getString(R.string.DbName)).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        Matcher matcher;
        String htmlLine;
        boolean updated = false;

        Scanner TVSeriesScanner = new Scanner(new URL(tvSeries.getIMDBLink()).openStream());

        while (TVSeriesScanner.hasNext()) {
            htmlLine = TVSeriesScanner.nextLine();

            if (htmlLine.contains(context.getString(R.string.IMDbRatingFinder1)) && htmlLine.contains(context.getString(R.string.IMDbRatingFinder2))) {
                matcher = Pattern.compile(context.getString(R.string.IMDbRatingPattern)).matcher(htmlLine);
                if (matcher.find()) {
                    if (tvSeries.getIMDBRating() != Float.parseFloat(matcher.group(1))) {
                        tvSeries.setIMDBRating(Float.parseFloat(matcher.group(1)));
                        updated = true;
                    }
                }
                break;
            }
        }

        List<Season> seasonsList = database.dao().getSeasonsWithIdTVSeries(tvSeries.getId());
        for (Season season : seasonsList) {
            int episodeIndex;
            List<Episode> episodesList = database.dao().getEpisodesWithIdSeason(season.getId());
            Scanner seasonScanner = new Scanner(new URL(tvSeries.getIMDBLink() + context.getString(R.string.forSeasonLink) + season.getIndex()).openStream());

            while (seasonScanner.hasNext()) {
                htmlLine = seasonScanner.nextLine();
                if (htmlLine.contains(context.getString(R.string.episodeFinder))) {
                    if (!htmlLine.contains(context.getString(R.string.episode0FinderInSeason))) {
                        matcher = Pattern.compile(context.getString(R.string.episodeIndexMatcher)).matcher(htmlLine);
                        if (matcher.find()) {
                            episodeIndex = Integer.parseInt(matcher.group(1));
                            boolean unreleased = true;
                            htmlLine = seasonScanner.nextLine();
                            while (!htmlLine.contains(context.getString(R.string.searchUntil))) {
                                if (htmlLine.contains(context.getString(R.string.episodeItemFinder))) {
                                    htmlLine = seasonScanner.nextLine();
                                    if (!htmlLine.contains(context.getString(R.string.episodeItemEmptyPattern))) {
                                        unreleased = false;
                                        break;
                                    }
                                }
                                htmlLine = seasonScanner.nextLine();
                            }
                            if (!unreleased) {
                                for (Episode episode : episodesList) {
                                    if (episodeIndex == episode.getIndex()) {
                                        while (!htmlLine.contains(context.getString(R.string.IMDbRatingInSeasonFinder))) {
                                            htmlLine = seasonScanner.nextLine();
                                        }
                                        matcher = Pattern.compile(context.getString(R.string.IMDbRatingInSeasonPattern)).matcher(htmlLine);
                                        if (matcher.find()) {
                                            float IMDBRating = Float.parseFloat(matcher.group(1));
                                            if (episode.getIMDBRating() != IMDBRating) {
                                                episode.setIMDBRating(IMDBRating);
                                                updated = true;
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            database.dao().updateEpisodesList(episodesList);
        }
        database.dao().updateTVSeries(tvSeries);
        return updated ? 1 : 0;
    }

    public static boolean updateTVSeries(Context context, TVSeries tvSeries) throws Exception {
        Database database = Global.database = Room.databaseBuilder(context, Database.class, context.getString(R.string.DbName)).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        Matcher matcher;
        String htmlLine;
        boolean updated = false;

        int nrEpisodesToDelete = 0;
        int nrEpisodesFound = 0;
        int nrEpisodesFromLastSeasonToDelete = 0;
        int TVSeriesNrSeasons;
        int TVSeriesNrEpisodes = 0;
        Global.STATES state = Global.STATES.UNDECLARED;

        Scanner TVSeriesScanner = new Scanner(new URL(tvSeries.getIMDBLink()).openStream());

        while (TVSeriesScanner.hasNext()) {
            htmlLine = TVSeriesScanner.nextLine();
            if (htmlLine.contains(context.getString(R.string.imageLinkFinder))) {
                htmlLine = TVSeriesScanner.nextLine();
                matcher = Pattern.compile(context.getString(R.string.imageLinkPattern)).matcher(htmlLine);
                if (matcher.find()) {
                    Bitmap newBitmap = BitmapFactory.decodeStream(new URL(matcher.group(1)).openConnection().getInputStream());
                    if (!tvSeries.getBitmapImage().sameAs(newBitmap)) {
                        tvSeries.setBitmapImage(newBitmap);
                    }
                }
            }
            if (htmlLine.contains(context.getString(R.string.nrSeasonsFinder))) {
                while (!htmlLine.contains(context.getString(R.string.nrSeasonsFindingCond))) {
                    htmlLine = TVSeriesScanner.nextLine();
                }
                matcher = Pattern.compile(context.getString(R.string.nrSeasonPattern)).matcher(htmlLine);
                if (matcher.find()) {
                    TVSeriesNrSeasons = Integer.parseInt(matcher.group(1));
                    while (nrEpisodesFound == nrEpisodesToDelete) {
                        nrEpisodesFromLastSeasonToDelete = 0;
                        Scanner seasonScanner = new Scanner(new URL(tvSeries.getIMDBLink() + context.getString(R.string.forSeasonLink) + TVSeriesNrSeasons).openStream());
                        while (seasonScanner.hasNext()) {
                            htmlLine = seasonScanner.nextLine();
                            if (htmlLine.contains(context.getString(R.string.episodeItemIdOdd)) || htmlLine.contains(context.getString(R.string.episodeItemIdEven))) {
                                nrEpisodesFound++;
                                boolean unreleased = true;
                                htmlLine = seasonScanner.nextLine();
                                while (!htmlLine.contains(context.getString(R.string.searchUntil))) {
                                    if (htmlLine.contains(context.getString(R.string.episodeItemFinder))) {
                                        htmlLine = seasonScanner.nextLine();
                                        if (!htmlLine.contains(context.getString(R.string.episodeItemEmptyPattern))) {
                                            unreleased = false;
                                            break;
                                        }
                                    }
                                    htmlLine = seasonScanner.nextLine();
                                }
                                if (unreleased) {
                                    nrEpisodesFromLastSeasonToDelete++;
                                }
                            }
                        }
                        nrEpisodesToDelete += nrEpisodesFromLastSeasonToDelete;
                        if (nrEpisodesFound == nrEpisodesToDelete) {
                            TVSeriesNrSeasons--;
                        }
                    }
                    if (state == Global.STATES.UNDECLARED) {
                        state = (nrEpisodesFromLastSeasonToDelete == 0) ? (Global.STATES.IN_STAND_BY) : (Global.STATES.ON_GOING);
                    }
                    if (tvSeries.getNrSeasons() != TVSeriesNrSeasons) {
                        tvSeries.setNrSeasons(TVSeriesNrSeasons);
                        tvSeries.setLastTimeUpdated(new Date().getTime());
                        updated = true;
                    }
                }
                Scanner seasonUnknownScanner = new Scanner(new URL(tvSeries.getIMDBLink() + context.getString(R.string.forSeasonLink) + context.getString(R.string.minusOne)).openStream());
                while (!htmlLine.contains(context.getString(R.string.seasonUnknownFinder))) {
                    htmlLine = seasonUnknownScanner.nextLine();
                }
                if (htmlLine.contains(context.getString(R.string.seasonUnknownPattern))) {
                    while (seasonUnknownScanner.hasNext()) {
                        htmlLine = seasonUnknownScanner.nextLine();
                        if (htmlLine.contains(context.getString(R.string.episodeItemIdOdd)) || htmlLine.contains(context.getString(R.string.episodeItemIdEven))) {
                            nrEpisodesToDelete++;
                        }
                    }
                }
                break;
            }
            if (htmlLine.contains(context.getString(R.string.nrEpisodesFinder))) {
                htmlLine = TVSeriesScanner.nextLine();
                matcher = Pattern.compile(context.getString(R.string.nrEpisodesPattern)).matcher(htmlLine);
                if (matcher.find()) {
                    TVSeriesNrEpisodes = Integer.parseInt(matcher.group(1));
                }
            }
            if (htmlLine.contains(context.getString(R.string.IMDbRatingFinder1)) && htmlLine.contains(context.getString(R.string.IMDbRatingFinder2))) {
                matcher = Pattern.compile(context.getString(R.string.IMDbRatingPattern)).matcher(htmlLine);
                if (matcher.find()) {
                    if (tvSeries.getIMDBRating() != Float.parseFloat(matcher.group(1))) {
                        tvSeries.setIMDBRating(Float.parseFloat(matcher.group(1)));
                    }
                }
            }
            if (htmlLine.contains(context.getString(R.string.dateFinder))) {
                matcher = Pattern.compile(context.getString(R.string.datePattern)).matcher(htmlLine);
                if (matcher.find()) {
                    if (matcher.group(1).contains(context.getString(R.string.minusForYears))) {
                        String[] years = matcher.group(1).split(context.getString(R.string.minusForYears));
                        if (tvSeries.getStartYear() != Integer.parseInt(years[0])) {
                            tvSeries.setStartYear(Integer.parseInt(years[0]));
                            tvSeries.setLastTimeUpdated(new Date().getTime());
                            updated = true;
                        }
                        if (years[1].trim().isEmpty()) {
                            if (tvSeries.getEndYear() != Calendar.getInstance().get(Calendar.YEAR)) {
                                tvSeries.setEndYear(Calendar.getInstance().get(Calendar.YEAR));
                                tvSeries.setLastTimeUpdated(new Date().getTime());
                                updated = true;
                            }
                        } else {
                            if (tvSeries.getEndYear() != Integer.parseInt(years[1])) {
                                tvSeries.setEndYear(Integer.parseInt(years[1]));
                            }
                            tvSeries.setLastTimeUpdated(new Date().getTime());
                            state = Global.STATES.FINISHED;
                            updated = true;
                        }
                    } else {
                        if (tvSeries.getStartYear() != Integer.parseInt(matcher.group(1))) {
                            tvSeries.setStartYear(Integer.parseInt(matcher.group(1)));

                        }
                        if (tvSeries.getEndYear() != Integer.parseInt(matcher.group(1))) {
                            tvSeries.setEndYear(Integer.parseInt(matcher.group(1)));
                        }
                        tvSeries.setLastTimeUpdated(new Date().getTime());
                        state = Global.STATES.FINISHED;
                        updated = true;
                    }

                }
            }
        }
        TVSeriesNrEpisodes -= nrEpisodesToDelete;

        if (state == Global.STATES.FINISHED && nrEpisodesToDelete != 0) {
            state = Global.STATES.ON_GOING;
        }
        tvSeries.setState(state);

        List<Season> seasonsList = database.dao().getSeasonsWithIdTVSeries(tvSeries.getId());

        for (Season season : seasonsList) {
            int SeasonNrEpisodes = 0;
            int nrTotalOfEpisodes = 0;
            int episodeIndex;
            List<Episode> episodesList = database.dao().getEpisodesWithIdSeason(season.getId());
            Scanner seasonScanner = new Scanner(new URL(tvSeries.getIMDBLink() + context.getString(R.string.forSeasonLink) + season.getIndex()).openStream());

            while (seasonScanner.hasNext()) {
                htmlLine = seasonScanner.nextLine();
                if (htmlLine.contains(context.getString(R.string.episodeFinder))) {
                    if (!htmlLine.contains(context.getString(R.string.episode0FinderInSeason))) {
                        nrTotalOfEpisodes++;
                        matcher = Pattern.compile(context.getString(R.string.episodeIndexMatcher)).matcher(htmlLine);
                        if (matcher.find()) {
                            episodeIndex = Integer.parseInt(matcher.group(1));
                            boolean unreleased = true;
                            htmlLine = seasonScanner.nextLine();
                            while (!htmlLine.contains(context.getString(R.string.searchUntil))) {
                                if (htmlLine.contains(context.getString(R.string.episodeItemFinder))) {
                                    htmlLine = seasonScanner.nextLine();
                                    if (!htmlLine.contains(context.getString(R.string.episodeItemEmptyPattern))) {
                                        unreleased = false;
                                        break;
                                    }
                                }
                                htmlLine = seasonScanner.nextLine();
                            }
                            if (!unreleased) {
                                SeasonNrEpisodes++;
                                for (Episode episode : episodesList) {
                                    if (episodeIndex == episode.getIndex()) {
                                        while (!htmlLine.contains(context.getString(R.string.IMDbRatingInSeasonFinder))) {
                                            htmlLine = seasonScanner.nextLine();
                                        }
                                        matcher = Pattern.compile(context.getString(R.string.IMDbRatingInSeasonPattern)).matcher(htmlLine);
                                        if (matcher.find()) {
                                            episode.setIMDBRating(Float.parseFloat(matcher.group(1)));
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        TVSeriesNrEpisodes--;
                    }
                }
            }
            season.setNrEpisodes(SeasonNrEpisodes);
            season.setNrTotalOfEpisodes(nrTotalOfEpisodes);
            database.dao().updateEpisodesList(episodesList);
        }
        if (tvSeries.getNrEpisodes() != TVSeriesNrEpisodes) {
            tvSeries.setNrEpisodes(TVSeriesNrEpisodes);
            tvSeries.setLastTimeUpdated(new Date().getTime());
            updated = true;
        }
        database.dao().updateSeasonsList(seasonsList);
        tvSeries.setSeenState();
        database.dao().updateTVSeries(tvSeries);
        return updated;
    }

}
