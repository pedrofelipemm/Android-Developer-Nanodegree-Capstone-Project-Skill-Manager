package study.pmoreira.skillmanager.data;

import com.google.android.gms.tasks.Task;

import study.pmoreira.skillmanager.infrastructure.OperationListener;

// make it static ?
public abstract class BaseDao {

//TODO: ?
//    public static void addUploadListeners(String uploadRef, final OperationListener<String> listener) {
//        FirebaseDao.addUploadListeners(uploadRef, listener);
//    }

    public void deleteImage(String url) {
        //TODO
        FirebaseDao.deleteImage(url, new OperationListener<Task<Void>>());
    }

}
