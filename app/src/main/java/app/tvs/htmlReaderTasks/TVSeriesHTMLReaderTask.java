package app.tvs.htmlReaderTasks;

import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.TextView;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.tvs.Global;
import app.tvseries.R;
import app.tvs.activities.MainActivity;
import app.tvs.activities.TVSeriesActivity;
import app.tvs.entities.TVSeries;

public class TVSeriesHTMLReaderTask extends HTMLReaderTask {

    private String url;

    public TVSeriesHTMLReaderTask(MainActivity activity, String url) {
        super(activity);
        this.url = url;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        ((TextView) activity.findViewById(R.id.totalNrTVSeriesTextView)).setText(String.format(Locale.getDefault(), "%d", Global.database.dao().getNrOfTVSeries()));
        if(result.equals("")) {
            ((TextView) activity.findViewById(R.id.footerListTextView)).setText(activity.getString(R.string.theEnd));
            activity.findViewById(R.id.showArrowImageView).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPreExecute() {
        ((TVSeriesActivity) activity).closeSearchView();
        ((TVSeriesActivity) activity).setHeaderVisible();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {

            String htmlLine;
            String name="";
            int startYear = 0;
            int endYear = 0;
            int nrSeasons = 0;
            int nrEpisodes = 0;
            double IMDBRating = 0;
            Global.STATES state = Global.STATES.UNDECLARED;
            String imageLink = "";
            Matcher matcher;
            int nrEpisodesToDelete = 0;
            int nrEpisodesFound = 0;
            int nrEpisodesFromLastSeasonToDelete = 0;

            Scanner TVSeriesScanner = new Scanner(new URL(url).openStream());

            while(TVSeriesScanner.hasNext()) {
                htmlLine = TVSeriesScanner.nextLine();
                if(htmlLine.contains(activity.getString(R.string.nameFinder))) {
                    htmlLine = TVSeriesScanner.nextLine();
                    matcher = Pattern.compile(activity.getString(R.string.namePattern)).matcher(htmlLine);
                    if(matcher.find()) {
                        name = matcher.group(1);
                    }
                    htmlLine = TVSeriesScanner.nextLine();
                    if(htmlLine.contains(activity.getString(R.string.originalNameFinder))) {
                        matcher = Pattern.compile(activity.getString(R.string.originalNamePattern)).matcher(htmlLine);
                        if(matcher.find()) {
                            name = matcher.group(1);
                        }
                    }
                }
                if(htmlLine.contains(activity.getString(R.string.dateFinder))) {
                    matcher = Pattern.compile(activity.getString(R.string.datePattern)).matcher(htmlLine);
                    if(matcher.find()) {
                        if(matcher.group(1).contains("–")) {
                            String years[] = matcher.group(1).split("–");
                            startYear = Integer.parseInt(years[0]);
                            if(years[1].equals(" ") || years[1].equals("")) {
                                endYear = Calendar.getInstance().get(Calendar.YEAR);
                            }
                            else {
                                endYear = Integer.parseInt(years[1]);
                                state = Global.STATES.FINISHED;
                            }
                        }
                        else {
                            startYear = Integer.parseInt(matcher.group(1));
                            endYear = Integer.parseInt(matcher.group(1));
                            state = Global.STATES.FINISHED;
                        }
                    }
                }
                if(htmlLine.contains(activity.getString(R.string.nrSeasonsFinder))) {
                    while(!htmlLine.contains(activity.getString(R.string.nrSeasonsFindingCond)))
                        htmlLine = TVSeriesScanner.nextLine();
                    matcher = Pattern.compile(activity.getString(R.string.nrSeasonPattern)).matcher(htmlLine);
                    if(matcher.find()) {
                        nrSeasons = Integer.parseInt(matcher.group(1));
                        while(nrEpisodesFound == nrEpisodesToDelete && nrSeasons > 0) {
                            nrEpisodesFromLastSeasonToDelete = 0;
                            Scanner seasonScanner = new Scanner(new URL(url+activity.getString(R.string.forSeasonLink)+nrSeasons).openStream());
                            while(seasonScanner.hasNext()) {
                                htmlLine = seasonScanner.nextLine();
                                if(htmlLine.contains(activity.getString(R.string.episodeItemIdOdd)) || htmlLine.contains(activity.getString(R.string.episodeItemIdEven))) {
                                    nrEpisodesFound++;
                                    boolean unreleased = true;
                                    htmlLine = seasonScanner.nextLine();
                                    while (!htmlLine.contains(activity.getString(R.string.searchUntil))) {
                                        if (htmlLine.contains(activity.getString(R.string.episodeItemFinder))) {
                                            htmlLine = seasonScanner.nextLine();
                                            if (!htmlLine.contains(activity.getString(R.string.episodeItemEmptyPattern))) {
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
                            if(nrEpisodesFound == nrEpisodesToDelete)
                                nrSeasons--;
                        }
                        if(state == Global.STATES.UNDECLARED)
                            state = (nrEpisodesFromLastSeasonToDelete == 0)?(Global.STATES.IN_STAND_BY):(Global.STATES.ON_GOING);
                    }
                    Scanner seasonUnknownScanner = new Scanner(new URL(url+activity.getString(R.string.forSeasonLink)+"-1").openStream());
                    while(seasonUnknownScanner.hasNext()) {
                        htmlLine = seasonUnknownScanner.nextLine();
                        if(htmlLine.contains(activity.getString(R.string.seasonUnknownFinder))) {
                            if(htmlLine.contains(activity.getString(R.string.seasonUnknownPattern))) {
                                nrEpisodesToDelete++;
                            }
                        }
                    }
                    break;
                }
                if(htmlLine.contains(activity.getString(R.string.nrEpisodesFinder))) {
                    htmlLine = TVSeriesScanner.nextLine();
                    matcher = Pattern.compile(activity.getString(R.string.nrEpisodesPattern)).matcher(htmlLine);
                    if(matcher.find()) {
                        nrEpisodes = Integer.parseInt(matcher.group(1));
                    }
                }
                if (htmlLine.contains(activity.getString(R.string.IMDbRatingFinder1)) && htmlLine.contains(activity.getString(R.string.IMDbRatingFinder2))) {
                    matcher = Pattern.compile(activity.getString(R.string.IMDbRatingPattern)).matcher(htmlLine);
                    if(matcher.find()) {
                        IMDBRating = Float.parseFloat(matcher.group(1));
                    }
                }
                if(htmlLine.contains(activity.getString(R.string.imageLinkFinder))) {
                    htmlLine = TVSeriesScanner.nextLine();
                    matcher = Pattern.compile(activity.getString(R.string.imageLinkPattern)).matcher(htmlLine);
                    if(matcher.find()) {
                        imageLink = matcher.group(1);
                    }
                }
            }
            nrEpisodes -= nrEpisodesToDelete;

            if(imageLink.equals("")) {
                imageLink = "https://semantic-ui.com/images/wireframe/image.png";
            }

            if(name.equals("") || startYear == 0 || endYear == 0 || nrSeasons == 0 || nrEpisodes == 0 || IMDBRating == 0)
                throw new Exception();
            else {
                TVSeries toAddTVSeries = new TVSeries(url, name, startYear, endYear, nrSeasons, nrEpisodes, 0, 0, 0, 0, IMDBRating, state, Global.SEENSTATES.PAUSE, BitmapFactory.decodeStream(new URL(imageLink).openConnection().getInputStream()), new Date().getTime(), Long.MIN_VALUE);
                Global.database.dao().addTVSeries(toAddTVSeries);
            }
            return "";

        } catch (Exception e) {
            return getToastMessage();
        }
    }

    @Override
    protected String getToastMessage() {
        return activity.getString(R.string.addingTVSeriesError);
    }
}