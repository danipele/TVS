package app.tvs.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import app.tvs.Global;
import app.tvseries.R;
import app.tvs.activities.SeasonsActivity;
import app.tvs.activities.TVSeriesActivity;
import app.tvs.entities.TVSeries;

public class TVSeriesAdapter extends BaseAdapter {

    private static class ViewHolder {
        TextView numberElemTextView;
        ImageView posterElemImageView;
        TextView nameElemTextView;
        TextView yearsElemTextView;
        TextView nrSeasonsElemTextView;
        TextView nrEpisodesElemTextView;
        TextView seenYearsElemTextView;
        TextView seenSeasonsElemTextView;
        TextView seenEpisodesElemTextView;
        Button goToSeasonElemButton;
        ConstraintLayout element;
        TextView stateTextView;
        CheckBox checkForDeleteTVSeriesCheckBox;
        TextView IMDBTextView;
        ImageView seenStateImageView;
        GradientDrawable stateDrawable;
    }

    private TVSeriesActivity activity;

    public TVSeriesAdapter(TVSeriesActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        if(activity.isNotSearchMode())
            return Global.database.dao().getNrOfTVSeries();
        else
            return activity.getSearchedTVSeries().size();
    }

    @Override
    public TVSeries getItem(int position) {
        if(activity.isNotSearchMode())
            return activity.getSorts().getActualSort().getSortedList().get(position);
        else
            return activity.getSearchedTVSeries().get(position);
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
                convertView = layoutInflater.inflate(R.layout.element,parent, false);
            }

            if(convertView != null) {
                viewHolder.numberElemTextView = convertView.findViewById(R.id.numberElemTextView);
                viewHolder.posterElemImageView = convertView.findViewById(R.id.posterElemImageView);
                viewHolder.nameElemTextView = convertView.findViewById(R.id.nameElemTextView);
                viewHolder.yearsElemTextView = convertView.findViewById(R.id.yearsElemTextView);
                viewHolder.nrSeasonsElemTextView = convertView.findViewById(R.id.nrSeasonsElemTextView);
                viewHolder.nrEpisodesElemTextView = convertView.findViewById(R.id.nrEpisodesElemTextView);
                viewHolder.seenYearsElemTextView = convertView.findViewById(R.id.seenYearsElemTextView);
                viewHolder.seenSeasonsElemTextView = convertView.findViewById(R.id.seenSeasonsElemTextView);
                viewHolder.seenEpisodesElemTextView = convertView.findViewById(R.id.seenEpisodesElemTextView);
                viewHolder.goToSeasonElemButton = convertView.findViewById(R.id.goToSeasonElemButton);
                viewHolder.element = convertView.findViewById(R.id.element);
                viewHolder.stateTextView = convertView.findViewById(R.id.stateTextView);
                viewHolder.checkForDeleteTVSeriesCheckBox = convertView.findViewById(R.id.checkForDeleteTVSeriesCheckBox);
                viewHolder.IMDBTextView = convertView.findViewById(R.id.IMDBTextView);
                viewHolder.seenStateImageView = convertView.findViewById(R.id.seenStateImageView);
                viewHolder.stateDrawable = (GradientDrawable) activity.getDrawable(R.drawable.state_style);

                convertView.setTag(viewHolder);
            }
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final TVSeries tvseries = getItem(position);

