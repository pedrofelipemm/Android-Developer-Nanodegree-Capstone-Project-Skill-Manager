package study.pmoreira.skillmanager.ui.main.collaborator;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import study.pmoreira.skillmanager.model.Collaborator;
import study.pmoreira.skillmanager.ui.AdapterFilter;

class CollaboratorAdapterFilter extends AdapterFilter<Collaborator> {

    CollaboratorAdapterFilter(List<Collaborator> collabs, OnFilter<Collaborator> onFilter) {
        super(collabs, onFilter);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (TextUtils.isEmpty(constraint)) {
            results.values = getItems();
            results.count = getItems().size();
        } else {
            List<Collaborator> collabs = new ArrayList<>();
            //TODO: filter by skill
            for (Collaborator collab : getItems()) {
                if (collab.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    collabs.add(collab);
                }

                results.values = collabs;
                results.count = collabs.size();
            }
        }

        return results;
    }
}
