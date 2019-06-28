package app.tvs.sorts;


import java.util.List;

import app.tvs.Global;
import app.tvs.entities.TVSeries;

public class LastTimeUpdatedSort extends Sort {
    LastTimeUpdatedSort() {
        super("Last updated");
    }

    @Override
    public List<TVSeries> getSortedList() {
        return Global.database.dao().getTVSeriesSortedByLastTimeUpdated();
    }
}