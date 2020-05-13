package app.tvs.adapters;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import java.util.List;
import java.util.Locale;

import app.tvs.Global;
import app.tvs.activities.EpisodesActivity;
import app.tvs.activities.SeasonsActivity;
import app.tvs.entities.Season;
import app.tvs.entities.TVSeries;
import app.tvseries.R;

public class SeasonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nrSeasonElemSeasonTextView;
        private TextView yearsElemSeasonTextView;
        private TextView nrEpisodesElemSeasonTextView;
        private TextView nrEpisodesSeenElemSeasonTextView;
        private TextView nrEpisodesTotalElemSeasonTextView;
        private Button goToEpisodesElemSeasonButton;
        private ConstraintLayout elementSeason;
        private CheckBox checkForDeleteSeasonsCheckBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nrSeasonElemSeasonTextView = itemView.findViewById(R.id.nrSeasonElemSeasonTextView);
            yearsElemSeasonTextView = itemView.findViewById(R.id.yearsElemSeasonTextView);
            nrEpisodesElemSeasonTextView = itemView.findViewById(R.id.nrEpisodesElemSeasonTextView);
            nrEpisodesSeenElemSeasonTextView = itemView.findViewById(R.id.nrEpisodesSeenElemSeasonTextView);
            nrEpisodesTotalElemSeasonTextView = itemView.findViewById(R.id.nrEpisodesTotalElemSeasonTextView);
            goToEpisodesElemSeasonButton = itemView.findViewById(R.id.goToEpisodesElemSeasonButton);
            elementSeason = itemView.findViewById(R.id.elementSeason);
            checkForDeleteSeasonsCheckBox = itemView.findViewById(R.id.checkForDeleteSeasonsCheckBox);
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

    private static final int TYPE_SEASON = 1;
    private static final int TYPE_FOOTER = 2;

    private List<Season> seasons;
    private SeasonsActivity seasonsActivity;
    private List<Season> forDeleteSeasonsList;

    public SeasonAdapter(List<Season> seasons, SeasonsActivity seasonsActivity) {
        this.seasons = seasons;
        this.seasonsActivity = seasonsActivity;
        this.forDeleteSeasonsList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int i) {
        if (i == seasons.size()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_SEASON;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return (viewType == TYPE_FOOTER) ? (new FooterViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.footer_list, viewGroup, false))) : (new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.element_season, viewGroup, false)));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (getItemViewType(i) == TYPE_FOOTER) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
            TVSeries parent = seasonsActivity.getSeasonsParent();
            if (seasons.size() == 0) {
                footerViewHolder.footerListTextView.setText(seasonsActivity.getString(R.string.noSeason));
                footerViewHolder.showArrowImageView.setVisibility(View.VISIBLE);
            } else {
                if (parent.getNrEpisodes() == parent.getEpisodesSeen()) {
                    if (parent.getState() == Global.STATES.FINISHED) {
                        footerViewHolder.footerListTextView.setText(seasonsActivity.getString(R.string.finished));
                    } else {
                        footerViewHolder.footerListTextView.setText(seasonsActivity.getString(R.string.upToDate));
                    }
                } else {
                    footerViewHolder.footerListTextView.setText(seasonsActivity.getString(R.string.theEnd));
                }
                footerViewHolder.showArrowImageView.setVisibility(View.INVISIBLE);
            }
        } else {
            ViewHolder seasonViewHolder = (ViewHolder) viewHolder;
            final Season season = seasons.get(i);

            seasonViewHolder.nrSeasonElemSeasonTextView.setText(String.format(Locale.getDefault(), seasonsActivity.getString(R.string.nrOFSeasonFormat), season.getIndex()));
            seasonViewHolder.yearsElemSeasonTextView.setText(String.format(Locale.getDefault(), seasonsActivity.getString(R.string.yearsEpisodeFormat), season.getStartYear(), (season.getStartYear() == season.getEndYear()) ? (StringUtils.EMPTY) : (seasonsActivity.getString(R.string.minus)+season.getEndYear())));
            seasonViewHolder.nrEpisodesElemSeasonTextView.setText(String.format(Locale.getDefault(), seasonsActivity.getString(R.string.intFormat), season.getNrEpisodes()));
            seasonViewHolder.nrEpisodesSeenElemSeasonTextView.setText(String.format(Locale.getDefault(), seasonsActivity.getString(R.string.intFormat), season.getNrEpisodesSeen()));
            seasonViewHolder.nrEpisodesTotalElemSeasonTextView.setText(String.format(Locale.getDefault(), seasonsActivity.getString(R.string.nrTotalEpisodeFormat), season.getNrTotalOfEpisodes()));

            seasonViewHolder.goToEpisodesElemSeasonButton.setOnClickListener(v -> {
                Intent intent = new Intent(seasonsActivity.getContext(), EpisodesActivity.class);
                intent.putExtra(seasonsActivity.getString(R.string.sharingSeasonId), season.getId());
                seasonViewHolder.elementSeason.setBackgroundResource(R.color.header);
                new Handler().postDelayed(() -> seasonViewHolder.elementSeason.setBackgroundResource(R.color.elemList), 30);
                seasonsActivity.startActivity(intent);
                seasonsActivity.overridePendingTransition(R.anim.right_in_animation, R.anim.left_out_animation);
            });
            seasonViewHolder.goToEpisodesElemSeasonButton.setClickable(seasonsActivity.isFormLayoutInvisible() && seasonsActivity.isProgressLayoutInvisible());
            if (seasonsActivity.isDeleteMode()) {
                seasonViewHolder.goToEpisodesElemSeasonButton.setVisibility(View.INVISIBLE);
                seasonViewHolder.checkForDeleteSeasonsCheckBox.setVisibility(View.VISIBLE);
            } else {
                seasonViewHolder.goToEpisodesElemSeasonButton.setVisibility(View.VISIBLE);
                seasonViewHolder.checkForDeleteSeasonsCheckBox.setVisibility(View.INVISIBLE);
            }

            seasonViewHolder.checkForDeleteSeasonsCheckBox.setOnCheckedChangeListener(null);
            seasonViewHolder.checkForDeleteSeasonsCheckBox.setChecked(forDeleteSeasonsList.contains(season));
            seasonViewHolder.checkForDeleteSeasonsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    forDeleteSeasonsList.add(season);
                } else {
                    forDeleteSeasonsList.remove(season);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return seasons.size() + 1;
    }

    public void removeSeasons(TVSeries parent) {
        int episodesFromDeletedSeasons = 0;
        for (Season season : forDeleteSeasonsList) {
            Global.database.dao().deleteSeason(season);
            episodesFromDeletedSeasons += season.getNrEpisodesSeen();
        }
        parent.removeSeasonsSeen(forDeleteSeasonsList.size());
        parent.removeEpisodesSeen(episodesFromDeletedSeasons);
        parent.updateYearsSeen();
        if (Global.database.dao().getNrEpisodesForTVSeries(parent.getId()) == 0) {
            parent.setLastTimeEpisodeSeen(Long.MIN_VALUE);
        } else {
            parent.setLastTimeEpisodeSeen(Global.database.dao().getLastTimeAddedEpisodeForTVSeriesWithId(parent.getId()));
        }
        Global.database.dao().updateTVSeries(parent);
        this.seasons.removeAll(forDeleteSeasonsList);
        forDeleteSeasonsList = new ArrayList<>();
    }

    public void updateSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public void addSeason(Season season) {
        seasons.add(season);
    }

}
