package app.tvs.sorts;

import java.util.ArrayList;
import java.util.List;

public class Sorts {

    private List<Sort> sorts;

    private Sort actualSort;

    public Sorts() {
        sorts = new ArrayList<>();
        sorts.add(new LastTimeEpisodeSeenSort());
        sorts.add(new NewestSort());
        sorts.add(new OldestSort());
        sorts.add(new NameSortAsc());
        sorts.add(new NameSortDesc());
        sorts.add(new IMDbSortAsc());
        sorts.add(new IMDbSortDesc());
        sorts.add(new SeasonsSeenSortAsc());
        sorts.add(new SeasonsSeenSortDesc());
        sorts.add(new EpisodesSeenSortAsc());
        sorts.add(new EpisodesSeenSortDesc());
        sorts.add(new StateSort());
        sorts.add(new SeenStateSort());
        sorts.add(new LastTimeUpdatedSort());
    }

    public Sort getActualSort() {
        return actualSort;
    }

    public void setActualSort(Sort actualSort) {
        this.actualSort = actualSort;
    }

    public int getSortsCount() {
        return sorts.size();
    }

    public Sort getSort(int position) {
        return sorts.get(position);
    }
}
