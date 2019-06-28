package app.tvs.htmlReaderTasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import app.tvseries.R;
import app.tvs.activities.MainActivity;

public abstract class HTMLReaderTask extends AsyncTask<Void, Void, String> {

    @SuppressLint("StaticFieldLeak")
    protected MainActivity activity;

    HTMLReaderTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        activity.findViewById(R.id.addingLayout).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.addingProgressBar).setVisibility(View.VISIBLE);
        ((TextView) activity.findViewById(R.id.addingTextView)).setText(setStartMessage());
    }

    @Override
    protected void onPostExecute(String result) {
        activity.findViewById(R.id.addingProgressBar).setVisibility(View.INVISIBLE);
        if(result.equals("")) {
            activity.findViewById(R.id.addingImageView).setVisibility(View.VISIBLE);
            ((TextView) activity.findViewById(R.id.addingTextView)).setText(setFinalMessage());
            activity.adapter.notifyDataSetChanged();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    finishTask();
                }
            }, 2000);
        }
        else{
            finishTask();
            Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
        }
    }

    protected void finishTask() {
        activity.findViewById(R.id.addingImageView).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.addingLayout).setVisibility(View.INVISIBLE);
        activity.setButtonsClickable(true);
        activity.adapter.notifyDataSetChanged();
    }

    abstract protected String getToastMessage();

    private String setFinalMessage() {
        return activity.getString(R.string.added);
    }

    private String setStartMessage() {
        return activity.getString(R.string.adding);
    }

}
