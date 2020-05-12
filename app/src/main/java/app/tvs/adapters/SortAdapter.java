package app.tvs.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.tvs.activities.TVSeriesActivity;
import app.tvs.sorts.Sort;
import app.tvs.sorts.Sorts;
import app.tvseries.R;

public class SortAdapter extends RecyclerView.Adapter<SortAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView sortTypeTextView;
        ConstraintLayout sortLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.sortTypeTextView = itemView.findViewById(R.id.sortTypeTextView);
            this.sortLayout = itemView.findViewById(R.id.sortLayout);
        }
    }

    private Sorts sorts;
    private TVSeriesActivity tvSeriesActivity;

    public SortAdapter(Sorts sorts, TVSeriesActivity tvSeriesActivity) {
        this.sorts = sorts;
        this.tvSeriesActivity = tvSeriesActivity;
    }

    @NonNull
    @Override
    public SortAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sort_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SortAdapter.ViewHolder viewHolder, int i) {
        final Sort sort = sorts.getSort(i);
        viewHolder.sortTypeTextView.setText(sort.getName());
        if(sort.getClass() == sorts.getActualSort().getClass())
            viewHolder.sortLayout.setBackgroundColor(tvSeriesActivity.getColor(R.color.adding));
        else
            viewHolder.sortLayout.setBackgroundColor(tvSeriesActivity.getColor(R.color.white));
        viewHolder.sortLayout.setOnClickListener(v -> {
            sorts.setActualSort(sort);
            tvSeriesActivity.closeSortList();
            tvSeriesActivity.notifySortAdapter();
            tvSeriesActivity.notifyChangedAdapter(tvSeriesActivity.getSorts().getActualSort().getSortedList());
        });
    }

    @Override
    public int getItemCount() {
        return sorts.getSortsCount();
    }
}
