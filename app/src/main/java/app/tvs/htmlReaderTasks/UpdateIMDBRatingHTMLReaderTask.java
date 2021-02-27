package app.tvs.htmlReaderTasks;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import app.tvs.Global;
import app.tvs.activities.MainActivity;
import app.tvs.entities.TVSeries;
import app.tvs.services.NotificationService;
import app.tvs.services.UpdateTVSeriesService;
import app.tvseries.R;

public class UpdateIMDBRatingHTMLReaderTask extends HTMLReaderTask {

    protected int updated;

    public UpdateIMDBRatingHTMLReaderTask(MainActivity activity) {
        super(activity);
        updated = 0;
    }

    @Override
    protected String getToastMessage() {
        return activity.getString(R.string.updateTVSeriesError);
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            List<TVSeries> TVSeriesList = Global.database.dao().getFinishedTVSeries();

            for (TVSeries tvSeries : TVSeriesList)
                updated += UpdateTVSeriesService.updateIMDBRating(activity, tvSeries);
            Global.database.dao().updateTVSeriesList(TVSeriesList);
            NotificationService.addNotification(setFinalMessage(), activity, activity.getString(R.string.updateTVSeriesChannel), activity.getString(R.string.updateTVSeriesName), activity.getString(R.string.updateTVSeriesDescription));
            return StringUtils.EMPTY;
        } catch (Exception e) {
            NotificationService.addNotification(getToastMessage(), activity, activity.getString(R.string.updateTVSeriesChannel), activity.getString(R.string.updateTVSeriesName), activity.getString(R.string.updateTVSeriesDescription));
            return getToastMessage();
        }
    }

    @Override
    protected String setStartMessage() {
        return activity.getString(R.string.updating);
    }

    @Override
    protected String setFinalMessage() {
        return activity.getString(R.string.updated) + " " + updated + " " + activity.getString(R.string.tvSeries);
    }

}
