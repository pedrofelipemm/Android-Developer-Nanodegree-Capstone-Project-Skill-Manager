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

//TODO: make it static ?
public class CollaboratorBusiness {

    //TODO
    public static final int INVALID_COLLABORATOR_NAME = 1;

    private CollaboratorSkillBusiness mCollaboratorSkillBusiness = new CollaboratorSkillBusiness();

    private CollaboratorDao mCollaboratorDao = new CollaboratorDao();
    private CollaboratorSkillDao mCollaboratorSkillDao = new CollaboratorSkillDao();

    public void findAll(OperationListener<List<Collaborator>> listener) {
        mCollaboratorDao.findAll(listener);
    }

    public void saveOrUpdate(final Collaborator collaborator, final OperationListener<Collaborator> listener) {
        if (isValid(collaborator, listener)) {
            mCollaboratorDao.saveOrUpdate(collaborator, new OnSaveOrUpdate(collaborator, listener));
        }
    }

    public void delete(String id, OperationListener<String> listener) {
        //TODO: delete CollaborratorSkill
        mCollaboratorDao.delete(id, listener);
    }

    public String uploadImage(byte[] data, final OperationListener<String> listener) {
        return mCollaboratorDao.uploadImage(data, listener);
    }

    public void deleteImage(String url) {
        mCollaboratorDao.deleteImage(url);
    }

    private boolean isValid(Collaborator collaborator, OperationListener<Collaborator> listener) {
        boolean isValid = true;

        //TODO
        if (StringUtils.isBlank(collaborator.getName())) {
            listener.onValidationError(new ValidateException(INVALID_COLLABORATOR_NAME));
            isValid = false;
        }

        return isValid;
    }

    private class OnSaveOrUpdate extends OperationListener<Collaborator> {

        private final Collaborator mCollaborator;
        private final OperationListener<Collaborator> mListener;

        OnSaveOrUpdate(Collaborator collaborator, OperationListener<Collaborator> listener) {
            mCollaborator = collaborator;
            mListener = listener;
        }

        @Override
        public void onSuccess(final Collaborator collab) {
            final List<Skill> skills = mCollaborator.getSkills();
            if (skills.isEmpty()) return;

            final MutableInt savedSkills = new MutableInt();

            for (Skill skill : skills) {
                CollaboratorSkill collabSkill = new CollaboratorSkill(collab.getId(), skill.getId(), skill.getName());
                mCollaboratorSkillDao.saveOrUpdate(collabSkill, new OperationListener<CollaboratorSkill>() {
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
