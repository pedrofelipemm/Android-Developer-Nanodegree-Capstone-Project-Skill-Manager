package study.pmoreira.skillmanager.data;

import android.net.Uri;

import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Skill;

public class SkillDao {

    private static final String SKILLS_PATH = "skills";
    private static final String SKILL_IMAGES_PATH = "skillImages";

    public void save(Skill skill, OperationListener<Skill> listener) {
        FirebaseDao.save(skill, SKILLS_PATH, listener);
    }

    public void uploadImage(Uri data, final OperationListener<String> listener) {
        FirebaseDao.uploadImage(data, SKILL_IMAGES_PATH, listener);
    }

    public void deleteImage(String url) {
        FirebaseDao.deleteImage(url);
    }

}
