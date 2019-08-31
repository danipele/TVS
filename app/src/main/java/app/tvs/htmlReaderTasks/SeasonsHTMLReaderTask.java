package app.tvs.htmlReaderTasks;

import android.view.View;
import android.widget.TextView;

import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.tvs.Global;
import app.tvseries.R;
import app.tvs.activities.MainActivity;
import app.tvs.activities.SeasonsActivity;
import app.tvs.entities.Season;
import app.tvs.entities.TVSeries;

public class SeasonsHTMLReaderTask extends HTMLReaderTask {

    private TVSeries parent;
    private int index;

    public SeasonsHTMLReaderTask(MainActivity activity, TVSeries parent, int index) {
        super(activity);
        this.parent = parent;
        this.index = index;
    }

    @Override
    protected void onPreExecute() {
        ((SeasonsActivity) activity).closeForm();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("")) {
            ((TextView) activity.findViewById(R.id.footerListTextView)).setText(activity.getString(R.string.theEnd));
            if(parent.getNrSeasons() == parent.getSeasonsSeen()) {
                activity.addButton.setVisibility(View.INVISIBLE);
            }
            activity.findViewById(R.id.showArrowImageView).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String htmlLine;
            Matcher matcher;
            int startYear = 0;
            int endYear = 0;
            int nrEpisodes = 0;
            int nrTotalOfEpisodes = 0;

            Scanner seasonScanner = new Scanner(new URL(parent.getIMDBLink() + activity.getString(R.string.forSeasonLink) + index).openStream());

            while(seasonScanner.hasNext()) {
                htmlLine = seasonScanner.nextLine();
                if(htmlLine.contains(activity.getString(R.string.episodeFinder))) {
                    if(!htmlLine.contains(activity.getString(R.string.episode0FinderInSeason))) {
                        nrTotalOfEpisodes++;
                        htmlLine = seasonScanner.nextLine();
                        if (htmlLine.contains(activity.getString(R.string.episodeItemAirdateFinder))) {
                            htmlLine = seasonScanner.nextLine();
                            matcher = Pattern.compile(activity.getString(R.string.episodeItemAirdatePattern)).matcher(htmlLine);
                            if (matcher.find()) {
                                if (startYear == 0) {
                                    startYear = Integer.parseInt(matcher.group(1));
                                } else {
                                    endYear = Integer.parseInt(matcher.group(1));
                                }
                            }
                            boolean unreleased = true;
                            htmlLine = seasonScanner.nextLine();
                            while (!htmlLine.contains(activity.getString(R.string.searchUntil))) {
                                if (htmlLine.contains(activity.getString(R.string.episodeItemFinder))) {
                                    htmlLine = seasonScanner.nextLine();
                                    if (!htmlLine.contains(activity.getString(R.string.episodeItemEmptyPattern))) {
                                        unreleased = false;
                                    }
                                }
                                htmlLine = seasonScanner.nextLine();
                            }
                            if (!unreleased) {
                                nrEpisodes++;
                            }
                        }
                    }
                    else {
                        parent.removeEpisodes();
                        Global.database.dao().updateTVSeries(parent);
                    }
                }
            }

            if(startYear == 0 || endYear == 0 || nrEpisodes == 0) {
                throw new Exception();
            }
            else {
                Global.database.dao().addSeason(new Season(index, startYear, endYear, nrEpisodes, nrTotalOfEpisodes, 0, parent.getId()));
                TVSeries updateTVSeries = Global.database.dao().getTVSeriesWithId(parent.getId());
                if(updateTVSeries.getStartYearSeen() > startYear || updateTVSeries.getStartYearSeen() == 0)
                    updateTVSeries.setStartYearSeen(startYear);
                if(updateTVSeries.getEndYearSeen() < endYear)
                    updateTVSeries.setEndYearSeen(endYear);
                updateTVSeries.setSeasonsSeen();
                Global.database.dao().updateTVSeries(updateTVSeries);
                parent = Global.database.dao().getTVSeriesWithId(parent.getId());
            }

            return "";
        }
        catch (Exception e) {
            return getToastMessage();
        }
    }

    @Override
    protected void finishTask() {
        super.finishTask();
        ((SeasonsActivity) activity).updateSpinner();
    }

    @Override
    protected String getToastMessage() {
        return activity.getString(R.string.addingSeasonError);
    }
}
