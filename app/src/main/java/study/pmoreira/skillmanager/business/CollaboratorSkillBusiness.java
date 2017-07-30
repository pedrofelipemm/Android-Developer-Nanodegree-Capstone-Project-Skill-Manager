package study.pmoreira.skillmanager.business;

import java.util.ArrayList;
import java.util.List;

import study.pmoreira.skillmanager.data.CollaboratorSkillDao;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.CollaboratorSkill;

public class CollaboratorSkillBusiness {

    private CollaboratorSkillDao mCollaboratorSkillDao = new CollaboratorSkillDao();

    public void findCollaboratorSkillsName(String collaboratorId, final OperationListener<List<String>> listener) {
        mCollaboratorSkillDao.findCollaboratorSkills(collaboratorId, new OperationListener<List<CollaboratorSkill>>() {
            @Override
            public void onSuccess(List<CollaboratorSkill> collabSkills) {
                List<String> results = new ArrayList<String>();
                for (CollaboratorSkill collabSkill : collabSkills) {
                    results.add(collabSkill.getSkillName());
                }
                listener.onSuccess(results);
            }
        });
    }
}
