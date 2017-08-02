package study.pmoreira.skillmanager.widget.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

public class WidgetService extends JobService {

    private static final String TAG = WidgetService.class.getName();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob: ");
        getApplicationContext().startService(new Intent(getApplicationContext(), WidgetIntentService.class));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
