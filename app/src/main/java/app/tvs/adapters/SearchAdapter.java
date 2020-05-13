package app.tvs.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import app.tvs.Global;
import app.tvs.activities.TVSeriesActivity;
import app.tvs.entities.TVSeries;
import app.tvs.entities.TVSeriesShort;
import app.tvs.htmlReaderTasks.TVSeriesHTMLReaderTask;
import app.tvseries.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameAddTextView;
        private TextView yearsAddTextView;
        private ConstraintLayout searchAddLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameAddTextView = itemView.findViewById(R.id.nameAddTextView);
            yearsAddTextView = itemView.findViewById(R.id.yearsAddTextView);
            searchAddLayout = itemView.findViewById(R.id.searchAddLayout);
        }
    }

    private List<TVSeriesShort> tvSeriesShorts;
    private TVSeriesActivity tvSeriesActivity;

    public SearchAdapter(List<TVSeriesShort> tvSeriesShorts, TVSeriesActivity tvSeriesActivity) {
        this.tvSeriesShorts = tvSeriesShorts;
        this.tvSeriesActivity = tvSeriesActivity;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_to_add_element, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder viewHolder, int i) {
        final TVSeriesShort tvSeriesShort = tvSeriesShorts.get(i);
        viewHolder.nameAddTextView.setText(tvSeriesShort.getName());
        viewHolder.yearsAddTextView.setText(String.format(Locale.getDefault(), "%s - %s", tvSeriesShort.getYearStart(), (tvSeriesShort.getYearEnd().equals(tvSeriesActivity.getString(R.string.unknownValue))) ? ("") : (tvSeriesShort.getYearEnd())));
        viewHolder.searchAddLayout.setOnClickListener(v -> {
            String url = tvSeriesActivity.getString(R.string.linkStandard) + tvSeriesShort.getId() + "/";
            if (notExistsAlready(url)) {
                new TVSeriesHTMLReaderTask(tvSeriesActivity, url).execute();
                tvSeriesActivity.resetAddSearchEditText();
            } else {
                Toast.makeText(tvSeriesActivity, tvSeriesActivity.getString(R.string.alreadyExists), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean notExistsAlready(String url) {
        List<TVSeries> TVSeriesList = Global.database.dao().getTVSeries();
        for (TVSeries tvseries : TVSeriesList) {
            if (tvseries.getIMDBLink().equals(url)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return tvSeriesShorts.size();
    }
}
