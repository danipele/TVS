package app.tvs.sorts;

import java.util.List;

import app.tvs.Global;
import app.tvs.entities.TVSeries;

public class EpisodesSeenSortDesc extends Sort {
    EpisodesSeenSortDesc() {
        super("Most episodes seen");
    }

    @Override
    public List<TVSeries> getSortedList() {
        return Global.database.dao().getTVSeriesSortedDescByEpisodesSeen();
    }
}
