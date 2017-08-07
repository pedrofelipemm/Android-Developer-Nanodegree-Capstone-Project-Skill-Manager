package study.pmoreira.skillmanager.data;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Skill;

public class SkillDao extends BaseDao {

    static final String SKILLS_PATH = "skills";
    static final String SKILL_IMAGES_PATH = "skillImages";

    private SkillDao() {}

    public static void findAll(final OperationListener<List<Skill>> listener) {
        FirebaseDao.findAll(Skill.class, SKILLS_PATH, listener);
    }

    public static void findAllSingleEvent(final OperationListener<List<Skill>> listener) {
        FirebaseDao.findAllSingleEvent(Skill.class, SKILLS_PATH, listener);
    }

    public static void findSkillsByName(String name, final OperationListener<List<Skill>> listener) {
        FirebaseDao.getDatabase().getReference(SKILLS_PATH)
                .orderByChild(Skill.JSON_NAME)
                .equalTo(name)
                .addListenerForSingleValueEvent(new OnDataChange() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Skill> results = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            results.add(snapshot.getValue(Skill.class));
                        }
                        listener.onSuccess(results);
                    }
                });
    }

    public static void saveOrUpdate(Skill skill, OperationListener<Skill> listener) {
        FirebaseDao.saveOrUpdate(skill, SKILLS_PATH, listener);
    }

    public static void delete(String id, OperationListener<String> listener) {
        FirebaseDao.delete(id, SKILLS_PATH, listener);
    }

    public static String uploadImage(byte[] data, final OperationListener<String> listener) {
        return FirebaseDao.uploadImage(data, SKILL_IMAGES_PATH, listener);
    }
}
