package app.tvs.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import app.tvs.activities.TVSeriesActivity;
import app.tvs.entities.TVSeries;
import app.tvseries.R;

public class GenreAdapter extends BaseAdapter {

    private static class ViewHolder {
        ConstraintLayout genreElement;
        TextView nameTextView;
    }

    private List<String> tvSeriesGenres;
    private TVSeriesActivity activity;

    GenreAdapter(TVSeries tvSeries, TVSeriesActivity activity) {
        this.tvSeriesGenres = Arrays.asList(tvSeries.getGenres().split(","));
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return tvSeriesGenres.size();
    }

    @Override
    public String getItem(int position) {
        return tvSeriesGenres.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if(contentView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = ((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            if(layoutInflater != null) {
                contentView = layoutInflater.inflate(R.layout.element_genre,parent, false);
            }

            if(contentView != null) {
                viewHolder.genreElement = contentView.findViewById(R.id.genreElement);
                viewHolder.nameTextView = contentView.findViewById(R.id.genreTextView);

                contentView.setTag(viewHolder);
            }
        }
        else {
            viewHolder = (ViewHolder) contentView.getTag();
        }

        final String genre = getItem(position);
        viewHolder.nameTextView.setText(genre);

        return contentView;
    }
}
