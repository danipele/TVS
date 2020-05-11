package app.tvs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import app.tvs.Global;
import app.tvseries.R;
import app.tvs.activities.TVSeriesActivity;
import app.tvs.entities.TVSeries;
import app.tvs.entities.TVSeriesShort;
import app.tvs.htmlReaderTasks.TVSeriesHTMLReaderTask;

public class SearchAdapter extends BaseAdapter {

    private TVSeriesActivity activity;

    public SearchAdapter(TVSeriesActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return activity.getSearchedForAddTVSeries().size();
    }

    @Override
    public TVSeriesShort getItem(int position) {
        return activity.getSearchedForAddTVSeries().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = ((LayoutInflater) activity.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            if (layoutInflater != null) {
                convertView = layoutInflater.inflate(R.layout.search_to_add_element, parent, false);
            }
        }

        final TVSeriesShort tvSeriesShort = getItem(position);
        if (convertView != null) {
            ((TextView) convertView.findViewById(R.id.nameAddTextView)).setText(tvSeriesShort.getName());
            ((TextView) convertView.findViewById(R.id.yearsAddTextView)).setText(String.format(Locale.getDefault(), "%s - %s", tvSeriesShort.getYearStart(), (tvSeriesShort.getYearEnd().equals(activity.getString(R.string.unknownValue))) ? ("") : (tvSeriesShort.getYearEnd())));
            convertView.findViewById(R.id.searchAddLayout).setOnClickListener(v -> {
                String url = activity.getString(R.string.linkStandard) + tvSeriesShort.getId() + "/";
                if(notExistsAlready(url)) {
                    new TVSeriesHTMLReaderTask(activity, url).execute();
                    activity.resetAddSearchEditText();
                }
                else {
                    Toast.makeText(activity, activity.getString(R.string.alreadyExists), Toast.LENGTH_LONG).show();
                }
            });
        }

        return convertView;
    }

    private boolean notExistsAlready(String url) {
        List<TVSeries> TVSeriesList = Global.database.dao().getTVSeries();
        for(TVSeries tvseries : TVSeriesList)
            if(tvseries.getIMDBLink().equals(url))
                return false;
        return true;
    }
}
