package study.pmoreira.skillmanager.business;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import study.pmoreira.skillmanager.data.SkillDao;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.infrastructure.exception.ValidateException;
import study.pmoreira.skillmanager.model.Skill;

//TODO: make it static ?
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

    public void delete(String id, OperationListener<String> listener) {
        mSkillDao.delete(id, listener);
    }

    public String uploadImage(byte[] data, final OperationListener<String> listener) {
        return mSkillDao.uploadImage(data, listener);
    }

    public void deleteImage(String url) {
        mSkillDao.deleteImage(url);
    }

    private boolean isValid(Skill skill, OperationListener<Skill> listener) {
        boolean isValid = true;

        if (StringUtils.isBlank(skill.getName())) {
            listener.onValidationError(new ValidateException(INVALID_SKILL_NAME));
            isValid = false;
        }
        if (StringUtils.isBlank(skill.getDescription())) {
            listener.onValidationError(new ValidateException(INVALID_SKILL_DESCRIPTION));
            isValid = false;
        }
        if (StringUtils.isBlank(skill.getLearnMoreUrl())) {
            listener.onValidationError(new ValidateException(INVALID_SKILL_LEARN_MORE_URL));
            isValid = false;
        }
        if (StringUtils.isBlank(skill.getPictureUrl())) {
            listener.onValidationError(new ValidateException(INVALID_SKILL_PICTURE_URL));
            isValid = false;
        }

        return isValid;
    }
}
