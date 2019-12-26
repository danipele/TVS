package app.tvs.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import app.tvs.Global;
import app.tvs.adapters.SearchAdapter;
import app.tvs.adapters.SortAdapter;
import app.tvs.adapters.TVSeriesAdapter;
import app.tvs.broadcastReceivers.UpdateTVSeriesBroadcastReceiver;
import app.tvs.broadcastReceivers.UpdateTVSeriesShortBroadcastReceiver;
import app.tvs.entities.TVSeries;
import app.tvs.entities.TVSeriesShort;
import app.tvs.sorts.LastTimeEpisodeSeenSort;
import app.tvs.sorts.Sorts;
import app.tvseries.R;

public class TVSeriesActivity extends MainActivity {

    private List<TVSeries> forDeleteTVSeriesList;
    private ListView sortListView;
    private Sorts sorts;
    private SortAdapter sortAdapter;
    private List<TVSeries> searchedTVSeries;
    private boolean searchMode = false;
    private SearchView TVSeriesSearchView;
    private ListView addSearchListView;
    private EditText TVSeriesSearchEditText;
    private SearchAdapter searchAdapter;
    private List<TVSeriesShort> searchedForAddTVSeries;
    protected ImageView sortButton;
    protected ImageView searchButton;
    protected ImageView updateButton;
    protected ImageView updateDBButton;
    protected TextView updateTextView;
    protected TextView updateDBTextView;
    protected Button acceptUpdateButton;
    protected Button acceptUpdateDBButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sortListView.getVisibility() == View.INVISIBLE) {
                    sortListView.setVisibility(View.VISIBLE);
                    setButtonsClickable(false);
                    sortButton.setClickable(true);
                    adapter.notifyDataSetChanged();
                }
                else {
                    closeSortList();
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMode = true;
                openSearchView();
                searchedTVSeries = Global.database.dao().getTVSeries();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formLayout.setVisibility(View.VISIBLE);
                setButtonsClickable(false);
                updateTextView.setVisibility(View.VISIBLE);
                addInForm.setVisibility(View.INVISIBLE);
                acceptUpdateButton.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });

        updateDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formLayout.setVisibility(View.VISIBLE);
                setButtonsClickable(false);
                updateDBTextView.setVisibility(View.VISIBLE);
                addInForm.setVisibility(View.INVISIBLE);
                acceptUpdateDBButton.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });

        acceptUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AlarmManager) Objects.requireNonNull(getSystemService(Context.ALARM_SERVICE))).set(AlarmManager.RTC, setCalendarForUpdate().getTimeInMillis(), PendingIntent.getBroadcast(getActivity(), 2, new Intent(getActivity(), UpdateTVSeriesBroadcastReceiver.class), 0));
                closeForm();
            }
        });

        acceptUpdateDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AlarmManager) Objects.requireNonNull(getSystemService(Context.ALARM_SERVICE))).set(AlarmManager.RTC, setCalendarForUpdate().getTimeInMillis(), PendingIntent.getBroadcast(getActivity(), 2, new Intent(getActivity(), UpdateTVSeriesShortBroadcastReceiver.class), 0));
                closeForm();
            }
        });

        TVSeriesSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(searchMode)
                    searchedTVSeries.clear();
                else
                    searchedForAddTVSeries.clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchMode)
                    searchedTVSeries.addAll(Global.database.dao().getTVSeriesSortedAscByNameThatContains("%" + s.toString().toLowerCase() + "%"));
                else {
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
                if(searchMode)
                    adapter.notifyDataSetChanged();
                else
                    searchAdapter.notifyDataSetChanged();
            }
        });

        TVSeriesSearchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TVSeriesSearchEditText.getText().toString().equals("")) {
                    closeSearchView();
                }
                else {
                    resetTVSeriesSearchEditText();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter.getCount() == 0) {
            ((TextView) findViewById(R.id.footerListTextView)).setText(getString(R.string.noTVSeries));
            findViewById(R.id.showArrowImageView).setVisibility(View.VISIBLE);
        }
        else {
            ((TextView) findViewById(R.id.footerListTextView)).setText(getString(R.string.theEnd));
            findViewById(R.id.showArrowImageView).setVisibility(View.INVISIBLE);
        }
        addButton.setVisibility(View.VISIBLE);
        findViewById(R.id.totalTVSeriesLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.totalSeasonsLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.totalEpisodesLayout).setVisibility(View.VISIBLE);
        updateTotals();
        sortButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.VISIBLE);
        updateDBButton.setVisibility(View.VISIBLE);
        if(searchMode) {
            closeSearchView();
            searchMode = false;
        }
        resetTVSeriesSearchEditText();
    }

    @Override
    public void onBackPressed() {
        if(sortListView.getVisibility() == View.VISIBLE) {
            closeSortList();
        }
        else if(TVSeriesSearchView.getVisibility() == View.VISIBLE) {
            closeSearchView();
        }
        else super.onBackPressed();
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
        addSearchListView = findViewById(R.id.addSearchListView);
        sortButton = findViewById(R.id.sortButton);
        searchButton = findViewById(R.id.searchButton);
        updateButton = findViewById(R.id.updateButton);
        updateDBButton = findViewById(R.id.updateDBButton);
        sortListView = findViewById(R.id.sortListView);
        TVSeriesSearchEditText = TVSeriesSearchView.findViewById(R.id.search_src_text);
        TVSeriesSearchEditText.setTextColor(getColor(R.color.background));
        TVSeriesSearchEditText.setHint("Search a T.V. Series");
        TVSeriesSearchEditText.setHintTextColor(getColor(R.color.background));
        updateTextView = findViewById(R.id.updateTextView);
        updateDBTextView = findViewById(R.id.updateDBTextView);
        acceptUpdateButton = findViewById(R.id.acceptUpdateButton);
        acceptUpdateDBButton = findViewById(R.id.acceptUpdateDBButton);
    }

    @Override
    protected void setAdapters() {
        super.setAdapters();
        sortAdapter = new SortAdapter(this);
        sortListView.setAdapter(sortAdapter);
        searchAdapter = new SearchAdapter(this);
        addSearchListView.setAdapter(searchAdapter);
    }

    @Override
    protected void setAdapter() {
        adapter = new TVSeriesAdapter(this);
    }

    @Override
    protected void startDeleteButtonAction() {
        super.setButtonsClickable(false);
        sortButton.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        updateButton.setVisibility(View.INVISIBLE);
        updateDBButton.setVisibility(View.INVISIBLE);
        super.startDeleteButtonAction();
    }

    @Override
    protected void endDeleteButtonAction() {
        forDeleteTVSeriesList.clear();
        sortButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.VISIBLE);
        updateDBButton.setVisibility(View.VISIBLE);
        super.setButtonsClickable(true);
        super.endDeleteButtonAction();
        if(adapter.getCount() == 0) {
            ((TextView) findViewById(R.id.footerListTextView)).setText(getString(R.string.noTVSeries));
            findViewById(R.id.showArrowImageView).setVisibility(View.VISIBLE);
        }
        else {
            ((TextView) findViewById(R.id.footerListTextView)).setText(getString(R.string.theEnd));
            findViewById(R.id.showArrowImageView).setVisibility(View.INVISIBLE);
        }
        updateTotals();
    }

    @Override
    protected void deleteButtonAction() {
        for(TVSeries tvSeries : forDeleteTVSeriesList) {
            Global.database.dao().deleteTVSeries(tvSeries);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setButtonsClickable(boolean bool) {
        super.setButtonsClickable(bool);
        sortButton.setClickable(bool);
        searchButton.setClickable(bool);
        updateButton.setClickable(bool);
        updateDBButton.setClickable(bool);
    }

    private void updateTotals() {
        ((TextView) findViewById(R.id.totalNrTVSeriesTextView)).setText(String.format(Locale.getDefault(), "%d", Global.database.dao().getNrOfTVSeries()));
        ((TextView) findViewById(R.id.totalNrSeasonsTextView)).setText(String.format(Locale.getDefault(), "%d", Global.database.dao().getNrOfSeasons()));
        ((TextView) findViewById(R.id.totalNrEpisodesTextView)).setText(String.format(Locale.getDefault(), "%d", Global.database.dao().getNrOFEpisodes()));
    }

    protected void addButtonAction() {
        openSearchView();
        addSearchListView.setVisibility(View.VISIBLE);
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

    public void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }

    public void resetAddSearchEditText() {
        TVSeriesSearchEditText.setText("");
    }

    public void setHeaderVisible() {
        headerTextView.setVisibility(View.VISIBLE);
    }

    public void closeSortList() {
        sortListView.setVisibility(View.INVISIBLE);
        setButtonsClickable(true);
        adapter.notifyDataSetChanged();
    }

    public void closeSearchView() {
        TVSeriesSearchView.setVisibility(View.INVISIBLE);
        addSearchListView.setVisibility(View.INVISIBLE);
        headerTextView.setVisibility(View.VISIBLE);
        setButtonsClickable(true);
        if(searchMode) {
            searchMode = false;
            adapter.notifyDataSetChanged();
        }
    }

    private void openSearchView() {
        TVSeriesSearchView.setVisibility(View.VISIBLE);
        TVSeriesSearchView.setIconified(false);
        setButtonsClickable(false);
        headerTextView.setVisibility(View.INVISIBLE);
        if(searchMode)
            TVSeriesSearchEditText.setHint("Search a T.V. Series");
        else
            TVSeriesSearchEditText.setHint("Add a T.V. Series");
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

    public boolean isAddSearchListView() {
        return addSearchListView.getVisibility() == View.INVISIBLE;
    }

    public boolean isSortListViewInvisible() {
        return sortListView.getVisibility() == View.INVISIBLE;
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
        acceptUpdateButton.setVisibility(View.INVISIBLE);
        acceptUpdateDBButton.setVisibility(View.INVISIBLE);
        super.closeForm();
    }

    private Calendar setCalendarForUpdate() {
        Calendar calendarTVSeries = Calendar.getInstance();
        calendarTVSeries.setTimeInMillis(new Date().getTime());
        calendarTVSeries.add(Calendar.SECOND, 10);
        return calendarTVSeries;
    }
}