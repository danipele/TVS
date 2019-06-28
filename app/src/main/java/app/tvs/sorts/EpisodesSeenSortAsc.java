package app.tvs.sorts;

import java.util.List;

import app.tvs.Global;
import app.tvs.entities.TVSeries;

public class EpisodesSeenSortAsc extends Sort {
    EpisodesSeenSortAsc() {
        super("Least episodes seen");
    }

    @Override
    public List<TVSeries> getSortedList() {
        return Global.database.dao().getTVSeriesSortedAscByEpisodesSeen();
    }
}
