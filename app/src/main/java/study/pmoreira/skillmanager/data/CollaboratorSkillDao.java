package study.pmoreira.skillmanager.data;

import java.util.List;

import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.CollaboratorSkill;

//TODO: make it static ?
public class CollaboratorSkillDao extends BaseDao {

    static final String COLLABORATOR_SKILLS_PATH = "collaboratorSkills";

    public void findAll(final OperationListener<List<CollaboratorSkill>> listener) {
        FirebaseDao.findAll(CollaboratorSkill.class, COLLABORATOR_SKILLS_PATH, listener);
    }

    public void saveOrUpdate(CollaboratorSkill collabSkill, OperationListener<CollaboratorSkill> listener) {
        FirebaseDao.saveOrUpdate(collabSkill, COLLABORATOR_SKILLS_PATH, listener);
    }

    public void delete(String id, OperationListener<String> listener) {
        FirebaseDao.delete(id, COLLABORATOR_SKILLS_PATH, listener);
    }
}
