package study.pmoreira.skillmanager.data;

// make it static ?
public abstract class BaseDao {

//    public static void addUploadListeners(String uploadRef, final OperationListener<String> listener) {
//        FirebaseDao.addUploadListeners(uploadRef, listener);
//    }

    public void deleteImage(String url) {
        FirebaseDao.deleteImage(url);
    }

}
