package app.tvs.htmlReaderTasks;

import android.graphics.BitmapFactory;
import android.view.View;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.tvs.Global;
import app.tvs.adapters.EpisodeAdapter;
import app.tvseries.R;
import app.tvs.activities.EpisodesActivity;
import app.tvs.activities.MainActivity;
import app.tvs.entities.Episode;
import app.tvs.entities.Season;
import app.tvs.entities.TVSeries;

public class EpisodesHTMLReaderTask extends HTMLReaderTask {

    private Season parent;
    private int index;

    public EpisodesHTMLReaderTask(MainActivity activity, Season parent, int index) {
        super(activity);
        this.parent = parent;
        this.index = index;
    }

    @Override
    protected void onPreExecute() {
        activity.closeForm();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.isEmpty()) {
            if (parent.getNrEpisodes() == parent.getNrEpisodesSeen()) {
                activity.addButton.setVisibility(View.INVISIBLE);
            } else {
                activity.addButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String htmlLine;
            String episodeLink;
            Matcher matcher;
            String name = StringUtils.EMPTY;
            String releaseDate = StringUtils.EMPTY;
            String duration = StringUtils.EMPTY;
            String description = StringUtils.EMPTY;
            double IMDb = 0;
            String imageLink = StringUtils.EMPTY;

            Scanner seasonScanner = new Scanner(new URL(Global.database.dao().getIMDBLinkOfTVSeriesForSeason(parent.getId()) + activity.getString(R.string.forSeasonLink) + parent.getIndex()).openStream());

            while (seasonScanner.hasNext()) {
                htmlLine = seasonScanner.nextLine();
                if (htmlLine.contains(activity.getString(R.string.episodeItemIdEven)) || htmlLine.contains(activity.getString(R.string.episodeItemIdOdd))) {
                    seasonScanner.nextLine();
                    htmlLine = seasonScanner.nextLine();
                    episodeLink = seasonScanner.nextLine();
                    while (!htmlLine.contains(activity.getString(R.string.episodeFinder))) {
                        htmlLine = seasonScanner.nextLine();
                    }
                    if (htmlLine.contains(Integer.toString(index))) {
                        matcher = Pattern.compile(activity.getString(R.string.episodeLinkPattern)).matcher(episodeLink);
                        if (matcher.find()) {
                            Scanner episodeScanner = new Scanner(new URL(activity.getString(R.string.IMDbSiteLink) + matcher.group(1)).openStream());
                            while (episodeScanner.hasNext()) {
                                htmlLine = episodeScanner.nextLine();
                                if (htmlLine.contains(activity.getString(R.string.nameFinder))) {
                                    htmlLine = episodeScanner.nextLine();
                                    matcher = Pattern.compile(activity.getString(R.string.namePattern)).matcher(htmlLine);
                                    if (matcher.find()) {
                                        name = matcher.group(1);
                                    }
                                }
                                if (htmlLine.contains(activity.getString(R.string.episodeAirdateFinder))) {
                                    matcher = Pattern.compile(activity.getString(R.string.episodeAirdatePattern)).matcher(htmlLine);
                                    if (matcher.find()) {
                                        releaseDate = matcher.group(1);
                                    }
                                }
                                if (htmlLine.contains(activity.getString(R.string.episodeDurationFinder)) && duration.isEmpty()) {
                                    htmlLine = episodeScanner.nextLine();
                                    matcher = Pattern.compile(activity.getString(R.string.episodeDurationPattern)).matcher(htmlLine);
                                    if (matcher.find()) {
                                        duration = matcher.group(1);
                                    }
                                }
                                if (htmlLine.contains(activity.getString(R.string.IMDbRatingFinder1)) && htmlLine.contains(activity.getString(R.string.IMDbRatingFinder2))) {
                                    matcher = Pattern.compile(activity.getString(R.string.IMDbRatingPattern)).matcher(htmlLine);
                                    if (matcher.find()) {
                                        IMDb = Float.parseFloat(matcher.group(1));
                                    }
                                }
                                if (htmlLine.contains(activity.getString(R.string.episodeImageLinkFinder))) {
                                    while (!htmlLine.contains(activity.getString(R.string.src))) {
                                        htmlLine = episodeScanner.nextLine();
                                    }
                                    String htmlLineAux;
                                    while (!(htmlLineAux = episodeScanner.nextLine()).contains(activity.getString(R.string.imageLinkEnd))) {
                                        htmlLine = String.format(Locale.getDefault(), activity.getString(R.string.stringStringFormat), htmlLine, htmlLineAux);
                                    }
                                    matcher = Pattern.compile(activity.getString(R.string.imageLinkPattern)).matcher(htmlLine);
                                    if (matcher.find()) {
                                        imageLink = matcher.group(1);
                                    }
                                }
                                if (htmlLine.contains(activity.getString(R.string.episodeDescriptionFinder))) {
                                    episodeScanner.nextLine();
                                    episodeScanner.nextLine();
                                    episodeScanner.nextLine();
                                    htmlLine = episodeScanner.nextLine();
                                    matcher = Pattern.compile(activity.getString(R.string.episodeDescriptionPattern)).matcher(htmlLine);
                                    if (matcher.find()) {
                                        description = matcher.group(1);
                                        String[] sentences = description.split(activity.getString(R.string.sentenceFinder));
                                        for (int i = 0; i < sentences.length; i++) {
                                            if (sentences[i].contains(activity.getString(R.string.endTag))) {
                                                String result = sentences[i].substring(0,sentences[i].indexOf('<')).concat(sentences[i].substring(sentences[i].indexOf('>') + 1));
                                                sentences[i] = result.substring(0,result.indexOf('<')).concat(result.substring(result.indexOf('>') + 1));
                                            }
                                        }
                                        if (sentences.length > 3) {
                                            description = sentences[0] + sentences[1] + sentences[2] + activity.getString(R.string.dot);
                                        } else {
                                            description = StringUtils.EMPTY;
                                            for (String sentence : sentences) {
                                                description = description.concat(sentence);
                                            }
                                        }

                                    } else {
                                        matcher = Pattern.compile(activity.getString(R.string.episodeDescriptionPatternNotFinished)).matcher(htmlLine);
                                        if (matcher.find()) {
                                            description = matcher.group(1) + activity.getString(R.string.dot);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }

            if (imageLink.isEmpty()) {
                imageLink = activity.getString(R.string.noImageLink);
            }

            if (name.isEmpty() || releaseDate.isEmpty() || duration.isEmpty() || IMDb == 0) {
                throw new Exception();
            } else {
                long now = new Date().getTime();
                Episode episode = new Episode(index, name, releaseDate, duration, description, IMDb, BitmapFactory.decodeStream(new URL(imageLink).openConnection().getInputStream()), parent.getId(), now);
                Global.database.dao().addEpisodes(episode);
                episode = Global.database.dao().getEpisodeForSeasonWithIndex(parent.getId(), index);
                Season season = Global.database.dao().getSeasonById(parent.getId());
                season.setNrEpisodesSeen();
                Global.database.dao().updateSeason(season);
                parent = Global.database.dao().getSeasonById(parent.getId());
                TVSeries TVseries= Global.database.dao().getTVSeriesBySeasonId(parent.getId());
                TVseries.setEpisodesSeen();
                TVseries.setLastTimeEpisodeSeen(now);
                TVseries.setSeenState();
                Global.database.dao().updateTVSeries(TVseries);
                ((EpisodeAdapter) activity.adapter).addEpisode(episode);
            }

            return StringUtils.EMPTY;
        }
        catch (Exception e) {
            return getToastMessage();
        }
    }

    @Override
    protected void finishTask() {
        super.finishTask();
        ((EpisodesActivity) activity).updateSpinner();
    }

    @Override
    protected String getToastMessage() {
        return activity.getString(R.string.addingEpisodeError);
    }
}
