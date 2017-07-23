package study.pmoreira.skillmanager.ui;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public abstract class AdapterFilter<T> extends Filter {

    private List<T> mItems = new ArrayList<>();

    private OnFilter<T> mOnFilter;

    protected AdapterFilter(List<T> items, OnFilter<T> onFilter) {
        mItems = items;
        mOnFilter = onFilter;
    }

    @Override
    protected abstract FilterResults performFiltering(CharSequence constraint);

    @Override
    @SuppressWarnings("unchecked")
    protected final void publishResults(CharSequence constraint, FilterResults results) {
        if (results.values instanceof List) {
            mOnFilter.publishResults(new ArrayList<>((List<T>) results.values));
        } else {
            mOnFilter.publishResults(mItems);
        }
    }

    public interface OnFilter<T> {
        void publishResults(List<T> results);
    }

    protected List<T> getItems() {
        return mItems;
    }
}
