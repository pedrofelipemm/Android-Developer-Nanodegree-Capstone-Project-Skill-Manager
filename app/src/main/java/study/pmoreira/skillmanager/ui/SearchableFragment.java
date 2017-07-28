package study.pmoreira.skillmanager.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class SearchableFragment extends Fragment implements SearchFilter {

    protected static final String STATE_QUERY = "STATE_QUERY";

    private CharSequence mQuery = "";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(STATE_QUERY, mQuery);
    }

    protected CharSequence getQuery() {
        return mQuery;
    }

    protected void setQuery(CharSequence query) {
        mQuery = query;
    }
}
