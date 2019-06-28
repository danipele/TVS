package app.tvs.sorts;

import java.util.List;

import app.tvs.Global;
import app.tvs.entities.TVSeries;

public class LastTimeEpisodeSeenSort extends Sort {
    public LastTimeEpisodeSeenSort() {
        super("Last episode seen");
    }

    @Override
    public List<TVSeries> getSortedList() {
        return Global.database.dao().getTVSeriesSortedByLastTimeEpisodeSeen();
    }
}