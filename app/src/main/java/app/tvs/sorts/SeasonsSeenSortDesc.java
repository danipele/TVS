package app.tvs.sorts;

import java.util.List;

import app.tvs.Global;
import app.tvs.entities.TVSeries;

public class SeasonsSeenSortDesc extends Sort {
    SeasonsSeenSortDesc() {
        super("Most seasons seen");
    }

    @Override
    public List<TVSeries> getSortedList() {
        return Global.database.dao().getTVSeriesSortedDescBySeasonsSeen();
    }
}