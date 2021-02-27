package app.tvs.htmlReaderTasks;

import org.apache.commons.lang3.StringUtils;

import app.tvs.activities.MainActivity;
import app.tvs.services.NotificationService;
import app.tvs.services.UpdateTVSeriesShortService;
import app.tvseries.R;

public class UpdateTVSeriesShortReader extends UpdateIMDBRatingHTMLReaderTask{

    public UpdateTVSeriesShortReader(MainActivity activity) {
        super(activity);
    }

    @Override
    protected String getToastMessage() {
        return activity.getString(R.string.updateTVSeriesShortError);
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            updated = UpdateTVSeriesShortService.update(activity);
            NotificationService.addNotification(setFinalMessage(), activity, activity.getString(R.string.updateTVSeriesShortChannel), activity.getString(R.string.updateTVSeriesShortName), activity.getString(R.string.updateTVSeriesShortDescription));
            return StringUtils.EMPTY;
        } catch (Exception e) {
            NotificationService.addNotification(getToastMessage(), activity, activity.getString(R.string.updateTVSeriesShortChannel), activity.getString(R.string.updateTVSeriesShortName), activity.getString(R.string.updateTVSeriesShortDescription));
            return getToastMessage();
        }
    }

    @Override
    protected String setStartMessage() {
        return activity.getString(R.string.adding);
    }

    @Override
    protected String setFinalMessage() {
        return activity.getString(R.string.added) + " " + updated + " " + activity.getString(R.string.tvSeries);
    }

}
