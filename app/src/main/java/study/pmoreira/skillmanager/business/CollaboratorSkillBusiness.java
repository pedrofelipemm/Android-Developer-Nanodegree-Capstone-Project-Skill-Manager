package study.pmoreira.skillmanager.business;

import java.util.ArrayList;
import java.util.List;

import study.pmoreira.skillmanager.data.CollaboratorSkillDao;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.CollaboratorSkill;

public class CollaboratorSkillBusiness {

    private CollaboratorSkillDao mCollaboratorSkillDao = new CollaboratorSkillDao();

    public void findAll(OperationListener<List<CollaboratorSkill>> listener) {
        mCollaboratorSkillDao.findAll(listener);
    }

    public void findCollaboratorSkillsName(String collaboratorId, final OperationListener<List<String>> listener) {
        mCollaboratorSkillDao.findCollaboratorSkillsByCollaborator(collaboratorId, new OperationListener<List<CollaboratorSkill>>() {
            @Override
            public void onSuccess(List<CollaboratorSkill> collabSkills) {
                List<String> results = new ArrayList<>();
                for (CollaboratorSkill collabSkill : collabSkills) {
                    results.add(collabSkill.getSkillName());
                }
                listener.onSuccess(results);
            }
        });
    }
}