        viewHolder.numberElemTextView.setText(String.format(Locale.getDefault(), "%d", position+1));
        viewHolder.posterElemImageView.setImageBitmap(tvseries.getBitmapImage());
        viewHolder.nameElemTextView.setText(tvseries.getName());
        viewHolder.yearsElemTextView.setText(String.format(Locale.getDefault(),"- %d-%d -", tvseries.getStartYear(), tvseries.getEndYear()));
        viewHolder.nrSeasonsElemTextView.setText(String.format(Locale.getDefault(), "%d", tvseries.getNrSeasons()));
        viewHolder.nrEpisodesElemTextView.setText(String.format(Locale.getDefault(), "%d", tvseries.getNrEpisodes()));
        viewHolder.seenYearsElemTextView.setText((tvseries.getEndYearSeen() == 0 && tvseries.getStartYearSeen() == 0)?("-"):(String.format(Locale.getDefault(), "%d%s", tvseries.getStartYearSeen(), (tvseries.getStartYearSeen() == tvseries.getEndYearSeen())?(""):("-"+tvseries.getEndYearSeen()))));
        viewHolder.seenSeasonsElemTextView.setText(String.format(Locale.getDefault(), "%d", tvseries.getSeasonsSeen()));
        viewHolder.seenEpisodesElemTextView.setText(String.format(Locale.getDefault(), "%d", tvseries.getEpisodesSeen()));
        viewHolder.IMDBTextView.setText(String.format(Locale.getDefault(), "%.1f", tvseries.getIMDBRating()));
        viewHolder.goToSeasonElemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getContext(), SeasonsActivity.class);
                intent.putExtra(activity.getString(R.string.sharingTVSeriesId), tvseries.getId());
                activity.startActivity(intent);
                viewHolder.element.setBackgroundResource(R.color.header);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.element.setBackgroundResource(R.color.elemList);
                    }
                }, 30);
                activity.overridePendingTransition(R.anim.right_in_animation, R.anim.left_out_animation);
            }
        });
        viewHolder. goToSeasonElemButton.setClickable(activity.isAddSearchListView() && activity.isProgressLayoutInvisible() && activity.isSortListViewInvisible() && activity.isUpdateFormInvisible());
        if(viewHolder.stateDrawable != null) {
            if(tvseries.getState() == Global.STATES.ON_GOING) {
                viewHolder.stateDrawable.setStroke(1, activity.getColor(R.color.onGoingState));
                viewHolder.stateTextView.setTextColor(activity.getColor(R.color.onGoingState));
                viewHolder.stateTextView.setText(activity.getString(R.string.onGoing));
            }
            else if(tvseries.getState() == Global.STATES.IN_STAND_BY) {
                viewHolder.stateDrawable.setStroke(1, activity.getColor(R.color.inStandByState));
                viewHolder.stateTextView.setTextColor(activity.getColor(R.color.inStandByState));
                viewHolder.stateTextView.setText(activity.getString(R.string.inStandBy));
            }
            else {
                viewHolder.stateDrawable.setStroke(1, activity.getColor(R.color.finishedState));
                viewHolder.stateTextView.setTextColor(activity.getColor(R.color.finishedState));
                viewHolder.stateTextView.setText(activity.getString(R.string.FINISHED));
            }
        }
        viewHolder.stateTextView.setBackground(viewHolder.stateDrawable);
        if(activity.isDeleteMode()) {
            viewHolder.goToSeasonElemButton.setVisibility(View.INVISIBLE);
            viewHolder.checkForDeleteTVSeriesCheckBox.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.goToSeasonElemButton.setVisibility(View.VISIBLE);
            viewHolder.checkForDeleteTVSeriesCheckBox.setVisibility(View.INVISIBLE);
            if(viewHolder.checkForDeleteTVSeriesCheckBox.isChecked()) {
                activity.removeForDeleteTVSeriesList(tvseries);
                viewHolder.checkForDeleteTVSeriesCheckBox.setChecked(false);
                viewHolder.element.setBackgroundResource(R.color.elemList);
            }
        }

        if(tvseries.getSeenState() == Global.SEENSTATES.PLAY) {
            viewHolder.seenStateImageView.setImageDrawable(activity.getDrawable(R.drawable.play));
        }
        else if(tvseries.getSeenState() == Global.SEENSTATES.PAUSE) {
            viewHolder.seenStateImageView.setImageDrawable(activity.getDrawable(R.drawable.pause));
        }
        else {
            viewHolder.seenStateImageView.setImageDrawable(activity.getDrawable(R.drawable.up_to_date));
        }

        viewHolder.checkForDeleteTVSeriesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(viewHolder.checkForDeleteTVSeriesCheckBox.isChecked()) {
                    activity.addForDeleteTVSeriesList(tvseries);
                    viewHolder.element.setBackgroundResource(R.color.header);
                }
                else {
                    viewHolder.element.setBackgroundResource(R.color.elemList);
                    activity.removeForDeleteTVSeriesList(tvseries);
                }
            }
        });
        return convertView;
    }

}
