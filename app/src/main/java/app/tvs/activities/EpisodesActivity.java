package app.tvs.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.tvs.Global;
import app.tvs.adapters.EpisodeAdapter;
import app.tvs.entities.Episode;
import app.tvs.entities.Season;
import app.tvs.entities.TVSeries;
import app.tvs.htmlReaderTasks.EpisodesHTMLReaderTask;
import app.tvseries.R;

public class EpisodesActivity extends EpisodesSeasonsActivity {

    private Season parent;
    private List<Episode> forDeleteEpisodesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(getIntent().getExtras() != null)
            parent = Global.database.dao().getSeasonById(getIntent().getExtras().getInt(getString(R.string.sharingSeasonId)));

        super.onCreate(savedInstanceState);

        if(parent.getNrEpisodes() != parent.getNrEpisodesSeen()) {
            addButton.setVisibility(View.VISIBLE);
        }
        else {
            addButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void initHeader() {
        String headerString = String.format(Locale.getDefault(), "%s - S%d", Global.database.dao().getNameOfTVSeriesForEpisodeWithIdSeason(parent.getId()),parent.getIndex());
        if(headerString.length() > 24) {
            headerTextView.setTextSize(20 - (((float)(headerString.length() - 24) / 5 + 1)* 2));
        }
        headerTextView.setText(headerString);
    }

    @Override
    protected void initInfoForm() {
        super.initInfoForm();
        headerFormTextView.setText(R.string.addEpisode);
        ((TextView) findViewById(R.id.nrTextView)).setText(R.string.episodeNr);
    }

    @Override
    protected void initArrays() {
        forDeleteEpisodesList = new ArrayList<>();
    }

    @Override
    protected void endDeleteButtonAction() {
        notifyRemoveAdapter();
        forDeleteEpisodesList.clear();
        setButtonsClickable(true);
        if(parent.getNrEpisodes() == parent.getNrEpisodesSeen())
            addButton.setVisibility(View.INVISIBLE);
        else
            addButton.setVisibility(View.VISIBLE);
        super.endDeleteButtonAction();
    }

    @Override
    protected void deleteButtonAction() {
        parent = Global.database.dao().getSeasonById(parent.getId());
        for (Episode episode : forDeleteEpisodesList) {
            Global.database.dao().deleteEpisode(episode);
        }
        parent.removeNrEpisodesSeen(forDeleteEpisodesList.size());
        Global.database.dao().updateSeason(parent);
        TVSeries TVSeriesParent = Global.database.dao().getTVSeriesBySeasonId(parent.getId());
        TVSeriesParent.removeEpisodesSeen(forDeleteEpisodesList.size());
        if(Global.database.dao().getNrEpisodesForTVSeries(TVSeriesParent.getId()) == 0) {
            TVSeriesParent.setLastTimeEpisodeSeen(Long.MIN_VALUE);
        }
        else {
            TVSeriesParent.setLastTimeEpisodeSeen(Global.database.dao().getLastTimeAddedEpisodeForTVSeriesWithId(TVSeriesParent.getId()));
        }
        TVSeriesParent.setSeenState();
        Global.database.dao().updateTVSeries(TVSeriesParent);
    }

    @Override
    protected void resetInfo() {
        spinner.setSelection(0);
    }

    @Override
    protected void getInfoFromForm() {
        parent = Global.database.dao().getSeasonById(parent.getId());
        new EpisodesHTMLReaderTask(this, parent, (Integer) spinner.getSelectedItem()).execute();
        resetInfo();
    }

    @Override
    public ArrayAdapter<Integer> updateSpinnerArray(ArrayAdapter<Integer> spinnerArray) {
        List<Integer> episodes = Global.database.dao().getEpisodesIndexForIdSeason(parent.getId());
        for(int i = 1; i<= parent.getNrEpisodes(); i++)
            if(!episodes.contains(i))
                spinnerArray.add(i);
        return spinnerArray;
    }

    public Season getEpisodesParent() {
        return parent;
    }

    public void removeForDeleteEpisodesList(Episode episode) {
        forDeleteEpisodesList.remove(episode);
    }

    public void addForDeleteEpisodesList(Episode episode) {
        forDeleteEpisodesList.add(episode);
    }

    public void notifyRemoveAdapter() {
        ((EpisodeAdapter) adapter).removeEpisodes(forDeleteEpisodesList);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void setAdapter() {

        adapter = new EpisodeAdapter(Global.database.dao().getEpisodesWithIdSeasonAscByIndex(getEpisodesParent().getId()), this);
    }
}
