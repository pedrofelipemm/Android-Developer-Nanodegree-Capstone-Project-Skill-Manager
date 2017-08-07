package study.pmoreira.skillmanager.business;

import java.util.ArrayList;
import java.util.List;

import study.pmoreira.skillmanager.data.CollaboratorSkillDao;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.CollaboratorSkill;

public class CollaboratorSkillBusiness extends BaseBusiness {

    private CollaboratorSkillBusiness() {}

    public static void findAll(OperationListener<List<CollaboratorSkill>> listener) {
        CollaboratorSkillDao.findAll(listener);
    }

    public static void findCollaboratorSkillsName(String collaboratorId, final OperationListener<List<String>> listener) {
        CollaboratorSkillDao.findCollaboratorSkillsByCollaborator(collaboratorId, new OperationListener<List<CollaboratorSkill>>() {
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
