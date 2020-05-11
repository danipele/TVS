package app.tvs.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.tvs.Global;
import app.tvs.adapters.SeasonsAdapter;
import app.tvs.entities.Season;
import app.tvs.entities.TVSeries;
import app.tvs.htmlReaderTasks.SeasonsHTMLReaderTask;
import app.tvseries.R;

public class SeasonsActivity extends EpisodesSeasonsActivity {

    private TVSeries parent;
    private List<Season> forDeleteSeasonsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(getIntent().getExtras() != null)
            parent = Global.database.dao().getTVSeriesWithId(getIntent().getExtras().getInt(getString(R.string.sharingTVSeriesId)));

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        parent = Global.database.dao().getTVSeriesWithId(parent.getId());
        if(adapter.getCount() == 0) {
            ((TextView) findViewById(R.id.footerListTextView)).setText(getString(R.string.noSeason));
            findViewById(R.id.showArrowImageView).setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
        }
        else {
            if (parent.getNrEpisodes() == parent.getEpisodesSeen()) {
                if (parent.getState() == Global.STATES.FINISHED)
                    ((TextView) findViewById(R.id.footerListTextView)).setText(getString(R.string.finished));
                else
                    ((TextView) findViewById(R.id.footerListTextView)).setText(getString(R.string.upToDate));
            }
            else {
                ((TextView) findViewById(R.id.footerListTextView)).setText(getString(R.string.theEnd));
            }
            if(parent.getNrSeasons() == parent.getSeasonsSeen()) {
                addButton.setVisibility(View.INVISIBLE);
            }
            findViewById(R.id.showArrowImageView).setVisibility(View.INVISIBLE);
        }
        super.onResume();
    }

    @Override
    protected void initHeader() {
        if(parent.getName().length() > 24) {
            headerTextView.setTextSize(20 - (((float)(parent.getName().length() - 24) / 5 + 1)* 2));
        }
        headerTextView.setText(parent.getName());
    }

    @Override
    protected void initArrays() {
        forDeleteSeasonsList = new ArrayList<>();
    }

    @Override
    protected void initInfoForm() {
        super.initInfoForm();
        headerFormTextView.setText(R.string.addSeason);
        ((TextView) findViewById(R.id.nrTextView)).setText(R.string.seasonNr);
    }

    @Override
    protected void setAdapter() {
        adapter = new SeasonsAdapter(this);
    }

    @Override
    protected void endDeleteButtonAction() {
        forDeleteSeasonsList.clear();
        setButtonsClickable(true);
        if(parent.getNrSeasons() == parent.getSeasonsSeen())
            addButton.setVisibility(View.INVISIBLE);
        else
            addButton.setVisibility(View.VISIBLE);
        super.endDeleteButtonAction();
        if(adapter.getCount() == 0) {
            ((TextView) findViewById(R.id.footerListTextView)).setText(getString(R.string.noSeason));
            findViewById(R.id.showArrowImageView).setVisibility(View.VISIBLE);
        }
        else {
            ((TextView) findViewById(R.id.footerListTextView)).setText(getString(R.string.theEnd));
            findViewById(R.id.showArrowImageView).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void deleteButtonAction() {
        parent = Global.database.dao().getTVSeriesWithId(parent.getId());
        int episodesFromDeletedSeasons = 0;
        for(Season season : forDeleteSeasonsList) {
            Global.database.dao().deleteSeason(season);
            episodesFromDeletedSeasons += season.getNrEpisodesSeen();
        }
        parent.removeSeasonsSeen(forDeleteSeasonsList.size());
        parent.removeEpisodesSeen(episodesFromDeletedSeasons);
        parent.updateYearsSeen();
        if(Global.database.dao().getNrEpisodesForTVSeries(parent.getId()) == 0) {
            parent.setLastTimeEpisodeSeen(Long.MIN_VALUE);
        }
        else {
            parent.setLastTimeEpisodeSeen(Global.database.dao().getLastTimeAddedEpisodeForTVSeriesWithId(parent.getId()));
        }
        Global.database.dao().updateTVSeries(parent);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void getInfoFromForm() {
        parent = Global.database.dao().getTVSeriesWithId(parent.getId());
        new SeasonsHTMLReaderTask(this, parent, (Integer) spinner.getSelectedItem()).execute();
        resetInfo();
    }

    @Override
    public ArrayAdapter<Integer> updateSpinnerArray(ArrayAdapter<Integer> spinnerArray) {
        List<Integer> seasons = Global.database.dao().getSeasonsIndexForTVSeries(parent.getId());
        for(int i = 1; i<= parent.getNrSeasons(); i++)
            if(!seasons.contains(i))
                spinnerArray.add(i);
        return spinnerArray;
    }

    public TVSeries getSeasonsParent() {
        return parent;
    }

    public void removeForDeleteSeasonsList(Season season) {
        forDeleteSeasonsList.remove(season);
    }

    public void addForDeleteSeasonsList(Season season) {
        forDeleteSeasonsList.add(season);
    }
}
