package study.pmoreira.skillmanager.business;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
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

    public void findAllSingleEvent(OperationListener<List<Skill>> listener) {
        mSkillDao.findAllSingleEvent(listener);
    }

    public void findSkillsByName(final List<String> names, final OperationListener<List<Skill>> listener) {
        final MutableInt namesCount = new MutableInt();
        final List<Skill> skills = new ArrayList<>();

        if (names.isEmpty()) {
            listener.onSuccess(skills);
        }

        for (final String name : names) {
            mSkillDao.findSkillsByName(name, new OperationListener<List<Skill>>() {
                @Override
                public void onSuccess(List<Skill> result) {
                    skills.addAll(result);
                    namesCount.increment();
                    if (namesCount.getValue().equals(names.size())) {
                        listener.onSuccess(skills);
                    }
                }
            });
        }
    }

    public void saveOrUpdate(Skill skill, OperationListener<Skill> listener) {
        if (isValid(skill, listener)) {
            mSkillDao.saveOrUpdate(skill, listener);
        }
    }

    public void delete(String id, OperationListener<String> listener) {
        //TODO: check if is there any collab using it
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

        if (StringUtils.isBlank(skill.getLearnMoreUrl())) {
            listener.onValidationError(new ValidateException(INVALID_SKILL_LEARN_MORE_URL));
            isValid = false;
        }
        if (StringUtils.isBlank(skill.getDescription())) {
            listener.onValidationError(new ValidateException(INVALID_SKILL_DESCRIPTION));
            isValid = false;
        }
        if (StringUtils.isBlank(skill.getName())) {
            listener.onValidationError(new ValidateException(INVALID_SKILL_NAME));
            isValid = false;
        }
        if (StringUtils.isBlank(skill.getPictureUrl())) {
            listener.onValidationError(new ValidateException(INVALID_SKILL_PICTURE_URL));
            isValid = false;
        }

        return isValid;
    }
}
