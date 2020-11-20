package app.tvs.htmlReaderTasks;

import org.apache.commons.lang3.StringUtils;

import app.tvs.activities.MainActivity;
import app.tvs.entities.TVSeries;
import app.tvs.services.UpdateTVSeriesService;
import app.tvseries.R;

public class UpdateTVSeriesHTMLReaderTask extends HTMLReaderTask {

    private TVSeries tvSeries;
    private boolean result;

    public UpdateTVSeriesHTMLReaderTask(MainActivity activity, TVSeries tvSeries) {
        super(activity);
        this.tvSeries = tvSeries;
    }

    @Override
    protected String getToastMessage() {
        return activity.getString(R.string.updateTVSeriesError);
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            result = UpdateTVSeriesService.updateTVSeries(activity, tvSeries);
            return StringUtils.EMPTY;
        } catch (Exception e) {
            return getToastMessage();
        }
    }

    @Override
    protected String setStartMessage() {
        return activity.getString(R.string.updating);
    }

    @Override
    protected String setFinalMessage() {
        return result ? activity.getString(R.string.updated) : activity.getString(R.string.nothingToUpdate) ;
    }

}
