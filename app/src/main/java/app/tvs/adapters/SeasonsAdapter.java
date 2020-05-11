package app.tvs.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Locale;

import app.tvs.Global;
import app.tvseries.R;
import app.tvs.activities.EpisodesActivity;
import app.tvs.activities.SeasonsActivity;
import app.tvs.entities.Season;

public class SeasonsAdapter extends BaseAdapter {

    private static class ViewHolder {
        TextView nrSeasonElemSeasonTextView;
        TextView yearsElemSeasonTextView;
        TextView nrEpisodesElemSeasonTextView;
        TextView nrEpisodesSeenElemSeasonTextView;
        TextView nrEpisodesTotalElemSeasonTextView;
        Button goToEpisodesElemSeasonButton;
        ConstraintLayout elementSeason;
        CheckBox checkForDeleteSeasonsCheckBox;
    }

    private SeasonsActivity activity;

    public SeasonsAdapter(SeasonsActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return Global.database.dao().getNrOfSeasonsForTVSeries(activity.getSeasonsParent().getId());
    }

    @Override
    public Season getItem(int position) {
        return Global.database.dao().getSeasonsWithIdTVSeriesAscByIndex(activity.getSeasonsParent().getId()).get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = ((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            if(layoutInflater != null) {
                convertView = layoutInflater.inflate(R.layout.element_season,parent, false);
            }
            if(convertView != null) {
                viewHolder.nrSeasonElemSeasonTextView = convertView.findViewById(R.id.nrSeasonElemSeasonTextView);
                viewHolder.yearsElemSeasonTextView = convertView.findViewById(R.id.yearsElemSeasonTextView);
                viewHolder.nrEpisodesElemSeasonTextView = convertView.findViewById(R.id.nrEpisodesElemSeasonTextView);
                viewHolder.nrEpisodesSeenElemSeasonTextView = convertView.findViewById(R.id.nrEpisodesSeenElemSeasonTextView);
                viewHolder.nrEpisodesTotalElemSeasonTextView = convertView.findViewById(R.id.nrEpisodesTotalElemSeasonTextView);
                viewHolder.goToEpisodesElemSeasonButton = convertView.findViewById(R.id.goToEpisodesElemSeasonButton);
                viewHolder.elementSeason = convertView.findViewById(R.id.elementSeason);
                viewHolder.checkForDeleteSeasonsCheckBox = convertView.findViewById(R.id.checkForDeleteSeasonsCheckBox);

                convertView.setTag(viewHolder);
            }
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final Season season = getItem(position);

        viewHolder.nrSeasonElemSeasonTextView.setText(String.format(Locale.getDefault(), "Season %d", season.getIndex()));
        viewHolder.yearsElemSeasonTextView.setText(String.format(Locale.getDefault(), "- %d%s -", season.getStartYear(), (season.getStartYear() == season.getEndYear())?(""):("-"+season.getEndYear())));
        viewHolder.nrEpisodesElemSeasonTextView.setText(String.format(Locale.getDefault(), "%d", season.getNrEpisodes()));
        viewHolder.nrEpisodesSeenElemSeasonTextView.setText(String.format(Locale.getDefault(), "%d", season.getNrEpisodesSeen()));
        viewHolder.nrEpisodesTotalElemSeasonTextView.setText(String.format(Locale.getDefault(), "out of %d", season.getNrTotalOfEpisodes()));

        viewHolder.goToEpisodesElemSeasonButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity.getContext(), EpisodesActivity.class);
            intent.putExtra(activity.getString(R.string.sharingSeasonId), season.getId());
            viewHolder.elementSeason.setBackgroundResource(R.color.header);
            new Handler().postDelayed(() -> viewHolder.elementSeason.setBackgroundResource(R.color.elemList), 30);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.right_in_animation, R.anim.left_out_animation);
        });
        viewHolder.goToEpisodesElemSeasonButton.setClickable(activity.isFormLayoutInvisible() && activity.isProgressLayoutInvisible());
        if(activity.isDeleteMode()) {
            viewHolder.goToEpisodesElemSeasonButton.setVisibility(View.INVISIBLE);
            viewHolder.checkForDeleteSeasonsCheckBox.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.goToEpisodesElemSeasonButton.setVisibility(View.VISIBLE);
            viewHolder.checkForDeleteSeasonsCheckBox.setVisibility(View.INVISIBLE);
            if(viewHolder.checkForDeleteSeasonsCheckBox.isChecked()) {
                activity.removeForDeleteSeasonsList(season);
                viewHolder.checkForDeleteSeasonsCheckBox.setChecked(false);
                viewHolder.elementSeason.setBackgroundResource(R.color.elemList);
            }
        }
        viewHolder.checkForDeleteSeasonsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(viewHolder.checkForDeleteSeasonsCheckBox.isChecked()) {
                activity.addForDeleteSeasonsList(season);
                viewHolder.elementSeason.setBackgroundResource(R.color.header);
            }
            else {
                viewHolder.elementSeason.setBackgroundResource(R.color.elemList);
                activity.removeForDeleteSeasonsList(season);
            }
        });
        return convertView;    }
}
