package study.pmoreira.skillmanager.widget.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import study.pmoreira.skillmanager.widget.WidgetScheduler;

public class WidgetIntentService extends IntentService {

    private static final String TAG = WidgetIntentService.class.getName();

    public WidgetIntentService() {
        super(WidgetIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        WidgetScheduler.updateData(getApplicationContext());
    }
}
