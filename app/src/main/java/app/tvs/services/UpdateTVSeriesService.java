package app.tvs.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.tvs.Global;
import app.tvseries.R;
import app.tvs.db.Database;
import app.tvs.entities.Episode;
import app.tvs.entities.Season;
import app.tvs.entities.TVSeries;

public class UpdateTVSeriesService extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    private Runnable updateTVSeriesRunnable = new Runnable() {
        @Override
        public void run() {
            String message = "";
            try {
                Database database = Global.database;
                List<TVSeries> TVSeriesList = database.dao().getTVSeries();
                Matcher matcher;
                String htmlLine;
                boolean[] updated = new boolean[TVSeriesList.size()];
                int updatedIndex = 0;

                for (TVSeries tvSeries : TVSeriesList) {
                    if (tvSeries.getState() != Global.STATES.FINISHED) {
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
                                    if(!tvSeries.getBitmapImage().sameAs(newBitmap)) {
                                        tvSeries.setBitmapImage(newBitmap);
                                    }
                                }
                            }
                            if (htmlLine.contains(context.getString(R.string.nrSeasonsFinder))) {
                                while (!htmlLine.contains(context.getString(R.string.nrSeasonsFindingCond)))
                                    htmlLine = TVSeriesScanner.nextLine();
                                matcher = Pattern.compile(context.getString(R.string.nrSeasonPattern)).matcher(htmlLine);
                                if (matcher.find()) {
                                    TVSeriesNrSeasons = Integer.parseInt(matcher.group(1));
                                    while (nrEpisodesFound == nrEpisodesToDelete) {
                                        nrEpisodesFromLastSeasonToDelete = 0;
                                        Scanner seasonScanner = new Scanner(new URL(tvSeries.getIMDBLink() + context.getString(R.string.forSeasonLink) + TVSeriesNrSeasons).openStream());
                                        while (seasonScanner.hasNext()) {
                                            htmlLine = seasonScanner.nextLine();
                                            if(htmlLine.contains(context.getString(R.string.episodeItemIdOdd)) || htmlLine.contains(context.getString(R.string.episodeItemIdEven))) {
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
                                        nrEpisodesToDelete+=nrEpisodesFromLastSeasonToDelete;
                                        if (nrEpisodesFound == nrEpisodesToDelete)
                                            TVSeriesNrSeasons--;
                                    }
                                    if(state == Global.STATES.UNDECLARED)
                                        state = (nrEpisodesFromLastSeasonToDelete == 0)?(Global.STATES.IN_STAND_BY):(Global.STATES.ON_GOING);
                                    if(tvSeries.getNrSeasons() != TVSeriesNrSeasons) {
                                        tvSeries.setNrSeasons(TVSeriesNrSeasons);
                                        tvSeries.setLastTimeUpdated(new Date().getTime());
                                        updated[updatedIndex] = true;
                                    }
                                }
                                Scanner seasonUnknownScanner = new Scanner(new URL(tvSeries.getIMDBLink() + context.getString(R.string.forSeasonLink)+"-1").openStream());
                                while(seasonUnknownScanner.hasNext()) {
                                    htmlLine = seasonUnknownScanner.nextLine();
                                    if(htmlLine.contains(context.getString(R.string.seasonUnknownFinder))) {
                                        if(htmlLine.contains(context.getString(R.string.seasonUnknownPattern))) {
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
                                    if(tvSeries.getIMDBRating() != Float.parseFloat(matcher.group(1))) {
                                        tvSeries.setIMDBRating(Float.parseFloat(matcher.group(1)));
                                    }
                                }
                            }
                            if (htmlLine.contains(context.getString(R.string.dateFinder))) {
                                matcher = Pattern.compile(context.getString(R.string.datePattern)).matcher(htmlLine);
                                if (matcher.find()) {
                                    if(matcher.group(1).contains("–")) {
                                        String[] years= matcher.group(1).split("–");
                                        if(tvSeries.getStartYear() != Integer.parseInt(years[0])) {
                                            tvSeries.setStartYear(Integer.parseInt(years[0]));
                                            tvSeries.setLastTimeUpdated(new Date().getTime());
                                            updated[updatedIndex] = true;
                                        }
                                        if(years[1].equals(" ") || years[1].equals("")) {
                                            if(tvSeries.getEndYear() != Calendar.getInstance().get(Calendar.YEAR)) {
                                                tvSeries.setEndYear(Calendar.getInstance().get(Calendar.YEAR));
                                                tvSeries.setLastTimeUpdated(new Date().getTime());
                                                updated[updatedIndex] = true;
                                            }
                                        }
                                        else {
                                            if(tvSeries.getEndYear() != Integer.parseInt(years[1])) {
                                                tvSeries.setEndYear(Integer.parseInt(years[1]));
                                                state = Global.STATES.FINISHED;
                                                tvSeries.setLastTimeUpdated(new Date().getTime());
                                                updated[updatedIndex] = true;
                                            }
                                        }
                                    }
                                    else {
                                        if(tvSeries.getStartYear() != Integer.parseInt(matcher.group(1))) {
                                            tvSeries.setStartYear(Integer.parseInt(matcher.group(1)));
                                            tvSeries.setLastTimeUpdated(new Date().getTime());
                                            updated[updatedIndex] = true;
                                        }
                                        if(tvSeries.getEndYear() != Integer.parseInt(matcher.group(1))) {
                                            tvSeries.setEndYear(Integer.parseInt(matcher.group(1)));
                                            tvSeries.setLastTimeUpdated(new Date().getTime());
                                            updated[updatedIndex] = true;
                                        }
                                        state = Global.STATES.FINISHED;
                                    }

                                }
                            }
                        }
                        TVSeriesNrEpisodes -= nrEpisodesToDelete;
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
                                    if(!htmlLine.contains(context.getString(R.string.episode0FinderInSeason))) {
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
                                                        while (!htmlLine.contains(context.getString(R.string.IMDbRatingInSeasonFinder)))
                                                            htmlLine = seasonScanner.nextLine();
                                                        matcher = Pattern.compile(context.getString(R.string.IMDbRatingInSeasonPattern)).matcher(htmlLine);
                                                        if (matcher.find()) {
                                                            episode.setIMDBRating(Float.parseFloat(matcher.group(1)));
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        TVSeriesNrEpisodes--;
                                    }
                                }
                            }
                            season.setNrEpisodes(SeasonNrEpisodes);
                            season.setNrTotalOfEpisodes(nrTotalOfEpisodes);
                            database.dao().updateEpisodesList(episodesList);
                        }
                        if(tvSeries.getNrEpisodes() != TVSeriesNrEpisodes) {
                            tvSeries.setNrEpisodes(TVSeriesNrEpisodes);
                            tvSeries.setLastTimeUpdated(new Date().getTime());
                            updated[updatedIndex] = true;
                        }
                        database.dao().updateSeasonsList(seasonsList);
                        tvSeries.setSeenState();
                    }
                    updatedIndex++;
                }
                database.dao().updateTVSeriesList(TVSeriesList);

                int updatedTrue = 0;
                for(boolean b : updated)
                    if(b) updatedTrue++;
                message = "TVS list was updated. " + updatedTrue + " T.V. Series were updated.";
            }
            catch (Exception e) {
                message = "TVS list couldn't be updated";
            }
            finally {
                NotificationService.addNotification(message, context, "C1", "UpdateTVSeries", "Update TVSeries table");
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.isRunning = false;
        this.context = this;
        this.backgroundThread = new Thread(updateTVSeriesRunnable);
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isRunning) {
            isRunning = true;
            backgroundThread.start();
            isRunning = false;
        }
        return START_NOT_STICKY;
    }
}
