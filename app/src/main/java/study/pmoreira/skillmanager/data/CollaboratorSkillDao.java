package study.pmoreira.skillmanager.data;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.CollaboratorSkill;

//TODO: make it static ?
public class CollaboratorSkillDao extends BaseDao {

    static final String COLLABORATOR_SKILLS_PATH = "collaboratorSkills";

    public void findAll(OperationListener<List<CollaboratorSkill>> listener) {
        FirebaseDao.findAll(CollaboratorSkill.class, COLLABORATOR_SKILLS_PATH, listener);
    }

    public void saveOrUpdate(CollaboratorSkill collabSkill, OperationListener<CollaboratorSkill> listener) {
        FirebaseDao.saveOrUpdate(collabSkill, COLLABORATOR_SKILLS_PATH, listener);
    }

    public void delete(String id, OperationListener<String> listener) {
        FirebaseDao.delete(id, COLLABORATOR_SKILLS_PATH, listener);
    }

    public void findCollaboratorSkills(String collabId, final OperationListener<List<CollaboratorSkill>> listener) {
        FirebaseDao.getDatabase().getReference(COLLABORATOR_SKILLS_PATH)
                .orderByChild(CollaboratorSkill.JSON_COLLABORATOR_ID)
                .equalTo(collabId)
                .addListenerForSingleValueEvent(new OnDataChange() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<CollaboratorSkill> results = new ArrayList<CollaboratorSkill>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            results.add(snapshot.getValue(CollaboratorSkill.class));
                        }
                        listener.onSuccess(results);
                    }
                });
    }

    public void deleteCollaboratorSkills(String collaboratorId) {
        findCollaboratorSkills(collaboratorId, new OperationListener<List<CollaboratorSkill>>() {
            @Override
            public void onSuccess(List<CollaboratorSkill> collabSkills) {
                for (CollaboratorSkill collabSkill : collabSkills) {
                    delete(collabSkill.getId(), new OperationListener<String>());
                }
            }
        });
    }
}
