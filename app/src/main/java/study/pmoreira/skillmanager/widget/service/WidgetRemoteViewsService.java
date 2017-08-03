package study.pmoreira.skillmanager.widget.service;

import android.content.Intent;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.business.CollaboratorBusiness;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Collaborator;
import study.pmoreira.skillmanager.ui.collaborator.CollaboratorActivity;
import study.pmoreira.skillmanager.widget.WidgetScheduler;

    public class WidgetRemoteViewsService extends RemoteViewsService {

    private static final String TAG = WidgetRemoteViewsService.class.getName();

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {

            private List<Collaborator> mCollabs = new ArrayList<>();

            @Override
            public void onCreate() {
                WidgetScheduler.initialize(getApplicationContext());
            }

            @Override
            public void onDataSetChanged() {
                Log.d(TAG, "onDataSetChanged: ");
                final long identityToken = Binder.clearCallingIdentity();

                new CollaboratorBusiness().findAllNoListener(new OperationListener<List<Collaborator>>() {
                    @Override
                    public void onSuccess(List<Collaborator> collabs) {
                        mCollabs = collabs;
                        Log.d(TAG, "Binder.restoreCallingIdentity ");
                        Binder.restoreCallingIdentity(identityToken);
                    }
                });
            }

            @Override
            public void onDestroy() {
                mCollabs = new ArrayList<>();
            }

            @Override
            public int getCount() {
                return mCollabs.size();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                Log.d(TAG, "getViewAt: ");
                if (position == AdapterView.INVALID_POSITION || mCollabs.size() < 1) {
                    return null;
                }

                Collaborator collaborator = mCollabs.get(position);

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
