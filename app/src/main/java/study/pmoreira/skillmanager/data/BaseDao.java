package study.pmoreira.skillmanager.data;

import com.google.android.gms.tasks.Task;

import study.pmoreira.skillmanager.infrastructure.OperationListener;

public abstract class BaseDao {

    public static void deleteImage(String url) {
        FirebaseDao.deleteImage(url, new OperationListener<Task<Void>>());
    }
}
