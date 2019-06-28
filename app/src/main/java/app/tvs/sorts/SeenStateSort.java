package app.tvs.sorts;

import java.util.List;

import app.tvs.Global;
import app.tvs.entities.TVSeries;

public class SeenStateSort extends Sort {
    SeenStateSort() {
        super("Seen state");
    }

    @Override
    public List<TVSeries> getSortedList() {
        return Global.database.dao().getTVSeriesSortedBySeenState();
    }
}