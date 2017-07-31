package study.pmoreira.skillmanager.ui.main.collaborator;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import study.pmoreira.skillmanager.model.Collaborator;
import study.pmoreira.skillmanager.model.CollaboratorSkill;
import study.pmoreira.skillmanager.ui.AdapterFilter;

class CollaboratorAdapterFilter extends AdapterFilter<Collaborator> {

    private List<CollaboratorSkill> mCollaboratorSkills = new ArrayList<>();

    CollaboratorAdapterFilter(List<Collaborator> collabs, List<CollaboratorSkill> collaboratorSkills,
                              OnFilter<Collaborator> onFilter) {
        super(collabs, onFilter);
        mCollaboratorSkills = collaboratorSkills;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        Set<Collaborator> collabs = new HashSet<>();

        if (TextUtils.isEmpty(constraint)) {
            results.values = getItems();
            results.count = getItems().size();
        } else {
            for (CollaboratorSkill collabSkill : mCollaboratorSkills) {
                for (Collaborator collab : getItems()) {
                    if (collab.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        collabs.add(collab);
                        break;
                    }
                }
                if (collabSkill.getSkillName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    for (Collaborator collab : getItems()) {
                        if (collabSkill.getCollaboratorId().equals(collab.getId())) {
                            collabs.add(collab);
                            break;
                        }
                    }
                }
            }
            results.values = new ArrayList<>(collabs);
            results.count = collabs.size();
        }

        return results;
    }
}
