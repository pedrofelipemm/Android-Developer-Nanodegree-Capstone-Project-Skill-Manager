package study.pmoreira.skillmanager.data;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Skill;

public class SkillDao {

    static final String SKILLS_PATH = "skills";
    static final String SKILL_IMAGES_PATH = "skillImages";

    public void findAll(final OperationListener<List<Skill>> listener) {
        FirebaseDao.findAllListener(Skill.class, SKILLS_PATH, listener);
    }

    public void save(Skill skill, OperationListener<Skill> listener) {
        FirebaseDao.save(skill, SKILLS_PATH, listener);
    }

    public String uploadImage(Uri data, final OperationListener<String> listener) {
        return FirebaseDao.uploadImage(data, SKILL_IMAGES_PATH, listener);
    }

    public void deleteImage(String url) {
        FirebaseDao.deleteImage(url);
    }
}
