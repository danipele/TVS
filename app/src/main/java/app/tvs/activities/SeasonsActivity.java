package app.tvs.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.tvs.Global;
import app.tvs.adapters.SeasonAdapter;
import app.tvs.entities.TVSeries;
import app.tvs.htmlReaderTasks.SeasonsHTMLReaderTask;
import app.tvseries.R;

public class SeasonsActivity extends EpisodesSeasonsActivity {

    private TVSeries parent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(getIntent().getExtras() != null)
            parent = Global.database.dao().getTVSeriesWithId(getIntent().getExtras().getInt(getString(R.string.sharingTVSeriesId)));

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        parent = Global.database.dao().getTVSeriesWithId(parent.getId());
        if (parent.getNrSeasons() != parent.getSeasonsSeen()) {
            addButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.INVISIBLE);
        }
        ((SeasonAdapter) adapter).updateSeasons(Global.database.dao().getSeasonsWithIdTVSeries(parent.getId()));
        super.onResume();
    }

    @Override
    protected void initHeader() {
        if (parent.getName().length() > 24) {
            headerTextView.setTextSize(20 - (((float)(parent.getName().length() - 24) / 5 + 1)* 2));
        }
        headerTextView.setText(parent.getName());
    }

    @Override
    protected void initInfoForm() {
        super.initInfoForm();
        headerFormTextView.setText(R.string.addSeason);
        ((TextView) findViewById(R.id.nrTextView)).setText(R.string.seasonNr);
    }

    @Override
    protected void setAdapter() {
        adapter = new SeasonAdapter(Global.database.dao().getSeasonsWithIdTVSeriesAscByIndex(getSeasonsParent().getId()), this);
    }

    @Override
    protected void endDeleteButtonAction() {
        setButtonsClickable(true);
        if (parent.getNrSeasons() == parent.getSeasonsSeen()) {
            addButton.setVisibility(View.INVISIBLE);
        } else {
            addButton.setVisibility(View.VISIBLE);
        }
        super.endDeleteButtonAction();
    }

    @Override
    protected void deleteButtonAction() {
        ((SeasonAdapter) adapter).removeSeasons(parent);
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
        for (int i = 1; i <= parent.getNrSeasons(); i++) {
            if (!seasons.contains(i)) {
                spinnerArray.add(i);
            }
        }
        return spinnerArray;
    }

    public TVSeries getSeasonsParent() {
        return parent;
    }

}
