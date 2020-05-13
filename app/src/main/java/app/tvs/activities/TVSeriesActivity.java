package app.tvs.activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.tvs.Global;
import app.tvs.adapters.TVSeriesAdapter;
import app.tvs.adapters.SortAdapter;
import app.tvs.adapters.SearchAdapter;
import app.tvs.entities.TVSeries;
import app.tvs.entities.TVSeriesShort;
import app.tvs.sorts.LastTimeEpisodeSeenSort;
import app.tvs.sorts.Sorts;
import app.tvseries.R;

public class TVSeriesActivity extends MainActivity {

    private List<TVSeries> forDeleteTVSeriesList;
    private RecyclerView sortRecycleView;
    private Sorts sorts;
    private SortAdapter sortAdapter;
    private List<TVSeries> searchedTVSeries;
    private boolean searchMode = false;
    private SearchView TVSeriesSearchView;
    private RecyclerView addSearchRecyclerView;
    private EditText TVSeriesSearchEditText;
    private SearchAdapter searchAdapter;
    private List<TVSeriesShort> searchedForAddTVSeries;
    protected ImageView sortButton;
    protected ImageView searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sortButton.setOnClickListener(v -> {
            if (sortRecycleView.getVisibility() == View.INVISIBLE) {
                sortRecycleView.setVisibility(View.VISIBLE);
                setButtonsClickable(false);
                sortButton.setClickable(true);
                notifyChangedAdapter(getSorts().getActualSort().getSortedList());
            } else {
                closeSortList();
            }
        });

        searchButton.setOnClickListener(v -> {
            searchMode = true;
            openSearchView();
            searchedTVSeries = Global.database.dao().getTVSeries();
        });

        TVSeriesSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (searchMode) {
                    searchedTVSeries.clear();
                } else {
                    searchedForAddTVSeries.clear();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchMode) {
                    searchedTVSeries.addAll(Global.database.dao().getTVSeriesSortedAscByNameThatContains("%" + s.toString().toLowerCase() + "%"));
                } else {
                    List<TVSeriesShort> foundTVSeriesShort = Global.database.dao().getTVSeriesShortLike("%" + s.toString().toLowerCase() + "%");
                    List<TVSeriesShort> toRemoveFromFoundTVSeriesShort = new ArrayList<>();
                    for (TVSeriesShort tvSeriesShort : foundTVSeriesShort) {
                        if (tvSeriesShort.getName().equalsIgnoreCase(s.toString())) {
                            searchedForAddTVSeries.add(tvSeriesShort);
                            toRemoveFromFoundTVSeriesShort.add(tvSeriesShort);
                        }
                    }
                    for (TVSeriesShort tvSeriesShort : toRemoveFromFoundTVSeriesShort) {
                        foundTVSeriesShort.remove(tvSeriesShort);
                    }
                    searchedForAddTVSeries.addAll(foundTVSeriesShort);
                    if (searchedForAddTVSeries.size() < 6) {
                        searchedForAddTVSeries.addAll(Global.database.dao().getTVSeriesShortWithLimit(6 - searchedForAddTVSeries.size()));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchMode) {
                    notifyChangedAdapter(searchedTVSeries);
                } else {
                    searchAdapter.notifyDataSetChanged();
                }
            }
        });

        TVSeriesSearchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn).setOnClickListener(v -> {
            if (TVSeriesSearchEditText.getText().toString().equals("")) {
                closeSearchView();
            } else {
                resetTVSeriesSearchEditText();
            }
        });
    }

    @Override
    public void onResume() {
        ((TVSeriesAdapter) adapter).updateTVSeries(getSorts().getActualSort().getSortedList());
        super.onResume();
        addButton.setVisibility(View.VISIBLE);
        findViewById(R.id.totalTVSeriesLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.totalSeasonsLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.totalEpisodesLayout).setVisibility(View.VISIBLE);
        updateTotals();
        sortButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        if (searchMode) {
            closeSearchView();
            searchMode = false;
        }
        resetTVSeriesSearchEditText();
    }

    @Override
    public void onBackPressed() {
        if (sortRecycleView.getVisibility() == View.VISIBLE) {
            closeSortList();
        } else if (TVSeriesSearchView.getVisibility() == View.VISIBLE) {
            closeSearchView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void initHeader() {
        headerTextView.setText(R.string.app_name);
    }

    @Override
    protected void initLeaveButton() {
        leaveActivityButton.setImageDrawable(getDrawable(R.drawable.close_app));
        leaveActivityButton.setBackground(getDrawable(R.drawable.ripple_style));
    }

    @Override
    protected void initArrays() {
        searchedTVSeries = new ArrayList<>();
        forDeleteTVSeriesList = new ArrayList<>();
        searchedForAddTVSeries = new ArrayList<>();
        sorts = new Sorts();
        sorts.setActualSort(new LastTimeEpisodeSeenSort());
    }

    @Override
    protected void initViews() {
        super.initViews();
        TVSeriesSearchView = findViewById(R.id.searchTVSeriesSearchView);
        addSearchRecyclerView = findViewById(R.id.addSearchRecyclerView);
        sortButton = findViewById(R.id.sortButton);
        searchButton = findViewById(R.id.searchButton);
        sortRecycleView = findViewById(R.id.sortRecycleView);
        TVSeriesSearchEditText = TVSeriesSearchView.findViewById(R.id.search_src_text);
        TVSeriesSearchEditText.setTextColor(getColor(R.color.background));
        TVSeriesSearchEditText.setHint("Search a T.V. Series");
        TVSeriesSearchEditText.setHintTextColor(getColor(R.color.background));
    }

    @Override
    protected void setAdapters() {
        super.setAdapters();

        sortRecycleView.setLayoutManager(new LinearLayoutManager(this));
        sortRecycleView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 1;
            }
        });
        sortAdapter = new SortAdapter(getSorts(), this);
        sortRecycleView.setAdapter(sortAdapter);

        addSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addSearchRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 2;
            }
        });
        searchAdapter = new SearchAdapter(getSearchedForAddTVSeries(), this);
        addSearchRecyclerView.setAdapter(searchAdapter);
    }

    @Override
    protected void setAdapter() {
        adapter = new TVSeriesAdapter((isNotSearchMode()) ? (getSorts().getActualSort().getSortedList()) : (getSearchedTVSeries()), this);
    }

    @Override
    protected void startDeleteButtonAction() {
        super.setButtonsClickable(false);
        sortButton.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        super.startDeleteButtonAction();
    }

    @Override
    protected void endDeleteButtonAction() {
        notifyRemoveAdapter();
        forDeleteTVSeriesList.clear();
        sortButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        super.setButtonsClickable(true);
        super.endDeleteButtonAction();
        updateTotals();
    }

    public void notifyRemoveAdapter() {
        ((TVSeriesAdapter) adapter).removeTVSeries(forDeleteTVSeriesList);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void deleteButtonAction() {
        for (TVSeries tvSeries : forDeleteTVSeriesList) {
            Global.database.dao().deleteTVSeries(tvSeries);
        }
    }

    @Override
    public void setButtonsClickable(boolean bool) {
        super.setButtonsClickable(bool);
        sortButton.setClickable(bool);
        searchButton.setClickable(bool);
    }

    private void updateTotals() {
        ((TextView) findViewById(R.id.totalNrTVSeriesTextView)).setText(String.format(Locale.getDefault(), "%d", Global.database.dao().getNrOfTVSeries()));
        ((TextView) findViewById(R.id.totalNrSeasonsTextView)).setText(String.format(Locale.getDefault(), "%d", Global.database.dao().getNrOfSeasons()));
        ((TextView) findViewById(R.id.totalNrEpisodesTextView)).setText(String.format(Locale.getDefault(), "%d", Global.database.dao().getNrOFEpisodes()));
    }

    protected void addButtonAction() {
        openSearchView();
        addSearchRecyclerView.setVisibility(View.VISIBLE);
        searchedForAddTVSeries.clear();
        searchedForAddTVSeries.addAll(Global.database.dao().getTVSeriesShortWithLimit(100));
        searchAdapter.notifyDataSetChanged();
    }

    public void removeForDeleteTVSeriesList(TVSeries tvSeries) {
        forDeleteTVSeriesList.remove(tvSeries);
    }

    public void addForDeleteTVSeriesList(TVSeries tvSeries) {
        forDeleteTVSeriesList.add(tvSeries);
    }

    public void notifySortAdapter() {
        sortAdapter.notifyDataSetChanged();
    }

    public void notifyChangedAdapter(List<TVSeries> tvSeries) {
        ((TVSeriesAdapter)adapter).updateTVSeries(tvSeries);
        adapter.notifyDataSetChanged();
    }

    public void resetAddSearchEditText() {
        TVSeriesSearchEditText.setText("");
    }

    public void setHeaderVisible() {
        headerTextView.setVisibility(View.VISIBLE);
    }

    public void closeSortList() {
        sortRecycleView.setVisibility(View.INVISIBLE);
        setButtonsClickable(true);
        adapter.notifyDataSetChanged();
    }

    public void closeSearchView() {
        TVSeriesSearchView.setVisibility(View.INVISIBLE);
        addSearchRecyclerView.setVisibility(View.INVISIBLE);
        headerTextView.setVisibility(View.VISIBLE);
        setButtonsClickable(true);
        if (searchMode) {
            searchMode = false;
            notifyChangedAdapter(getSorts().getActualSort().getSortedList());
        }
    }

    private void openSearchView() {
        TVSeriesSearchView.setVisibility(View.VISIBLE);
        TVSeriesSearchView.setIconified(false);
        setButtonsClickable(false);
        headerTextView.setVisibility(View.INVISIBLE);
        if (searchMode) {
            TVSeriesSearchEditText.setHint("Search a T.V. Series");
        } else {
            TVSeriesSearchEditText.setHint("Add a T.V. Series");
        }
    }

    public List<TVSeries> getSearchedTVSeries() {
        return searchedTVSeries;
    }

    public Sorts getSorts() {
        return sorts;
    }

    public List<TVSeriesShort> getSearchedForAddTVSeries() {
        return searchedForAddTVSeries;
    }

    public boolean isNotSearchMode() {
        return !searchMode;
    }

    public boolean getAddSearchRecyclerView() {
        return addSearchRecyclerView.getVisibility() == View.INVISIBLE;
    }

    public boolean isSortListViewInvisible() {
        return sortRecycleView.getVisibility() == View.INVISIBLE;
    }

    public boolean isUpdateFormInvisible() {
        return formLayout.getVisibility() == View.INVISIBLE;
    }

    public void resetTVSeriesSearchEditText() {
        TVSeriesSearchEditText.setText("");
    }

    @Override
    public void closeForm() {
        addButton.setVisibility(View.VISIBLE);
        super.closeForm();
    }

}