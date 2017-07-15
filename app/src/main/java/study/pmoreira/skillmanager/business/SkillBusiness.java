package study.pmoreira.skillmanager.business;

import android.net.Uri;
import android.text.TextUtils;

import study.pmoreira.skillmanager.data.SkillDao;
import study.pmoreira.skillmanager.infrastructure.BusinessException;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Skill;

public class SkillBusiness {

    public static final int INVALID_SKILL_NAME = 1;
    public static final int INVALID_SKILL_DESCRIPTION = 2;
    public static final int INVALID_SKILL_LEARN_MORE_URL = 3;
    public static final int INVALID_SKILL_PICTURE_URL = 4;

    private SkillDao mSkillDao = new SkillDao();

    public void save(Skill skill, OperationListener<Skill> listener) {
        if (isValid(skill, listener)) {
            mSkillDao.save(skill, listener);
        }
    }

    public void uploadImage(Uri data, final OperationListener<String> listener) {
        mSkillDao.uploadImage(data, listener);
    }

    private boolean isValid(Skill skill, OperationListener<Skill> listener) {
        boolean isValid = true;

        if (TextUtils.isEmpty(skill.getName())) {
            listener.onError(new BusinessException("Name cannot be empty.", INVALID_SKILL_NAME));
            isValid = false;
        }
//        if (TextUtils.isEmpty()) {
//
//        }
//        if (TextUtils.isEmpty()) {
//
//        }
//        if (TextUtils.isEmpty()) {
//
//        }

        return isValid;
    }
}
