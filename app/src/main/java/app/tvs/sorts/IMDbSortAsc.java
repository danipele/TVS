package app.tvs.sorts;

import java.util.List;

import app.tvs.Global;
import app.tvs.entities.TVSeries;

public class IMDbSortAsc extends Sort {
    IMDbSortAsc() {
        super("Worst IMDb rating");
    }

    @Override
    public List<TVSeries> getSortedList() {
        return Global.database.dao().getTVSeriesSortedAscByIMDb();
    }
}