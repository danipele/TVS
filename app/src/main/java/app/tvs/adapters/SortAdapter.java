package app.tvs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import app.tvseries.R;
import app.tvs.activities.TVSeriesActivity;
import app.tvs.sorts.Sort;

public class SortAdapter extends BaseAdapter {

    private TVSeriesActivity activity;

    public SortAdapter(TVSeriesActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return activity.getSorts().getSortsCount();
    }

    @Override
    public Sort getItem(int position) {
        return activity.getSorts().getSort(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = ((LayoutInflater)activity.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            if(layoutInflater != null) {
                convertView = layoutInflater.inflate(R.layout.sort_list,parent, false);
            }
        }
        if(convertView != null) {
            final Sort sort = getItem(position);
            ((TextView) convertView.findViewById(R.id.sortTypeTextView)).setText(sort.getName());
            if(sort.getClass() == activity.getSorts().getActualSort().getClass())
                convertView.findViewById(R.id.sortLayout).setBackgroundColor(activity.getColor(R.color.adding));
            else
                convertView.findViewById(R.id.sortLayout).setBackgroundColor(activity.getColor(R.color.white));
            convertView.findViewById(R.id.sortLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getSorts().setActualSort(sort);
                    activity.closeSortList();
                    activity.notifySortAdapter();
                    activity.notifyAdapter();
                }
            });
        }
        return convertView;
    }
}
