package app.tvs.adapters;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import app.tvs.Global;
import app.tvs.activities.SeasonsActivity;
import app.tvs.activities.TVSeriesActivity;
import app.tvs.entities.TVSeries;
import app.tvseries.R;

public class TVSeriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView numberElemTextView;
        private ImageView posterElemImageView;
        private TextView nameElemTextView;
        private TextView yearsElemTextView;
        private TextView nrSeasonsElemTextView;
        private TextView nrEpisodesElemTextView;
        private TextView seenYearsElemTextView;
        private TextView seenSeasonsElemTextView;
        private TextView seenEpisodesElemTextView;
        private Button goToSeasonElemButton;
        private ConstraintLayout element;
        private TextView stateTextView;
        private CheckBox checkForDeleteTVSeriesCheckBox;
        private TextView IMDBTextView;
        private ImageView seenStateImageView;
        private GradientDrawable stateDrawable;
        private RecyclerView genreRecyclerView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            numberElemTextView = itemView.findViewById(R.id.numberElemTextView);
            posterElemImageView = itemView.findViewById(R.id.posterElemImageView);
            nameElemTextView = itemView.findViewById(R.id.nameElemTextView);
            yearsElemTextView = itemView.findViewById(R.id.yearsElemTextView);
            nrSeasonsElemTextView = itemView.findViewById(R.id.nrSeasonsElemTextView);
            nrEpisodesElemTextView = itemView.findViewById(R.id.nrEpisodesElemTextView);
            seenYearsElemTextView = itemView.findViewById(R.id.seenYearsElemTextView);
            seenSeasonsElemTextView = itemView.findViewById(R.id.seenSeasonsElemTextView);
            seenEpisodesElemTextView = itemView.findViewById(R.id.seenEpisodesElemTextView);
            goToSeasonElemButton = itemView.findViewById(R.id.goToSeasonElemButton);
            element = itemView.findViewById(R.id.element);
            stateTextView = itemView.findViewById(R.id.stateTextView);
            checkForDeleteTVSeriesCheckBox = itemView.findViewById(R.id.checkForDeleteTVSeriesCheckBox);
            IMDBTextView = itemView.findViewById(R.id.IMDBTextView);
            seenStateImageView = itemView.findViewById(R.id.seenStateImageView);
            stateDrawable = (GradientDrawable) itemView.getContext().getDrawable(R.drawable.state_style);
            genreRecyclerView = itemView.findViewById(R.id.genreRecyclerView);

        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {

        private TextView footerListTextView;
        private ImageView showArrowImageView;

        FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            footerListTextView = itemView.findViewById(R.id.footerListTextView);
            showArrowImageView = itemView.findViewById(R.id.showArrowImageView);
        }
    }

    private static final int TYPE_TVSERIES = 1;
    private static final int TYPE_FOOTER = 2;

    private List<TVSeries> tvSeries;
    private TVSeriesActivity tvSeriesActivity;
    private List<TVSeries> forDeleteTVSeriesList;

    public TVSeriesAdapter(List<TVSeries> tvSeries, TVSeriesActivity tvSeriesActivity) {
        this.tvSeries = tvSeries;
        this.tvSeriesActivity = tvSeriesActivity;
        this.forDeleteTVSeriesList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int i) {
        if (i == tvSeries.size()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_TVSERIES;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return (viewType == TYPE_FOOTER) ? (new FooterViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.footer_list, viewGroup, false))) : (new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.element, viewGroup, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (getItemViewType(i) == TYPE_FOOTER) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
            if (tvSeries.size() == 0) {
                footerViewHolder.footerListTextView.setText(tvSeriesActivity.getString(R.string.noTVSeries));
                footerViewHolder.showArrowImageView.setVisibility(View.VISIBLE);
            } else {
                footerViewHolder.footerListTextView.setText(tvSeriesActivity.getString(R.string.theEnd));
                footerViewHolder.showArrowImageView.setVisibility(View.INVISIBLE);
            }
        } else {
            ViewHolder tvSeriesViewHolder = (ViewHolder) viewHolder;
            final TVSeries tvseries = tvSeries.get(i);

            tvSeriesViewHolder.genreRecyclerView.setLayoutManager(new LinearLayoutManager(tvSeriesActivity, LinearLayoutManager.HORIZONTAL, false));
            tvSeriesViewHolder.genreRecyclerView.setAdapter(new GenreAdapter(Arrays.asList(tvseries.getGenres().split(tvSeriesActivity.getString(R.string.comma)))));
            tvSeriesViewHolder.numberElemTextView.setText(String.format(Locale.getDefault(), tvSeriesActivity.getString(R.string.intFormat), i+1));
            tvSeriesViewHolder.posterElemImageView.setImageBitmap(tvseries.getBitmapImage());
            tvSeriesViewHolder.nameElemTextView.setText(tvseries.getName());
            tvSeriesViewHolder.yearsElemTextView.setText(String.format(Locale.getDefault(),tvSeriesActivity.getString(R.string.yearsEpisodesFormat), tvseries.getStartYear(), tvseries.getEndYear()));
            tvSeriesViewHolder.nrSeasonsElemTextView.setText(String.format(Locale.getDefault(), tvSeriesActivity.getString(R.string.intFormat), tvseries.getNrSeasons()));
            tvSeriesViewHolder.nrEpisodesElemTextView.setText(String.format(Locale.getDefault(), tvSeriesActivity.getString(R.string.intFormat), tvseries.getNrEpisodes()));
            tvSeriesViewHolder.seenYearsElemTextView.setText((tvseries.getEndYearSeen() == 0 && tvseries.getStartYearSeen() == 0) ? (tvSeriesActivity.getString(R.string.minus)) : (String.format(Locale.getDefault(), tvSeriesActivity.getString(R.string.intStringFormat), tvseries.getStartYearSeen(), (tvseries.getStartYearSeen() == tvseries.getEndYearSeen()) ? (StringUtils.EMPTY) : (tvSeriesActivity.getString(R.string.minus)+tvseries.getEndYearSeen()))));
            tvSeriesViewHolder.seenSeasonsElemTextView.setText(String.format(Locale.getDefault(), tvSeriesActivity.getString(R.string.intFormat), tvseries.getSeasonsSeen()));
            tvSeriesViewHolder.seenEpisodesElemTextView.setText(String.format(Locale.getDefault(), tvSeriesActivity.getString(R.string.intFormat), tvseries.getEpisodesSeen()));
            tvSeriesViewHolder.IMDBTextView.setText(String.format(Locale.getDefault(), tvSeriesActivity.getString(R.string.float1RealFormat), tvseries.getIMDBRating()));
            tvSeriesViewHolder.goToSeasonElemButton.setOnClickListener(v -> {
                Intent intent = new Intent(tvSeriesActivity.getContext(), SeasonsActivity.class);
                intent.putExtra(tvSeriesActivity.getString(R.string.sharingTVSeriesId), tvseries.getId());
                tvSeriesActivity.startActivity(intent);
                tvSeriesViewHolder.element.setBackgroundResource(R.color.header);
                new Handler().postDelayed(() -> tvSeriesViewHolder.element.setBackgroundResource(R.color.elemList), 30);
                tvSeriesActivity.overridePendingTransition(R.anim.right_in_animation, R.anim.left_out_animation);
            });
            tvSeriesViewHolder.goToSeasonElemButton.setClickable(tvSeriesActivity.getAddSearchRecyclerView() && tvSeriesActivity.isProgressLayoutInvisible() && tvSeriesActivity.isSortListViewInvisible() && tvSeriesActivity.isUpdateFormInvisible());
            if (tvSeriesViewHolder.stateDrawable != null) {
                if (tvseries.getState() == Global.STATES.ON_GOING) {
                    tvSeriesViewHolder.stateDrawable.setStroke(1, tvSeriesActivity.getColor(R.color.onGoingState));
                    tvSeriesViewHolder.stateTextView.setTextColor(tvSeriesActivity.getColor(R.color.onGoingState));
                    tvSeriesViewHolder.stateTextView.setText(tvSeriesActivity.getString(R.string.onGoing));
                } else if (tvseries.getState() == Global.STATES.IN_STAND_BY) {
                    tvSeriesViewHolder.stateDrawable.setStroke(1, tvSeriesActivity.getColor(R.color.inStandByState));
                    tvSeriesViewHolder.stateTextView.setTextColor(tvSeriesActivity.getColor(R.color.inStandByState));
                    tvSeriesViewHolder.stateTextView.setText(tvSeriesActivity.getString(R.string.inStandBy));
                } else {
                    tvSeriesViewHolder.stateDrawable.setStroke(1, tvSeriesActivity.getColor(R.color.finishedState));
                    tvSeriesViewHolder.stateTextView.setTextColor(tvSeriesActivity.getColor(R.color.finishedState));
                    tvSeriesViewHolder.stateTextView.setText(tvSeriesActivity.getString(R.string.FINISHED));
                }
            }
            tvSeriesViewHolder.stateTextView.setBackground(tvSeriesViewHolder.stateDrawable);
            if (tvSeriesActivity.isDeleteMode()) {
                tvSeriesViewHolder.goToSeasonElemButton.setVisibility(View.INVISIBLE);
                tvSeriesViewHolder.checkForDeleteTVSeriesCheckBox.setVisibility(View.VISIBLE);
            } else {
                tvSeriesViewHolder.goToSeasonElemButton.setVisibility(View.VISIBLE);
                tvSeriesViewHolder.checkForDeleteTVSeriesCheckBox.setVisibility(View.INVISIBLE);
            }

            if (tvseries.getSeenState() == Global.SEENSTATES.PLAY) {
                tvSeriesViewHolder.seenStateImageView.setImageDrawable(tvSeriesActivity.getDrawable(R.drawable.play));
            } else if (tvseries.getSeenState() == Global.SEENSTATES.PAUSE) {
                tvSeriesViewHolder.seenStateImageView.setImageDrawable(tvSeriesActivity.getDrawable(R.drawable.pause));
            } else {
                tvSeriesViewHolder.seenStateImageView.setImageDrawable(tvSeriesActivity.getDrawable(R.drawable.up_to_date));
            }

            tvSeriesViewHolder.checkForDeleteTVSeriesCheckBox.setOnCheckedChangeListener(null);
            tvSeriesViewHolder.checkForDeleteTVSeriesCheckBox.setChecked(forDeleteTVSeriesList.contains(tvseries));
            tvSeriesViewHolder.checkForDeleteTVSeriesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    forDeleteTVSeriesList.add(tvseries);
                } else {
                    forDeleteTVSeriesList.remove(tvseries);
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return tvSeries.size() + 1;
    }

    public void updateTVSeries(List<TVSeries> tvSeries) {
        this.tvSeries = tvSeries;
    }

    public void removeTVSeries() {
        for (TVSeries tvSeries : forDeleteTVSeriesList) {
            Global.database.dao().deleteTVSeries(tvSeries);
        }
        tvSeries.removeAll(forDeleteTVSeriesList);
        forDeleteTVSeriesList = new ArrayList<>();
    }

    public void addTVSeries(TVSeries tvSeries) {
        this.tvSeries.add(tvSeries);
    }

}
