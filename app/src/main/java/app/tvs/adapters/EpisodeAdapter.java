package app.tvs.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.tvs.Global;
import app.tvs.activities.EpisodesActivity;
import app.tvs.entities.Episode;
import app.tvs.entities.Season;
import app.tvs.entities.TVSeries;
import app.tvseries.R;

public class EpisodeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView numberElemEpisodeTextView;
        private ImageView posterElemEpisodeImageView;
        private TextView nameElemEpisodeTextView;
        private TextView dateElemEpisodeTextView;
        private TextView durationElemEpisodeTextView;
        private TextView descriptionElemEpisodeTextView;
        private TextView IMDBElemEpisodeTextView;
        private CheckBox checkForDeleteEpisodeCheckBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            numberElemEpisodeTextView = itemView.findViewById(R.id.numberElemEpisodeTextView);
            posterElemEpisodeImageView = itemView.findViewById(R.id.posterElemEpisodeImageView);
            nameElemEpisodeTextView = itemView.findViewById(R.id.nameElemEpisodeTextView);
            dateElemEpisodeTextView = itemView.findViewById(R.id.dateElemEpisodeTextView);
            durationElemEpisodeTextView = itemView.findViewById(R.id.durationElemEpisodeTextView);
            descriptionElemEpisodeTextView = itemView.findViewById(R.id.descriptionElemEpisodeTextView);
            IMDBElemEpisodeTextView = itemView.findViewById(R.id.IMDBElemEpisodeTextView);
            checkForDeleteEpisodeCheckBox = itemView.findViewById(R.id.checkForDeleteEpisodeCheckBox);
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

    private static final int TYPE_EPISODE = 1;
    private static final int TYPE_FOOTER = 2;

    private List<Episode> episodes;
    private EpisodesActivity episodesActivity;
    private List<Episode> forDeleteEpisodesList;

    public EpisodeAdapter(List<Episode> episodes, EpisodesActivity episodesActivity) {
        this.episodes = episodes;
        this.episodesActivity = episodesActivity;
        this.forDeleteEpisodesList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int i) {
        if (i == episodes.size()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_EPISODE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return (viewType == TYPE_FOOTER) ? (new FooterViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.footer_list, viewGroup, false))) : (new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.element_episode, viewGroup, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (getItemViewType(i) == TYPE_FOOTER) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
            Season parent = episodesActivity.getEpisodesParent();
            if (episodes.size() == 0) {
                footerViewHolder.footerListTextView.setText(episodesActivity.getString(R.string.noEpisode));
                footerViewHolder.showArrowImageView.setVisibility(View.VISIBLE);
            } else {
                if (parent.getNrEpisodes() == parent.getNrEpisodesSeen()) {
                    if (parent.getNrEpisodesSeen() == parent.getNrTotalOfEpisodes()) {
                        footerViewHolder.footerListTextView.setText(episodesActivity.getString(R.string.finished));
                    } else {
                        footerViewHolder.footerListTextView.setText(episodesActivity.getString(R.string.upToDate));
                    }
                } else {
                    footerViewHolder.footerListTextView.setText(episodesActivity.getString(R.string.theEnd));
                }
                footerViewHolder.showArrowImageView.setVisibility(View.INVISIBLE);
            }
        } else {
            ViewHolder episodeViewHolder = (ViewHolder) viewHolder;
            final Episode episode = episodes.get(i);

            episodeViewHolder.posterElemEpisodeImageView.setImageBitmap(episode.getBitmapImage());
            episodeViewHolder.numberElemEpisodeTextView.setText(String.format(Locale.getDefault(), episodesActivity.getString(R.string.nrOfEpisodeFormat), episode.getIndex()));
            episodeViewHolder.nameElemEpisodeTextView.setText(episode.getName());
            episodeViewHolder.dateElemEpisodeTextView.setText(episode.getReleaseDate());
            episodeViewHolder.durationElemEpisodeTextView.setText(episode.getDuration());
            episodeViewHolder.descriptionElemEpisodeTextView.setText(episode.getDescription());
            episodeViewHolder.IMDBElemEpisodeTextView.setText(String.format(Locale.getDefault(), episodesActivity.getString(R.string.float1RealFormat), episode.getIMDBRating()));
            if (episodesActivity.isDeleteMode()) {
                episodeViewHolder.descriptionElemEpisodeTextView.setVisibility(View.INVISIBLE);
                episodeViewHolder.checkForDeleteEpisodeCheckBox.setVisibility(View.VISIBLE);
            } else {
                episodeViewHolder.descriptionElemEpisodeTextView.setVisibility(View.VISIBLE);
                episodeViewHolder.checkForDeleteEpisodeCheckBox.setVisibility(View.INVISIBLE);
            }

            episodeViewHolder.checkForDeleteEpisodeCheckBox.setOnCheckedChangeListener(null);
            episodeViewHolder.checkForDeleteEpisodeCheckBox.setChecked(forDeleteEpisodesList.contains(episode));
            episodeViewHolder.checkForDeleteEpisodeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    forDeleteEpisodesList.add(episode);
                } else {
                    forDeleteEpisodesList.remove(episode);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return episodes.size() + 1;
    }

    public void removeEpisodes(Season parent) {
        for (Episode episode : forDeleteEpisodesList) {
            Global.database.dao().deleteEpisode(episode);
        }
        parent.removeNrEpisodesSeen(forDeleteEpisodesList.size());
        Global.database.dao().updateSeason(parent);
        TVSeries TVSeriesParent = Global.database.dao().getTVSeriesBySeasonId(parent.getId());
        TVSeriesParent.removeEpisodesSeen(forDeleteEpisodesList.size());
        if (Global.database.dao().getNrEpisodesForTVSeries(TVSeriesParent.getId()) == 0) {
            TVSeriesParent.setLastTimeEpisodeSeen(Long.MIN_VALUE);
        } else {
            TVSeriesParent.setLastTimeEpisodeSeen(Global.database.dao().getLastTimeAddedEpisodeForTVSeriesWithId(TVSeriesParent.getId()));
        }
        TVSeriesParent.setSeenState();
        Global.database.dao().updateTVSeries(TVSeriesParent);
        this.episodes.removeAll(forDeleteEpisodesList);
        forDeleteEpisodesList = new ArrayList<>();
    }

    public void addEpisode(Episode episode) {
        episodes.add(episode);
    }

}
