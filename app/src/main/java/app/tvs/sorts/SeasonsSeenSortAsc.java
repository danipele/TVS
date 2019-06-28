package app.tvs.sorts;

import java.util.List;

import app.tvs.Global;
import app.tvs.entities.TVSeries;

public class SeasonsSeenSortAsc extends Sort {
    SeasonsSeenSortAsc() {
        super("Least seasons seen");
    }

    @Override
    public List<TVSeries> getSortedList() {
        return Global.database.dao().getTVSeriesSortedAscBySeasonsSeen();
    }
}
