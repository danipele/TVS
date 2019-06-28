package app.tvs.sorts;

import java.util.List;

import app.tvs.Global;
import app.tvs.entities.TVSeries;

public class IMDbSortDesc extends Sort {
    IMDbSortDesc() {
        super("Best IMDb rating");
    }

    @Override
    public List<TVSeries> getSortedList() {
        return Global.database.dao().getTVSeriesSortedDescByIMDb();
    }
}