package study.pmoreira.skillmanager.business;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;

import study.pmoreira.skillmanager.data.CollaboratorSkillDao;
import study.pmoreira.skillmanager.data.SkillDao;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.infrastructure.exception.ValidateException;
import study.pmoreira.skillmanager.model.CollaboratorSkill;
import study.pmoreira.skillmanager.model.Skill;

public class SkillBusiness extends BaseBusiness {

    public static final int INVALID_SKILL_NAME = 1;
    public static final int INVALID_SKILL_DESCRIPTION = 2;
    public static final int INVALID_SKILL_LEARN_MORE_URL = 3;
    public static final int INVALID_SKILL_PICTURE_URL = 4;

    public static final int SKILL_BEING_USED = 5;

    private SkillBusiness() {}

    public static void findAll(OperationListener<List<Skill>> listener) {
        SkillDao.findAll(listener);
    }

    public static void findAllSingleEvent(OperationListener<List<Skill>> listener) {
        SkillDao.findAllSingleEvent(listener);
    }

    public static void findSkillsByName(final List<String> names, final OperationListener<List<Skill>> listener) {
        final MutableInt namesCount = new MutableInt();
        final List<Skill> skills = new ArrayList<>();

        if (names.isEmpty()) {
            listener.onSuccess(skills);
        }

        for (final String name : names) {
            SkillDao.findSkillsByName(name, new OperationListener<List<Skill>>() {
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

    public static void saveOrUpdate(Skill skill, OperationListener<Skill> listener) {
        if (isValid(skill, listener)) {
            SkillDao.saveOrUpdate(skill, listener);
        }
    }

    public static void delete(final String id, final OperationListener<String> listener) {
        CollaboratorSkillDao.findCollaboratorSkillsBySkill(id, new OperationListener<List<CollaboratorSkill>>() {
            @Override
            public void onSuccess(List<CollaboratorSkill> result) {
                if (result.isEmpty()) {
                    SkillDao.delete(id, listener);
                } else {
                    listener.onError(new ValidateException(SKILL_BEING_USED));
                }
            }
        });
    }

    public static String uploadImage(byte[] data, final OperationListener<String> listener) {
        return SkillDao.uploadImage(data, listener);
    }

    public static void deleteImage(String url) {
        SkillDao.deleteImage(url);
    }

    public static boolean isValid(Skill skill, OperationListener<Skill> listener) {
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
