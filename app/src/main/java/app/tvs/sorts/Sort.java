package app.tvs.sorts;

import java.util.List;

import app.tvs.entities.TVSeries;

public abstract class Sort {
    private String name;
    public Sort(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract public List<TVSeries> getSortedList();

}
