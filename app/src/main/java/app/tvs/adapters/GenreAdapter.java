package app.tvs.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.tvseries.R;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.genreTextView);
        }
    }

    private List<String> genres;

    GenreAdapter(List<String> genres) {
        this.genres = genres;
    }

    @NonNull
    @Override
    public GenreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.element_genre, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GenreAdapter.ViewHolder viewHolder, int i) {
        final String genre = genres.get(i);
        viewHolder.nameTextView.setText(genre);
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }
}
