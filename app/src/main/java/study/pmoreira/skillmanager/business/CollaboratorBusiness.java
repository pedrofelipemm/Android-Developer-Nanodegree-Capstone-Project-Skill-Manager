package study.pmoreira.skillmanager.business;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

import study.pmoreira.skillmanager.data.CollaboratorDao;
import study.pmoreira.skillmanager.data.CollaboratorSkillDao;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.infrastructure.exception.BusinessException;
import study.pmoreira.skillmanager.infrastructure.exception.ValidateException;
import study.pmoreira.skillmanager.model.Collaborator;
import study.pmoreira.skillmanager.model.CollaboratorSkill;
import study.pmoreira.skillmanager.model.Skill;

public class CollaboratorBusiness extends BaseBusiness {

    public static final int INVALID_COLLABORATOR_NAME = 1;
    public static final int INVALID_COLLABORATOR_BIRTHDATE = 2;
    public static final int INVALID_COLLABORATOR_ROLE = 3;
    public static final int INVALID_COLLABORATOR_EMAIL = 4;
    public static final int INVALID_COLLABORATOR_PHONE = 5;
    public static final int INVALID_COLLABORATOR_PICTURE = 6;

    private CollaboratorBusiness() {}
    
    public static void findAll(OperationListener<List<Collaborator>> listener) {
        CollaboratorDao.findAll(listener);
    }

    public static void findAllSingleEvent(OperationListener<List<Collaborator>> listener) {
        CollaboratorDao.findAllSingleEvent(listener);
    }

    public static void saveOrUpdate(final Collaborator collaborator, final OperationListener<Collaborator> listener) {
        if (isValid(collaborator, listener)) {
            CollaboratorDao.saveOrUpdate(collaborator, new OnSaveOrUpdate(collaborator, listener));
        }
    }

    public static void saveOrUpdate(final Collaborator collaborator, List<String> collabSkillNames,
                             final OperationListener<Collaborator> listener) {

        if (isValid(collaborator, listener)) {
            SkillBusiness.findSkillsByName(collabSkillNames, new OperationListener<List<Skill>>() {
                @Override
                public void onSuccess(final List<Skill> skills) {
                    CollaboratorSkillDao.deleteCollaboratorSkills(collaborator.getId(), new OperationListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            collaborator.setSkills(skills);
                            CollaboratorDao.saveOrUpdate(collaborator, new OnSaveOrUpdate(collaborator, listener));
                        }
                    });
                }
            });
        }
    }

    public static void delete(String id, OperationListener<String> listener) {
        CollaboratorDao.delete(id, listener);
        CollaboratorSkillDao.deleteCollaboratorSkills(id, new OperationListener<Void>());
    }

    public static String uploadImage(byte[] data, final OperationListener<String> listener) {
        return CollaboratorDao.uploadImage(data, listener);
    }

    public static void deleteImage(String url) {
        CollaboratorDao.deleteImage(url);
    }

    private static boolean isValid(Collaborator collaborator, OperationListener<Collaborator> listener) {
        boolean isValid = true;

        if (StringUtils.isBlank(collaborator.getPhone())) {
            listener.onValidationError(new ValidateException(INVALID_COLLABORATOR_PHONE));
            isValid = false;
        }
        if (StringUtils.isBlank(collaborator.getEmail())) {
            listener.onValidationError(new ValidateException(INVALID_COLLABORATOR_EMAIL));
            isValid = false;
        }
        if (StringUtils.isBlank(collaborator.getRole())) {
            listener.onValidationError(new ValidateException(INVALID_COLLABORATOR_ROLE));
            isValid = false;
        }
        if (collaborator.getBirthdate() == null) {
            listener.onValidationError(new ValidateException(INVALID_COLLABORATOR_BIRTHDATE));
            isValid = false;
        }
        if (StringUtils.isBlank(collaborator.getName())) {
            listener.onValidationError(new ValidateException(INVALID_COLLABORATOR_NAME));
            isValid = false;
        }
        if (StringUtils.isBlank(collaborator.getPictureUrl())) {
            listener.onValidationError(new ValidateException(INVALID_COLLABORATOR_PICTURE));
            isValid = false;
        }

        return isValid;
    }

    private static class OnSaveOrUpdate extends OperationListener<Collaborator> {

        private final Collaborator mCollaborator;
        private final OperationListener<Collaborator> mListener;

        OnSaveOrUpdate(Collaborator collaborator, OperationListener<Collaborator> listener) {
            mCollaborator = collaborator;
            mListener = listener;
        }

        @Override
        public void onSuccess(final Collaborator collab) {
            final List<Skill> skills = mCollaborator.getSkills();
            if (skills.isEmpty()) {
                mListener.onSuccess(collab);
                return;
            }

            final MutableInt savedSkills = new MutableInt();

            for (Skill skill : skills) {
                CollaboratorSkill collabSkill = new CollaboratorSkill(collab.getId(), skill.getId(), skill.getName());
                CollaboratorSkillDao.saveOrUpdate(collabSkill, new OperationListener<CollaboratorSkill>() {
                    @Override
                    public void onSuccess(CollaboratorSkill result) {
                        savedSkills.increment();
                        if (savedSkills.getValue() == skills.size()) {
                            collab.setSkills(skills);
                            mListener.onSuccess(collab);
                        }
                    }
                });
            }
        }

        @Override
        public void onError(BusinessException e) {
            mListener.onError(e);
        }
    }
}
