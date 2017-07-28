package study.pmoreira.skillmanager.ui.main.skill;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import study.pmoreira.skillmanager.model.Skill;
import study.pmoreira.skillmanager.ui.AdapterFilter;

class SkillAdapterFilter extends AdapterFilter<Skill> {

    SkillAdapterFilter(List<Skill> skills, OnFilter<Skill> onFilter) {
        super(skills, onFilter);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (TextUtils.isEmpty(constraint)) {
            results.values = getItems();
            results.count = getItems().size();
        } else {
            List<Skill> skills = new ArrayList<>();
            for (Skill skill : getItems()) {
                if (skill.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    skills.add(skill);
                }

                results.values = skills;
                results.count = skills.size();
            }

        }

        return results;
    }
}
