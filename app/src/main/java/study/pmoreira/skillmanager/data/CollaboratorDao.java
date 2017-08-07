package study.pmoreira.skillmanager.data;

import java.util.List;

import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Collaborator;

public class CollaboratorDao extends BaseDao {

    static final String COLLABORATORS_PATH = "collaborators";
    static final String COLLABORATORS_IMAGES_PATH = "collaboratorImages";

    private CollaboratorDao() {}

    public static void findAll(final OperationListener<List<Collaborator>> listener) {
        FirebaseDao.findAll(Collaborator.class, COLLABORATORS_PATH, listener);
    }

    public static void findAllSingleEvent(final OperationListener<List<Collaborator>> listener) {
        FirebaseDao.findAllSingleEvent(Collaborator.class, COLLABORATORS_PATH, listener);
    }

    public static void saveOrUpdate(Collaborator collab, OperationListener<Collaborator> listener) {
        FirebaseDao.saveOrUpdate(collab, COLLABORATORS_PATH, listener);
    }

    public static void delete(String id, OperationListener<String> listener) {
        FirebaseDao.delete(id, COLLABORATORS_PATH, listener);
    }

    public static String uploadImage(byte[] data, final OperationListener<String> listener) {
        return FirebaseDao.uploadImage(data, COLLABORATORS_IMAGES_PATH, listener);
    }
}
