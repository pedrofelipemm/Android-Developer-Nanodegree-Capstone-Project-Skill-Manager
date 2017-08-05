package study.pmoreira.skillmanager.widget.service;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.data.SkillManagerContract.CollaboratorsEntry;
import study.pmoreira.skillmanager.model.Collaborator;
import study.pmoreira.skillmanager.ui.collaborator.CollaboratorActivity;

public class WidgetRemoteViewsService extends RemoteViewsService {

    private static final String TAG = WidgetRemoteViewsService.class.getName();

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {}

            @Override
            public void onDataSetChanged() {
                Log.d(TAG, "onDataSetChanged: ");
                if (data != null) {
                    data.close();
                }

                long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(
                        CollaboratorsEntry.CONTENT_URI,
                        CollaboratorsEntry.ALL_COLUMNS.toArray(new String[CollaboratorsEntry.ALL_COLUMNS_SIZE]),
                        null, null,
                        CollaboratorsEntry.ORDER_BY_NAME);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                Log.d(TAG, "getViewAt: ");
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }

                Collaborator collaborator = new Collaborator(data);

                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(CollaboratorActivity.EXTRA_COLLABORATOR, collaborator);

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_item);
                views.setTextViewText(R.id.name_textview, collaborator.getName());
                views.setTextViewText(R.id.role_textview, collaborator.getRole());
                views.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                Log.d(TAG, "getLoadingView: ");
                return new RemoteViews(getPackageName(), R.layout.widget_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                Log.d(TAG, "getItemId: ");
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
