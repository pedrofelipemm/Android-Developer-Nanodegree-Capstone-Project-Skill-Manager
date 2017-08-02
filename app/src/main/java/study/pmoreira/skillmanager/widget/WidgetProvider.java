package study.pmoreira.skillmanager.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.ui.collaborator.CollaboratorActivity;
import study.pmoreira.skillmanager.ui.main.MainActivity;
import study.pmoreira.skillmanager.widget.service.WidgetRemoteViewsService;

public class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = WidgetProvider.class.getName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: ");
        for (int appWidgetId : appWidgetIds) {
            PendingIntent pendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .addNextIntent(new Intent(context, CollaboratorActivity.class))
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setRemoteAdapter(R.id.listview, new Intent(context, WidgetRemoteViewsService.class));
            views.setPendingIntentTemplate(R.id.listview, pendingIntent);
            views.setEmptyView(R.id.listview, R.id.emptyview);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (WidgetScheduler.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview);
        }
    }

}
