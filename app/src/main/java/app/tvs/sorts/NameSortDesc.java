package app.tvs.sorts;

import java.util.List;

import app.tvs.Global;
import app.tvs.entities.TVSeries;

public class NameSortDesc extends Sort {
    NameSortDesc() {
        super("Z-A");
    }

    @Override
    public List<TVSeries> getSortedList() {
        return Global.database.dao().getTVSeriesSortedDescByName();
    }
}