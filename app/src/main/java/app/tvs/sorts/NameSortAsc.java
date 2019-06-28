package app.tvs.sorts;

import java.util.List;

import app.tvs.Global;
import app.tvs.entities.TVSeries;

public class NameSortAsc extends Sort {
    NameSortAsc() {
        super("A-Z");
    }

    @Override
    public List<TVSeries> getSortedList() {
        return Global.database.dao().getTVSeriesSortedAscByName();
    }
}