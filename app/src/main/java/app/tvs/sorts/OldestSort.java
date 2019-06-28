package app.tvs.sorts;

import java.util.List;

import app.tvs.Global;
import app.tvs.entities.TVSeries;

public class OldestSort extends Sort {
    OldestSort() {
        super("Oldest");
    }

    @Override
    public List<TVSeries> getSortedList() {
        return Global.database.dao().getTVSeries();
    }
}