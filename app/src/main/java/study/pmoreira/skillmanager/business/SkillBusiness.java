package study.pmoreira.skillmanager.business;

import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.data.SkillDao;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.infrastructure.exception.ValidateException;
import study.pmoreira.skillmanager.model.Skill;

public class SkillBusiness {

    public static final int INVALID_SKILL_NAME = 1;
    public static final int INVALID_SKILL_DESCRIPTION = 2;
    public static final int INVALID_SKILL_LEARN_MORE_URL = 3;
    public static final int INVALID_SKILL_PICTURE_URL = 4;

    private SkillDao mSkillDao = new SkillDao();

    public void findAll(OperationListener<List<Skill>> listener) {
        mSkillDao.findAll(listener);
    }

    public void save(Skill skill, OperationListener<Skill> listener) {
        if (isValid(skill, listener)) {
            mSkillDao.save(skill, listener);
        }
    }

    public void delete(Skill skill) {
        mSkillDao.deleteImage(skill.getPictureUrl());
//        mSkillDao.delete();
    }

    public void uploadImage(Uri data, final OperationListener<String> listener) {
        mSkillDao.uploadImage(data, listener);
    }

    public void deleteImage(String url) {
        mSkillDao.deleteImage(url);
    }

    private boolean isValid(Skill skill, OperationListener<Skill> listener) {
        boolean isValid = true;

        if (TextUtils.isEmpty(skill.getName())) {
            listener.onValidationError(new ValidateException(R.string.name_cannot_be_empty, INVALID_SKILL_NAME));
            isValid = false;
        }
        if (TextUtils.isEmpty(skill.getDescription())) {
            listener.onValidationError(new ValidateException(R.string.description_cannot_be_empty, INVALID_SKILL_DESCRIPTION));
            isValid = false;
        }
        if (TextUtils.isEmpty(skill.getLearnMoreUrl())) {
            listener.onValidationError(new ValidateException(R.string.learn_more_cannot_be_empty, INVALID_SKILL_LEARN_MORE_URL));
            isValid = false;
        }
        if (TextUtils.isEmpty(skill.getPictureUrl())) {
            listener.onValidationError(new ValidateException(R.string.picture_cannot_be_empty, INVALID_SKILL_PICTURE_URL));
            isValid = false;
        }

        return isValid;
    }
}
