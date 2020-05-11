package app.tvs.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import app.tvs.Global;
import app.tvs.activities.EpisodesActivity;
import app.tvs.entities.Episode;
import app.tvseries.R;

public class EpisodeAdapter extends BaseAdapter {

    private static class ViewHolder {
        TextView numberElemEpisodeTextView;
        ImageView posterElemEpisodeImageView;
        TextView nameElemEpisodeTextView;
        TextView dateElemEpisodeTextView;
        TextView durationElemEpisodeTextView;
        TextView descriptionElemEpisodeTextView;
        TextView IMDBElemEpisodeTextView;
        ConstraintLayout elementEpisode;
        CheckBox checkForDeleteEpisodeCheckBox;
    }

    private EpisodesActivity activity;

    public EpisodeAdapter(EpisodesActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return Global.database.dao().getNrEpisodesForSeason(activity.getEpisodesParent().getId());
    }

    @Override
    public Episode getItem(int position) {
        return Global.database.dao().getEpisodesWithIdSeasonAscByIndex(activity.getEpisodesParent().getId()).get(position);
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
                convertView = layoutInflater.inflate(R.layout.element_episode,parent, false);
            }

            if(convertView != null) {
                viewHolder.numberElemEpisodeTextView = convertView.findViewById(R.id.numberElemEpisodeTextView);
                viewHolder.posterElemEpisodeImageView = convertView.findViewById(R.id.posterElemEpisodeImageView);
                viewHolder.nameElemEpisodeTextView = convertView.findViewById(R.id.nameElemEpisodeTextView);
                viewHolder.dateElemEpisodeTextView = convertView.findViewById(R.id.dateElemEpisodeTextView);
                viewHolder.durationElemEpisodeTextView = convertView.findViewById(R.id.durationElemEpisodeTextView);
                viewHolder.descriptionElemEpisodeTextView = convertView.findViewById(R.id.descriptionElemEpisodeTextView);
                viewHolder.IMDBElemEpisodeTextView = convertView.findViewById(R.id.IMDBElemEpisodeTextView);
                viewHolder.checkForDeleteEpisodeCheckBox = convertView.findViewById(R.id.checkForDeleteEpisodeCheckBox);
                viewHolder.elementEpisode = convertView.findViewById(R.id.elementEpisode);

                convertView.setTag(viewHolder);
            }
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Episode episode = getItem(position);

        viewHolder.posterElemEpisodeImageView.setImageBitmap(episode.getBitmapImage());
        viewHolder.numberElemEpisodeTextView.setText(String.format(Locale.getDefault(), "Episode %d", episode.getIndex()));
        viewHolder.nameElemEpisodeTextView.setText(episode.getName());
        viewHolder.dateElemEpisodeTextView.setText(episode.getReleaseDate());
        viewHolder.durationElemEpisodeTextView.setText(episode.getDuration());
        viewHolder.descriptionElemEpisodeTextView.setText(episode.getDescription());
        viewHolder.IMDBElemEpisodeTextView.setText(String.format(Locale.getDefault(), "%.1f", episode.getIMDBRating()));
        if(activity.isDeleteMode()) {
            viewHolder.descriptionElemEpisodeTextView.setVisibility(View.INVISIBLE);
            viewHolder.checkForDeleteEpisodeCheckBox.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.descriptionElemEpisodeTextView.setVisibility(View.VISIBLE);
            viewHolder.checkForDeleteEpisodeCheckBox.setVisibility(View.INVISIBLE);
            if(viewHolder.checkForDeleteEpisodeCheckBox.isChecked()) {
                activity.removeForDeleteEpisodesList(episode);
                viewHolder.checkForDeleteEpisodeCheckBox.setChecked(false);
                viewHolder.elementEpisode.setBackgroundResource(R.color.elemList);
            }
        }
        viewHolder.checkForDeleteEpisodeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(viewHolder.checkForDeleteEpisodeCheckBox.isChecked()) {
                activity.addForDeleteEpisodesList(episode);
                viewHolder.elementEpisode.setBackgroundResource(R.color.header);
            }
            else {
                viewHolder.elementEpisode.setBackgroundResource(R.color.elemList);
                activity.removeForDeleteEpisodesList(episode);
            }
        });
        return convertView;
    }
}
