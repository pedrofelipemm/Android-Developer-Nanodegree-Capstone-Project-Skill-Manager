package study.pmoreira.skillmanager.widget;

import android.app.job.JobInfo;
import android.app.job.JobInfo.Builder;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import study.pmoreira.skillmanager.business.CollaboratorBusiness;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Collaborator;
import study.pmoreira.skillmanager.utils.NetworkUtils;
import study.pmoreira.skillmanager.widget.service.WidgetIntentService;
import study.pmoreira.skillmanager.widget.service.WidgetService;

public final class WidgetScheduler {

    private static final String TAG = WidgetScheduler.class.getName();

    private static final int ONE_OFF_ID = 2;
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;

    static final String ACTION_DATA_UPDATED = "study.pmoreira.skillmanager.ACTION_DATA_UPDATED";

    private WidgetScheduler() {}

    public static List<Collaborator> sCollabs = new ArrayList<>();

    public static void updateData(final Context context) {
        //TODO:
        new CollaboratorBusiness().findAllNoListener(new OperationListener<List<Collaborator>>() {
            @Override
            public void onSuccess(List<Collaborator> collabs) {
                sCollabs = collabs;
                context.sendBroadcast(new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName()));
            }
        });
    }

    public static synchronized void initialize(Context context) {
        schedulePeriodic(context);
        syncImmediately(context);
    }

    private static void schedulePeriodic(Context context) {
        Log.d(TAG, "initializing widget scheduler.. ");

        ((JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE)).schedule(
                new Builder(PERIODIC_ID, new ComponentName(context, WidgetService.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPeriodic(PERIOD)
                        .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL)
                        .build()
        );
    }


    public static synchronized void syncImmediately(Context context) {

        if (NetworkUtils.isConnected(context)) {
            context.startService(new Intent(context, WidgetIntentService.class));
        } else {
            ((JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE)).schedule(
                    new Builder(ONE_OFF_ID, new ComponentName(context, WidgetService.class))
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL)
                            .build()
            );
        }
    }
}
