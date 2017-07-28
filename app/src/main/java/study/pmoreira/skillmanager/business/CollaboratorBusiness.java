package study.pmoreira.skillmanager.business;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import study.pmoreira.skillmanager.data.CollaboratorDao;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.infrastructure.exception.ValidateException;
import study.pmoreira.skillmanager.model.Collaborator;

//TODO: make it static ?
public class CollaboratorBusiness {

    //TODO
    public static final int INVALID_COLLABORATOR_NAME = 1;

    private CollaboratorDao mCollaboratorDao = new CollaboratorDao();

    public void findAll(OperationListener<List<Collaborator>> listener) {
        mCollaboratorDao.findAll(listener);
    }

    public void save(Collaborator collaborator, OperationListener<Collaborator> listener) {
        if (isValid(collaborator, listener)) {
            mCollaboratorDao.save(collaborator, listener);
        }
    }

    public void delete(String id, OperationListener<String> listener) {
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
}
