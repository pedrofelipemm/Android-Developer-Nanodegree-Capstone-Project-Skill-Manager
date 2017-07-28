package study.pmoreira.skillmanager.data;

import java.util.List;

import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Collaborator;

//TODO: make it static ?
public class CollaboratorDao extends BaseDao {

    static final String COLLABORATORS_PATH = "collaborators";
    static final String COLLABORATORS_IMAGES_PATH = "collaboratorImages";

    public void findAll(final OperationListener<List<Collaborator>> listener) {
        FirebaseDao.findAllListener(Collaborator.class, COLLABORATORS_PATH, listener);
    }

    public void save(Collaborator collab, OperationListener<Collaborator> listener) {
        FirebaseDao.save(collab, COLLABORATORS_PATH, listener);
    }

    public void delete(String id, OperationListener<String> listener) {
        FirebaseDao.delete(id, COLLABORATORS_PATH, listener);
    }

    public String uploadImage(byte[] data, final OperationListener<String> listener) {
        return FirebaseDao.uploadImage(data, COLLABORATORS_IMAGES_PATH, listener);
    }
}