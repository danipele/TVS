package app.tvs.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import app.tvs.Global;
import app.tvs.adapters.EpisodeAdapter;
import app.tvs.entities.Season;
import app.tvs.htmlReaderTasks.EpisodesHTMLReaderTask;
import app.tvseries.R;

public class EpisodesActivity extends EpisodesSeasonsActivity {

    private Season parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (getIntent().getExtras() != null) {
            parent = Global.database.dao().getSeasonById(getIntent().getExtras().getInt(getString(R.string.sharingSeasonId)));
        }

        super.onCreate(savedInstanceState);

        if (parent.getNrEpisodes() != parent.getNrEpisodesSeen()) {
            addButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void initHeader() {
        String headerString = String.format(Locale.getDefault(), getString(R.string.episodeHeaderFormat), Global.database.dao().getNameOfTVSeriesForEpisodeWithIdSeason(parent.getId()),parent.getIndex());
        if (headerString.length() > 24) {
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
    protected void endDeleteButtonAction() {
        setButtonsClickable(true);
        if (parent.getNrEpisodes() == parent.getNrEpisodesSeen()) {
            addButton.setVisibility(View.INVISIBLE);
        } else {
            addButton.setVisibility(View.VISIBLE);
        }
        super.endDeleteButtonAction();
    }

    @Override
    protected void deleteButtonAction() {
        ((EpisodeAdapter) adapter).removeEpisodes(parent);
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
        for (int i = 1; i <= parent.getNrEpisodes(); i++) {
            if (!episodes.contains(i)) {
                spinnerArray.add(i);
            }
        }
        return spinnerArray;
    }

    public Season getEpisodesParent() {
        return parent;
    }

    @Override
    protected void setAdapter() {

        adapter = new EpisodeAdapter(Global.database.dao().getEpisodesWithIdSeasonAscByIndex(getEpisodesParent().getId()), this);
    }
}
