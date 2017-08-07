package study.pmoreira.skillmanager.data;

import com.google.firebase.database.DataSnapshot;

import org.apache.commons.lang3.mutable.MutableInt;

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

    public void findCollaboratorSkillsByCollaborator(String collabId, final OperationListener<List<CollaboratorSkill>> listener) {
        FirebaseDao.getDatabase().getReference(COLLABORATOR_SKILLS_PATH)
                .orderByChild(CollaboratorSkill.JSON_COLLABORATOR_ID)
                .equalTo(collabId)
                .addListenerForSingleValueEvent(new OnDataChange() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<CollaboratorSkill> results = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            results.add(snapshot.getValue(CollaboratorSkill.class));
                        }
                        listener.onSuccess(results);
                    }
                });
    }

    public void findCollaboratorSkillsBySkill(String skillId, final OperationListener<List<CollaboratorSkill>> listener) {
        FirebaseDao.getDatabase().getReference(COLLABORATOR_SKILLS_PATH)
                .orderByChild(CollaboratorSkill.JSON_SKILL_ID)
                .equalTo(skillId)
                .addListenerForSingleValueEvent(new OnDataChange() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<CollaboratorSkill> results = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            results.add(snapshot.getValue(CollaboratorSkill.class));
                        }
                        listener.onSuccess(results);
                    }
                });
    }

    public void deleteCollaboratorSkills(String collaboratorId, final OperationListener<Void> listener) {
        findCollaboratorSkillsByCollaborator(collaboratorId, new OperationListener<List<CollaboratorSkill>>() {
            @Override
            public void onSuccess(final List<CollaboratorSkill> collabSkills) {
                final MutableInt skillsCount = new MutableInt();
                if (collabSkills.isEmpty()) {
                    listener.onSuccess(null);
                }

                for (final CollaboratorSkill collabSkill : collabSkills) {
                    delete(collabSkill.getId(), new OperationListener<String>() {
                        @Override
                        public void onSuccess(String result) {
                            skillsCount.increment();
                            if ( skillsCount.getValue().equals(collabSkills.size()) ) {
                                listener.onSuccess(null);
                            }
                        }
                    });
                }
            }
        });
    }
}
